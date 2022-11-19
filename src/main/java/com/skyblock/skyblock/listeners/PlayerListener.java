package com.skyblock.skyblock.listeners;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Arrays;

public class PlayerListener implements Listener {

    private final Skyblock plugin;
    public PlayerListener(Skyblock skyblock) {
        plugin = skyblock;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SkyblockPlayer.registerPlayer(player.getUniqueId());

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (player.getItemInHand() != null) {
            skyblockPlayer.setHand(player.getItemInHand());
        }

        skyblockPlayer.tick();

        for (ItemStack item : player.getInventory().getArmorContents()) {
            skyblockPlayer.updateStats(null, item);
        }
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
        if (e.getEntity() instanceof Player){
            if (!e.isCancelled()) {
                SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) e.getEntity());
                double damage = e.getDamage();

                e.setDamage(0);

                player.damage(damage, e.getCause(), null);
            }
        }else{
            if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
                if (!e.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
                    Util.setDamageIndicator(e.getEntity().getLocation(), ChatColor.GRAY + "" + Math.round(e.getFinalDamage()));
                }
            }
        }
    }
}
