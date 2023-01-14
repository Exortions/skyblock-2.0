package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.Pair;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Function;

public class FireAspectEnchantment extends SwordEnchantment {

    private static final Function<Integer, Pair<Integer, Integer>> getStats = (level) -> {
        int time = level < 3 ? (2 + level) : 4;
        int percentOfDamage = level * 3;

        return Pair.of(time, percentOfDamage);
    };

    public FireAspectEnchantment() {
        super("fire_aspect", "Fire Aspect", (level) -> {
            Pair<Integer, Integer> stats = getStats.apply(level);

            return "&7Ignites your enemies for &a" + stats.getFirst() + "s&7,\n&7dealing &a" + stats.getSecond() + "%&7 of your damage\n&7per second.";
        }, 3);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (!e.getEntity().hasMetadata("skyblockEntityData")) return damage;

        SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());
        if (sentity == null) return damage;

        try {
            Util.delay(() -> {
                ItemBase base = new ItemBase(player.getBukkitPlayer().getItemInHand());
                int level = base.getEnchantment(this.getName()).getLevel();
                Pair<Integer, Integer> stats = getStats.apply(level);

                e.getEntity().setFireTicks(1 + (stats.getFirst() * 20));
                e.getEntity().setMetadata("should_take_fire_damage", new FixedMetadataValue(Skyblock.getPlugin(), false));

                new BukkitRunnable() {
                    int tick = 0;

                    @Override
                    public void run() {
                        if (tick >= stats.getFirst()) {
                            e.getEntity().removeMetadata("should_take_fire_damage", Skyblock.getPlugin());
                            this.cancel();
                            return;
                        }

                        tick++;

                        double percentOfDamage = (damage * stats.getSecond()) / 100F;

                        sentity.damage((long) percentOfDamage, player, false);
                    }
                }.runTaskTimer(Skyblock.getPlugin(), 0, 20);
            }, 10);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return damage;
        }

        return damage;
    }

    @EventHandler
    public void onEntityDamageByFire(EntityDamageEvent event) {
        if (event.getEntity() == null || !event.getEntity().hasMetadata("should_take_fire_damage")) return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
            if (event.getEntity().getMetadata("should_take_fire_damage").get(0).asBoolean()) event.setCancelled(true);
        }
    }
}
