package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PigmanSword extends SkyblockItem {

    public PigmanSword() {
        super(plugin.getItemHandler().getItem("PIGMAN_SWORD.json"), "pigman_sword");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (player.isOnCooldown(getInternalName())) {
            player.sendOnCooldown(getInternalName());

            return;
        }

        if (!player.checkMana(400)) return;

        player.addStat(SkyblockStat.DEFENSE, 300);
        Util.delay(() -> player.subtractStat(SkyblockStat.DEFENSE, 300), 20 * 5);

        long damage = Math.round(Util.calculateAbilityDamage(30000, player.getStat(SkyblockStat.MANA), 0, 0));

        List<SkyblockEntity> targets = player.getBukkitPlayer().getNearbyEntities(7, 7, 7)
                .stream()
                .filter(e -> plugin.getEntityHandler().getEntity(e) != null)
                .map(e -> plugin.getEntityHandler().getEntity(e))
                .collect(Collectors.toList());

        Location firedAt = player.getBukkitPlayer().getLocation();

        for (SkyblockEntity target : targets) {
            ArmorStand stand = (ArmorStand) firedAt.getWorld().spawnEntity(firedAt, EntityType.ARMOR_STAND);
            stand.setSmall(true);
            stand.setArms(true);
            stand.setBasePlate(false);
            stand.setMarker(true);
            stand.setVisible(false);

            Vector position = stand.getLocation().toVector();
            Vector targetVector = target.getVanilla().getLocation().toVector();

            Vector velocity = targetVector.subtract(position).normalize().multiply(1);
            stand.setVelocity(velocity);

            new BukkitRunnable() {
                int ticksAlive = 0;

                @Override
                public void run() {
                    if (ticksAlive >= 20) {
                        stand.remove();
                        cancel();
                        return;
                    }

                    ticksAlive++;

                    if (stand.getLocation().distance(firedAt) > 7) {
                        stand.remove();
                        cancel();
                    }

                    ParticleEffect.FLAME.display(stand.getLocation().add(0, 1,  0), 0.1f, 0.1f, 0.1f, 0, 25, null);

                    if (stand.getLocation().distance(target.getVanilla().getLocation()) < 1) {
                        long dps = damage / 5;

                        new BukkitRunnable() {
                            int ticks = 0;
                            @Override
                            public void run() {
                                if (ticks > 5 || target.getVanilla().isDead()) {
                                    cancel();
                                    return;
                                }

                                target.damage(dps, player, true);
                                ticks++;
                            }
                        }.runTaskTimer(plugin, 0, 20);

                        stand.remove();
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        if (player.getCooldown(getInternalName())) {
            player.setCooldown(getInternalName(), 5);
        }

        Util.sendAbility(player, "Burning Souls", 400);
    }
}
