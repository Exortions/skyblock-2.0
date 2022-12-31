package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class FlamingSword extends SkyblockItem {
    public FlamingSword() {
        super(plugin.getItemHandler().getItem("FLAMING_SWORD.json"), "flaming_sword");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (!e.getEntity().hasMetadata("skyblockEntityData")) return damage;

        SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());
        if (sentity == null) return damage;

        try {
            e.getEntity().setFireTicks(1 + (7 * 20));
            e.getEntity().setMetadata("should_take_fire_damage", new FixedMetadataValue(Skyblock.getPlugin(), false));

            new BukkitRunnable() {
                int tick = 0;

                @Override
                public void run() {
                    if (tick >= 7) {
                        e.getEntity().removeMetadata("should_take_fire_damage", Skyblock.getPlugin());
                        this.cancel();
                        return;
                    }

                    tick++;

                    double percentOfDamage = (damage * 10) / 100F;

                    sentity.damage((long) percentOfDamage, player, false);
                }
            }.runTaskTimer(Skyblock.getPlugin(), 0, 20);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return damage;
        }

        return damage;
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() == null || !event.getEntity().hasMetadata("should_take_fire_damage")) return;

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) && event.getEntity().getMetadata("should_take_fire_damage").get(0).asBoolean())
            event.setCancelled(true);
    }
}
