package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SpiderSword extends SkyblockItem {

    public SpiderSword() {
        super(plugin.getItemHandler().getItem("SPIDER_SWORD.json"), "spider_sword");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());

        if (sentity == null) return damage;

        if (sentity.getVanilla().getType().equals(EntityType.CAVE_SPIDER) ||
            sentity.getVanilla().getType().equals(EntityType.SPIDER)) return damage * 2;

        return damage;
    }
}
