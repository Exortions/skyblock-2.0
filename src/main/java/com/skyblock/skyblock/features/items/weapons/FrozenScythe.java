package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FrozenScythe extends SkyblockItem {

    public FrozenScythe() {
        super(plugin.getItemHandler().getItem("FROZEN_SCYTHE.json"), "frozen_scythe");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (!player.checkMana(50)) return;

        List<SkyblockEntity> entities = player.getBukkitPlayer().getNearbyEntities(100, 100, 100).stream()
                        .filter(entity -> plugin.getEntityHandler().getEntity(entity) != null)
                        .map(entity -> plugin.getEntityHandler().getEntity(entity))
                        .collect(Collectors.toList());

        Location firedAt = player.getBukkitPlayer().getLocation().add(0, 1, 0);

        ArmorStand bolt = player.getBukkitPlayer().getWorld().spawn(player.getBukkitPlayer().getLocation().add(0, 1, 0), ArmorStand.class);
        bolt.setGravity(true);
//        bolt.setVisible(false);
        bolt.setMarker(true);
        bolt.setSmall(true);

        bolt.setHelmet(new ItemStack(Material.ICE));

        Vector target = player.getBukkitPlayer().getLocation().add(player.getBukkitPlayer().getLocation().getDirection().multiply(100)).toVector();

        player.getBukkitPlayer().sendMessage("x: " + target.getBlockX() + " y: " + target.getBlockY() + " z: " + target.getBlockZ());

        new BukkitRunnable() {
            boolean hit = false;

            @Override
            public void run() {
                if (bolt.isDead()) {
                    cancel();
                    return;
                }

                if (!hit) {
                    Vector speed = target.subtract(bolt.getLocation().toVector()).normalize().multiply(1);

                    bolt.setVelocity(speed);
                }

                ParticleEffect.CLOUD.display(bolt.getLocation(), 0, 0, 0, 0, 25, null);

                if (bolt.getLocation().distance(firedAt) > 100) {
                    hit = true;
                    Util.delay(() -> {
                        bolt.remove();
                        cancel();
                    }, 60);
                    return;
                }
            }
        }.runTaskTimer(plugin, 0, 1);

        Util.sendAbility(player, "Ice Bolt", 50);
    }
}
