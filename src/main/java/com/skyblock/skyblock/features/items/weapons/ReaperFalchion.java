package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ReaperFalchion extends Falchion {

    public ReaperFalchion() {
        super("reaper", 250);
    }

    @Override
    public void onEntityDamage(SkyblockPlayerDamageEntityEvent event) {
        event.getPlayer().addStat(SkyblockStat.HEALTH, 10);
    }

    @Override
    public double getModifiedIncomingDamage(SkyblockPlayer player, EntityDamageEvent event, double damage) {
        return damage * 0.8;
    }

}
