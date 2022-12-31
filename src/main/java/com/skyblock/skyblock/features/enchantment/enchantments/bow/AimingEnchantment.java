package com.skyblock.skyblock.features.enchantment.enchantments.bow;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.enchantment.types.BowEnchantment;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.List;

public class AimingEnchantment extends BowEnchantment {

    public AimingEnchantment() {
        super("aiming", "Aiming", (level) -> {
            return "&7Arrows home towards enemies if they are within " + level * 2 + " blocks";
        }, 5);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (Util.isNotSkyblockItem(event.getBow())) return;
        Player player = (Player) (event.getEntity());
        ItemBase base = new ItemBase(event.getBow());

        if (!base.hasEnchantment(this)) return;

        int level = base.getEnchantment(this.name).getLevel();
        int radius = level * 2;
        Arrow arrow = (Arrow) event.getProjectile();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!arrow.isOnGround()) {
                    List<Entity> ents = arrow.getNearbyEntities(radius, radius, radius);
                    Comparator<Entity> comp = Comparator.comparingDouble(e -> player.getLocation().distance(((Entity) e).getLocation()));
                    ents.sort(comp);
                    for (Entity en : ents) {
                        if (en instanceof Monster || en instanceof Animals) {
                            en.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize();
                            arrow.setVelocity(en.getLocation().add(0, 1, 0).toVector().subtract(arrow.getLocation().toVector()).multiply(event.getForce() * 0.5));
                            return;
                        }
                    }
                } else {
                    arrow.remove();
                }
            }
        }.runTaskTimer(Skyblock.getPlugin(), 0, 1);
    }
}
