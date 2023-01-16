package com.skyblock.skyblock.features.slayer.boss;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.slayer.SlayerBoss;
import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.utilities.Util;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSpider;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

@Getter
public class TarantulaBroodfather extends SlayerBoss {

    private final TopSpider top;

    public TarantulaBroodfather(Player spawner, Integer level) {
        super(EntityType.SPIDER, SlayerType.TARANTULA, spawner, level, -0.75);

        switch (level) {
            case 1:
                loadStats(750, 35, false, true, true, new Equipment(), "Tarantula Broodfather", 40, 50);
                break;
            case 2:
                loadStats(30000, 110, false, true, true, new Equipment(), "Tarantula Broodfather", 90, 100);
                break;
            case 3:
                loadStats(900000, 525, false, true, true, new Equipment(), "Tarantula Broodfather", 180, 200);
                break;
            case 4:
                loadStats(2400000, 1325, false, true, true, new Equipment(), "Tarantula Broodfather", 260, 500);
                break;
            default:
                break;
        }

        top = new TopSpider(this);
    }

    @Override
    protected void tick() {
        super.tick();

        if (tick == 0) {
            top.spawn(getVanilla().getLocation());
            getVanilla().setPassenger(top.getVanilla());
        }

        Spider spider = (Spider) getVanilla();

        spider.setTarget(getSpawner());

        EntitySpider nms = ((CraftSpider) spider).getHandle();

        nms.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.55);

        if (tick % 20 == 0 && getLevel() >= 2) {
            for (Entity entity : getVanilla().getNearbyEntities(5, 2, 5)) {
                if (entity instanceof Player) {
                    SkyblockPlayer.getPlayer((Player) entity).damage(getEntityData().damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getVanilla());
                }
            }
        }

        // https://github.com/superischroma/Spectaculation/blob/main/src/main/java/me/superischroma/spectaculation/entity/nms/TarantulaBroodfather.java
        if (tick % 40 == 0) {
            Entity e = getVanilla();
            if (e.getLocation().clone().distance(getSpawner().getLocation().clone()) < 5.0) return;
            if (e.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) return;
            Vector vector = e.getLocation().clone().toVector().subtract(getSpawner().getLocation().clone().toVector()).multiply(-1.0).multiply(new Vector(0.1, 0.2, 0.1));
            vector.setY(Math.abs(vector.getY()));
            if (vector.getY() < 0.8)
                vector.setY(1.5);
            if (vector.getY() > 5.0)
                vector.setY(5.0);
            e.setVelocity(e.getVelocity().add(vector));
        }
    }

    @Override
    protected void onDeath() {
        super.onDeath();

        top.getEntityData().health = -1;
    }

    public static class TopSpider extends SkyblockEntity {

        private final TarantulaBroodfather parent;

        public TopSpider(TarantulaBroodfather parent) {
            super(EntityType.CAVE_SPIDER);

            this.parent = parent;
            setEntityData(parent.getEntityData());
        }

        @Override
        protected void tick() {
            getVanilla().setCustomNameVisible(true);
            getVanilla().setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + parent.getEntityData().level + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "☠ " + ChatColor.WHITE + parent.getEntityData().entityName + " " + ChatColor.GREEN + Util.format(Math.max(0, parent.getEntityData().health)) + ChatColor.DARK_GRAY + "/" + ChatColor.GREEN + Util.format(parent.getEntityData().maximumHealth) + ChatColor.RED + "❤");
            parent.getVanilla().setCustomNameVisible(false);

            CaveSpider spider = (CaveSpider) getVanilla();
            spider.setTarget(parent.getSpawner());
        }

        @Override
        public void onDamage(EntityDamageByEntityEvent event, SkyblockPlayer player, boolean crit, double damage) {
            event.setCancelled(true);

            setHealth(getEntityData().maximumHealth);

            ((LivingEntity) parent.getVanilla()).damage(0.0, event.getDamager());
        }
    }
}
