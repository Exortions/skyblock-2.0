package com.skyblock.skyblock.features.entities.spider;

import com.skyblock.skyblock.features.entities.SkyblockEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSkeleton;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Spider extends SkyblockEntity {

    private final SpiderType type;
    private Skeleton jockey;

    public Spider(String type) {
        super(EntityType.SPIDER);

        this.type = SpiderType.valueOf(type);

        switch (this.type){
            case SPLITTER_SPIDER_MEDIUM:
                loadStats(180, 30, false, true, true, new Equipment(), "Splitter Spider", 2, 9, "weak_splitter_spider");
                break;
            case SPLITTER_SPIDER_INTERMEDIATE:
                loadStats(9000, 850, false, true, true, new Equipment(), "Splitter Spider", 50, 36, "strong_splitter_spider");
                break;
            case DASHER_SPIDER_EASY:
                loadStats(170, 55, false, true, true, new Equipment(), "Dasher Spider", 4, 10, "2coin_spider");
                break;
            case DASHER_SPIDER_INTERMEDIATE:
                loadStats(9000, 650, false, true, true, new Equipment(), "Dasher Spider", 50, 36, "3coin_spider");
                break;
            case WEAVER_SPIDER:
                loadStats(9100, 650, false, true, true, new Equipment(), "Weaver Spider", 50, 36, "3coin_spider");
                break;
            case SPIDER_JOCKEY:
                loadStats(7000, 550, false, true, true, new Equipment(), "Spider Jockey", 50, 9, "jockey_spider");
                break;
            case VORACIOUS_SPIDER:
                loadStats(9000, 700, false, true, true, new Equipment(), "Voracious Spider", 50, 36, "3coin_spider");
                break;
            case TARANTULA_BROOD_MOTHER:
                loadStats(6000, 250, false, true, true, new Equipment(), "Tarantula Brood Mother", 12, 25, "brood_mother");
                break;
            default:
                break;
        }
    }

    @Override
    protected void tick() {
        if (type.equals(SpiderType.SPIDER_JOCKEY)) {
            if (jockey == null) jockey = getVanilla().getWorld().spawn(getVanilla().getLocation(), Skeleton.class);

            getVanilla().setPassenger(jockey);
            jockey.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 5, true, false));
            ((CraftSkeleton) jockey).getHandle().ai = false;

            jockey.setCustomNameVisible(true);
            jockey.setCustomName(getVanilla().getCustomName());
            getVanilla().setCustomNameVisible(false);
        }
    }

    @Override
    protected void onDeath() {
        super.onDeath();

        if (jockey != null) jockey.remove();
    }
}
