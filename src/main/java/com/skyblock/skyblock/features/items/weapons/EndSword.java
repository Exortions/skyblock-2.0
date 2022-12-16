package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EndSword extends SkyblockItem {

    public EndSword() {
        super(plugin.getItemHandler().getItem("END_SWORD.json"), "end_sword");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (e.getEntityType().equals(EntityType.ENDERMAN) ||
            e.getEntityType().equals(EntityType.ENDERMITE) ||
            e.getEntityType().equals(EntityType.ENDER_DRAGON)) return damage + damage;

        return damage;
    }
}
