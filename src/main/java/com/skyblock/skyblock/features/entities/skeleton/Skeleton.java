package com.skyblock.skyblock.features.entities.skeleton;

import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Skeleton extends SkyblockEntity {

    private enum SkeletonType {
        DIAMOND_RESERVE,
        OBSIDIAN_SANCTUARY
    }

    public Skeleton(String type) {
        super(EntityType.SKELETON);

        switch (SkeletonType.valueOf(type)) {
            case DIAMOND_RESERVE:
                loadStats(250, 150, true, false, true, new Equipment(new ItemStack(Material.DIAMOND_HELMET),
                        new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.BOW)), "Skeleton", 15, 20, "diamond_skeleton");
                break;
            case OBSIDIAN_SANCTUARY:
                loadStats(300, 200, true, false, true, new Equipment(new ItemBuilder(Material.DIAMOND_BLOCK).addEnchantmentGlint().toItemStack(),
                        new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack(), new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchantmentGlint().toItemStack(), new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantmentGlint().toItemStack(),
                        new ItemBuilder(Material.BOW).addEnchantmentGlint().toItemStack()), "Skeleton", 15, 20, "obsidian_skeleton");
                break;
        }
    }
}
