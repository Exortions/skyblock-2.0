package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PoochSword extends SkyblockItem {

    public PoochSword() {
        super(plugin.getItemHandler().getItem("POOCH_SWORD.json"), "pooch_sword");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        double playerMaxHealth = player.getStat(SkyblockStat.MAX_HEALTH);

        return damage + (playerMaxHealth / 50);
    }

    @Override
    public void onEntityDamage(SkyblockPlayerDamageEntityEvent event) {
        if (!event.getEntity().getEntityType().equals(EntityType.WOLF)) return;

        event.getPlayer().addStat(SkyblockStat.STRENGTH, 150);

        Util.delay(() -> event.getPlayer().subtractStat(SkyblockStat.STRENGTH, 15), 1);
    }

    @Override
    public double getModifiedIncomingDamage(SkyblockPlayer player, EntityDamageEvent event, double damage) {
        if (event.getEntity().getType().equals(EntityType.WOLF)) return damage * 0.8;

        return damage;
    }
}
