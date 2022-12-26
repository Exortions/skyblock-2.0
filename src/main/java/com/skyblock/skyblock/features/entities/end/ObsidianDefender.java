package com.skyblock.skyblock.features.entities.end;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

public class ObsidianDefender extends SkyblockEntity {
    public ObsidianDefender() {
        super(EntityType.SKELETON);

        loadStats(10000, 200, true, false, true,
                new Equipment(new ItemStack(Material.OBSIDIAN), new ItemBuilder(Material.LEATHER_CHESTPLATE).dyeColor(Color.BLACK).toItemStack()
                        , null, null, null), "Obsidian Defender", 55, 40, "obsidian_defender");
    }

    @Override
    protected void tick() {
        Skeleton skeleton = (Skeleton) getVanilla();
        skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
        skeleton.getEquipment().setItemInHand(null);
    }
}
