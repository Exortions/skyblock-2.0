package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class Falchion extends SkyblockItem {

    protected final int falchionDamage;

    protected Falchion(String name, int damage) {
        super(plugin.getItemHandler().getItem(name.toUpperCase() + "_SWORD.json"), name.toLowerCase() + "_falchion");

        this.falchionDamage = damage;
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        SkyblockEntity entity = plugin.getEntityHandler().getEntity(e.getEntity());

        return entity == null || !entity.getVanilla().getType().equals(EntityType.ZOMBIE) ? damage : damage * (falchionDamage / 100.0);
    }

}
