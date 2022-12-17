package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class UndeadSword extends SkyblockItem {

    public UndeadSword(Skyblock plugin) {
        super(plugin.getItemHandler().getItem("UNDEAD_SWORD.json"), "undead_sword");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());

        if (sentity == null) return damage;

        if (sentity.getVanilla().getType().equals(EntityType.ZOMBIE) ||
            sentity.getVanilla().getType().equals(EntityType.SKELETON) ||
            sentity.getVanilla().getType().equals(EntityType.PIG_ZOMBIE)) return damage * 2;

        return damage;
    }
}
