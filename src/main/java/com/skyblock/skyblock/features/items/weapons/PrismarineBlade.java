package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PrismarineBlade extends SkyblockItem {

    public PrismarineBlade() {
        super(plugin.getItemHandler().getItem("PRISMARINE_BLADE.json"), "prismarine_blade");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        return player.getBukkitPlayer().getLocation().getBlock().isLiquid() ? damage * 3 : damage;
    }
}
