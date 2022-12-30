package com.skyblock.skyblock.features.enchantment.enchantments.bow;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.BowEnchantment;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;

import net.citizensnpcs.api.jnbt.ListTag;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class AimingEnchantment extends BowEnchantment {
    private final int angleChange = 15;

    public AimingEnchantment() {
        super("aiming", "Aiming", (level) -> { return "&7Arrows home towards enemies if they are within " + level * 2 + " blocks"; }, 5);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (Util.isNotSkyblockItem(e.getBow())) return;
        Player player = (Player) (e.getEntity());
        ItemBase b = new ItemBase(e.getBow());
        if (!b.hasEnchantment(this)) {
            return;
        }
        else {
            int level = b.getEnchantment(this.name).getLevel();
            int radius = level * 2;
            Arrow arrow = (Arrow) e.getProjectile();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!arrow.isOnGround()) {
                        int shortestDistance = 100;
                        Entity shortestEntity = null;
                        List<Entity> ents = arrow.getNearbyEntities(radius, radius, radius);
                        Comparator<Entity> comp = (e1, e2) -> Double.compare(player.getLocation().distance(((Entity) e1).getLocation()),
                                                                player.getLocation().distance(((Entity) e2).getLocation()));
                        Collections.sort(ents, comp);
                        for (Entity en : ents) {
                            if (en instanceof Monster || en instanceof Animals) {
                                Vector vec = en.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize();
                                arrow.setVelocity(en.getLocation().add(0, 1, 0).toVector().subtract(arrow.getLocation().toVector()).multiply(e.getForce() * 0.5));
                                return; 
                            }
                        }
                    }
                    else {
                        arrow.remove();
                    }
                }
            }.runTaskTimer(Skyblock.getPlugin(), 0, 1);            
        }
    }
}
