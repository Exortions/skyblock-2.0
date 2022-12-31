package com.skyblock.skyblock.features.entities.pigman;

import com.skyblock.skyblock.features.entities.SkyblockEntity;
import org.bukkit.entity.EntityType;

public class RedstonePigman extends SkyblockEntity {
    public RedstonePigman() {
        super(EntityType.PIG_ZOMBIE);
        loadStats(250, 75, false, false, false, new Equipment(), "Redstone Pigman", 10, 20, "zombie_pigman");
    }
}
