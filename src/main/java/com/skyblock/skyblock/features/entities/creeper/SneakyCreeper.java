package com.skyblock.skyblock.features.entities.creeper;

import com.skyblock.skyblock.features.entities.EntityDrop;
import com.skyblock.skyblock.features.entities.EntityDropRarity;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SneakyCreeper extends SkyblockEntity {
    public SneakyCreeper() {
        super(EntityType.CREEPER);
        loadStats(120, 80, false, false, true, new Equipment(), "Sneaky Creeper", 3, 8);
    }

    @Override
    public List<EntityDrop> getDrops() {
        List<EntityDrop> drops = new ArrayList<>(Arrays.asList(new EntityDrop(handler.getItem("GUNPOWDER.json"))));
        return drops;
    }

    @Override
    protected void tick() {
        ((CraftEntity) getVanilla()).getHandle().setInvisible(((Creeper) getVanilla()).getTarget() == null);
    }
}
