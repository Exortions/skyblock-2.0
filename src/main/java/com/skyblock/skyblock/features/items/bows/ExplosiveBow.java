package com.skyblock.skyblock.features.items.bows;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ExplosiveBow extends SkyblockItem {
    public ExplosiveBow() {
        super(plugin.getItemHandler().getItem("EXPLOSIVE_BOW.json"), "explosive_bow");
    }

    @Override
    public void onBowShoot(EntityShootBowEvent event) {
        markExplosive((Arrow) event.getProjectile(), SkyblockPlayer.getPlayer((Player) event.getEntity()));
    }

    private void markExplosive(Arrow arrow, SkyblockPlayer skyblockPlayer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isDead() || arrow.isOnGround()) {
                    arrow.getWorld().playEffect(arrow.getLocation(), Effect.EXPLOSION_HUGE, 10);

                    for (Player p : Bukkit.getOnlinePlayers()) p.playSound(arrow.getLocation(), Sound.EXPLODE, 10, 1);

                    for (Entity entity : arrow.getNearbyEntities(3.5, 3.5, 3.5)) {
                        SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(entity);

                        if (sentity == null) continue;

                        Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(skyblockPlayer.getBukkitPlayer(), entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 0));
                    }

                    arrow.remove();
                    cancel();
                }
            }
        }.runTaskTimer(Skyblock.getPlugin(), 1, 1);
    }
}
