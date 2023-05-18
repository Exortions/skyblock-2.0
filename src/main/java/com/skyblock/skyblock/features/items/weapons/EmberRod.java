package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class EmberRod extends SkyblockItem {

    public EmberRod() {
        super(plugin.getItemHandler().getItem("EMBER_ROD.json"), "ember_rod");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        Player player = event.getPlayer();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.isOnCooldown(getInternalName())) {
            skyblockPlayer.sendOnCooldown(getInternalName());

            return;
        }

        if (skyblockPlayer.checkMana(150)) {
            Util.sendAbility(skyblockPlayer, "Fire Blast", 150);

            for (int i = 0; i < 3; i++) {
                Util.delay(() -> {
                    Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(2)).toLocation(player.getWorld(),
                            player.getLocation().getYaw(),
                            player.getLocation().getPitch());

                    Fireball fireball = player.getWorld().spawn(loc, Fireball.class);
                    fireball.setShooter(player);
                    fireball.setIsIncendiary(false);
                    fireball.setYield(0f);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!fireball.isDead()) return;

                            for (Entity en : fireball.getNearbyEntities(2, 2, 2)) {
                                Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(player, en, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 0));
                            }

                            cancel();
                        }
                    }.runTaskTimer(plugin, 5L, 5L);
                }, i * 15);
            }
            skyblockPlayer.setCooldown(getInternalName(), 30);
        }
    }
}
