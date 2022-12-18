package com.skyblock.skyblock.listeners;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import com.sk89q.worldedit.event.platform.BlockInteractEvent;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.enchantment.ItemEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.launchpads.LaunchPadHandler;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import static org.bukkit.Material.*;

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

        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) &&
            !e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
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
                        if (sentity == null) return;
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
            double damage = 5 + player.getStat(SkyblockStat.DAMAGE) + (player.getStat(SkyblockStat.STRENGTH) / 5F) * (1 + player.getStat(SkyblockStat.STRENGTH) / 100F);
            boolean crit = player.crit();

            for (BiFunction<SkyblockPlayer, Entity, Integer> func : player.getPredicateDamageModifiers()) {
                if (func == null) return;

                damage += (damage * func.apply(player, e.getEntity()) / 100);
            }

            double combat = 4 * Skill.getLevel((double) player.getValue("skill.combat.exp"));

            damage += damage * (combat / 100F);

            try {
                ItemBase base = new ItemBase(p.getItemInHand());

                for (ItemEnchantment ench : base.getEnchantments()) {
                    damage = ench.getBaseEnchantment().getModifiedDamage(player, e, damage);
                }
            } catch (Exception ignored) {}

            if (Util.notNull(p.getItemInHand())) {
                if (plugin.getSkyblockItemHandler().isRegistered(p.getItemInHand())) {
                    damage = plugin.getSkyblockItemHandler().getRegistered(p.getItemInHand()).getModifiedDamage(player, e, damage);
                }
            }

            if (player.getArmorSet() != null) {
                damage = player.getArmorSet().getModifiedDamage(player, e, damage);
            }

            double display = damage;

            if (e.getEntity().hasMetadata("skyblockEntityData")) {
                SkyblockEntity sentity = plugin.getEntityHandler().getEntity(e.getEntity());
                if (sentity == null) return;

                sentity.setLifeSpan(sentity.getLifeSpan() + 15 * 20);
                sentity.setLastDamager(player);

                if (crit) {
                    damage = (damage * (1 + player.getStat(SkyblockStat.CRIT_DAMAGE) / 100F)) / sentity.getEntityData().maximumHealth;
                } else {
                    damage = damage / sentity.getEntityData().maximumHealth;
                }

                display = damage * sentity.getEntityData().maximumHealth;

                sentity.onDamage(e, player, crit, display);

                sentity.getEntityData().health = (long) (sentity.getEntityData().health - display);
            } else {
                if (!e.getEntity().getType().equals(EntityType.ARMOR_STAND) && crit) {
                    damage = damage * (1 + player.getStat(SkyblockStat.CRIT_DAMAGE) / 100F);
                }
            }

            if (player.getExtraData("cleave_enchantment") != null) {
                float perc = (float) player.getExtraData("cleave_enchantment");
                damage = damage * perc;
                display = display * perc;
            }

            e.setDamage(damage);

            try {
                ItemBase base = new ItemBase(p.getItemInHand());

                for (ItemEnchantment ench : base.getEnchantments()) {
                    ench.getBaseEnchantment().onDamage(player, e, damage);
                }
            } catch (Exception ignored) {}

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
        } else if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            Entity entity = (Entity) arrow.getShooter();

            Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(entity, e.getEntity(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, e.getDamage()));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location to = e.getTo();
        Player player = e.getPlayer();
        Location bottom = new Location(to.getWorld(), to.getX(), to.getY() - 1, to.getZ());

        if (bottom.getBlock().getType().equals(Material.SLIME_BLOCK)) {
            LaunchPadHandler padHandler = plugin.getLaunchPadHandler();
            String pad = padHandler.closeTo(player);
            if (!pad.equals("NONE")) {
                padHandler.launch(player, pad);
            }
        } else if (bottom.getBlock().getType().equals(ENDER_PORTAL)) {
            player.performCommand("sb warp home");
        } else if (to.getBlock().getType().equals(PORTAL)) {
            player.performCommand("sb warp hub");
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
        if (player.getArmorSet() != null) {
            player.getArmorSet().stopFullSetBonus(player.getBukkitPlayer());
        }

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

    @EventHandler
    public void onUpdate(BlockPhysicsEvent e){
        if (e.getChangedType().equals(AIR)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
//        if (e.getClickedInventory() == null) return;
//        if (!e.getClickedInventory().equals(e.getWhoClicked().getInventory())) return;
//
//        try {
//            ItemBase base = new ItemBase(e.getCurrentItem());
//        } catch (Exception ex) {
//            e.getWhoClicked().setItemOnCursor(Util.toSkyblockItem(e.getCurrentItem()));
//        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();

        if (item.getItemMeta().hasDisplayName()) {
            String name = item.getItemMeta().getDisplayName();
            if (name.startsWith("coins_")) {
                try {
                    SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());
                    int amount = Integer.parseInt(name.split("_")[1]);

                    player.addCoins(amount);
                    player.getBukkitPlayer().playSound(e.getItem().getLocation(), Sound.ORB_PICKUP, 10, 2);
                    player.setExtraData("lastpicked_coins", amount);

                    e.setCancelled(true);
                    e.getItem().remove();

                    Util.delay(() -> {
                        player.setExtraData("lastpicked_coins", null);
                    }, 80);

                    return;
                } catch (NumberFormatException ignored) { }
            }
        }

        try {
            ItemBase base = new ItemBase(item);
        } catch (Exception ex) {
            e.getItem().setItemStack(Util.toSkyblockItem(item));
        }
    }

    @EventHandler
    public void onCreative(InventoryCreativeEvent e) {
        e.setCursor(Util.toSkyblockItem(e.getCursor()));
    }

    @EventHandler
    public void onOpen(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(WORKBENCH)) {
                e.setCancelled(true);
                e.getPlayer().performCommand("sb craft");
            }
        }
    }
}
