package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class RevenantFalchion extends SkyblockItem {

    public RevenantFalchion() {
        super(plugin.getItemHandler().getItem("REVENANT_SWORD.json"), "revenant_falchion");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        SkyblockEntity entity = plugin.getEntityHandler().getEntity(e.getEntity());

        return entity == null || !entity.getVanilla().getType().equals(EntityType.ZOMBIE) ? damage : damage * 2.5;
    }
}
