package com.skyblock.skyblock.listeners;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.launchpads.LaunchPadHandler;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class PlayerListener implements Listener {

    private final Skyblock plugin;
    public PlayerListener(Skyblock skyblock) {
        plugin = skyblock;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (!event.getFrom().getName().startsWith(IslandManager.ISLAND_PREFIX)) {
            SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

            if (player == null) return;

            this.plugin.getMinionHandler().reloadPlayer(player, false);

            return;
        }

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (player == null) return;

        this.plugin.getMinionHandler().deleteAll(player.getBukkitPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SkyblockPlayer.registerPlayer(player.getUniqueId(), e, (skyblockPlayer) -> {
            if (player.getItemInHand() != null) {
                skyblockPlayer.setHand(player.getItemInHand());
            }

            skyblockPlayer.tick();

            this.plugin.getMinionHandler().reloadPlayer(skyblockPlayer, false);

            for (ItemStack item : player.getInventory().getArmorContents()) {
                skyblockPlayer.updateStats(null, item);
            }

            Util.delay(() -> {
                if (!Skyblock.getPlugin().getFairySoulHandler().initialized) Skyblock.getPlugin().getFairySoulHandler().init();
            }, 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()){
                        skyblockPlayer.tick();
                    }else{
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 5L, 1);
        });
    }

    @EventHandler
    public void onArmorEquip(PlayerArmorEquipEvent e){
        SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());
        player.updateStats(e.getItemStack(), null);
    }

    @EventHandler
    public void onArmorUnEquip(PlayerArmorUnequipEvent e){
        if (!Arrays.asList(e.getPlayer().getInventory().getArmorContents()).contains(e.getItemStack())) {
            SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());
            player.updateStats(null, e.getItemStack());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if (e.getEntity().hasMetadata("merchant")) return;

        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            if (e.getEntity() instanceof Player){
                if (!e.isCancelled()) {
                    SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) e.getEntity());
                    double damage = e.getDamage();

                    e.setDamage(0);

                    player.damage(damage, e.getCause(), null);
                }
            } else {
                if (!e.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
                    if (e.getEntity().hasMetadata("skyblockEntityData")) {
                        SkyblockEntity sentity = plugin.getEntityHandler().getEntity(e.getEntity());
                        int damage = (int) (e.getFinalDamage() / sentity.getEntityData().maximumHealth);

                        sentity.getEntityData().health = sentity.getEntityData().health - damage;
                    }

                    Util.setDamageIndicator(e.getEntity().getLocation(), ChatColor.GRAY + "" + Math.round(e.getFinalDamage()), true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ArmorStand || e.getEntity().hasMetadata("merchant")) return;

        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();

            SkyblockPlayer player = SkyblockPlayer.getPlayer(p);
            double damage = 5 + player.getStat((SkyblockStat.DAMAGE)) + (player.getStat(SkyblockStat.STRENGTH) / 5F) * (1 + player.getStat(SkyblockStat.STRENGTH) / 100F);
            double display = damage;
            boolean crit = player.crit();

            for (BiFunction<SkyblockPlayer, Entity, Integer> func : player.getPredicateDamageModifiers()) {
                if (func == null) return;

                damage += (damage * func.apply(player, e.getEntity()) / 100);
            }

            if (e.getEntity().hasMetadata("skyblockEntityData")) {
                SkyblockEntity sentity = plugin.getEntityHandler().getEntity(e.getEntity());

                sentity.setLifeSpan(sentity.getLifeSpan() + 15 * 20);
                sentity.setLastDamager(player);

                if (crit) {
                    damage = (damage * ((100 + player.getStat(SkyblockStat.CRIT_DAMAGE))) / 100) / sentity.getEntityData().maximumHealth;
                } else {
                    damage = damage / sentity.getEntityData().maximumHealth;
                }

                display = damage * sentity.getEntityData().maximumHealth;

                sentity.onDamage(e, player, crit, display);

                sentity.getEntityData().health = (long) (sentity.getEntityData().health - display);
            } else {
                if (!e.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
                    if (crit) {
                        damage = damage * ((100 + player.getStat(SkyblockStat.CRIT_DAMAGE))) / 100;
                    }
                }
            }

            e.setDamage(damage);

            if (crit) {
                Util.setDamageIndicator(e.getEntity().getLocation(), Util.addCritTexture((int) Math.round(display)), false);
            } else {
                Util.setDamageIndicator(e.getEntity().getLocation(), ChatColor.GRAY + "" + Math.round(display), true);
            }
        } else if (e.getDamager().hasMetadata("skyblockEntityData")) {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                SkyblockPlayer player = SkyblockPlayer.getPlayer(p);

                SkyblockEntity sentity = plugin.getEntityHandler().getEntity(e.getDamager());
                sentity.setLifeSpan(sentity.getLifeSpan() + 15 * 20);

                player.damage(sentity.getEntityData().damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, sentity.getVanilla());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location to = e.getTo();
        Player player = e.getPlayer();
        if (new Location(to.getWorld(), to.getX(), to.getY() - 1, to.getZ())
                .getBlock().getType().equals(Material.SLIME_BLOCK)) {
            LaunchPadHandler padHandler = plugin.getLaunchPadHandler();
            String pad = padHandler.closeTo(player);
            if (!pad.equals("NONE")) {
                padHandler.launch(player, pad);
            }
        }

        Skyblock.getPlugin().getFairySoulHandler().loadChunk(to.getChunk());
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent e) {
        if (e.getEntityType().equals(EntityType.ENDERMAN)) e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());

        if (player.getPetDisplay() != null) player.getPetDisplay().remove();

        SkyblockPlayer.playerRegistry.remove(player.getBukkitPlayer().getUniqueId());
    }

    @EventHandler
    public void onArmorStand(PlayerArmorStandManipulateEvent e) {
        if (e.getRightClicked().hasMetadata("isFairySoul")) {
            e.setCancelled(true);

            SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());
            ArrayList<Location> found = (ArrayList<Location>) player.getValue("fairySouls.found");

            if (!found.contains(e.getRightClicked().getLocation())) {
                e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "SOUL! " + ChatColor.RESET + "" + ChatColor.WHITE + "You found a " + ChatColor.LIGHT_PURPLE + "Fairy Soul" + ChatColor.WHITE + "!");
                found.add(e.getRightClicked().getLocation());
                player.setValue("fairySouls.found", found);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.AMBIENCE_CAVE, 10, 2);
            } else {
                e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "You have already found that Fairy Soul!");
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.SILVERFISH_KILL, 10, 2);
            }
        }
    }
}
