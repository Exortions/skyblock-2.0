package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ShamanSword extends SkyblockItem {

    public ShamanSword() {
        super(plugin.getItemHandler().getItem("SHAMAN_SWORD.json"), "shaman_sword");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        return damage + (player.getStat(SkyblockStat.MAX_HEALTH) / 50f);
    }

    @Override
    public double getModifiedIncomingDamage(SkyblockPlayer player, EntityDamageEvent event, double damage) {
        return event.getEntity().getType().equals(EntityType.WOLF) ? damage * 0.8f : damage;
    }

}
