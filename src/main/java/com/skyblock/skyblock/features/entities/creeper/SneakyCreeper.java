package com.skyblock.skyblock.features.entities.creeper;

import com.skyblock.skyblock.features.entities.SkyblockEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;

public class SneakyCreeper extends SkyblockEntity {
    public SneakyCreeper() {
        super(EntityType.CREEPER);
        loadStats(120, 80, false, false, true, new Equipment(), "Sneaky Creeper", 3, 8, "creeper");
    }

    @Override
    protected void tick() {
        ((CraftEntity) getVanilla()).getHandle().setInvisible(((Creeper) getVanilla()).getTarget() == null);
    }
}
