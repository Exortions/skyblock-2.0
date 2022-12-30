package com.skyblock.skyblock.features.entities.skeleton;

import com.skyblock.skyblock.features.entities.EntityDrop;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.entities.zombie.ZombieType;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Skeleton extends SkyblockEntity {

    private enum SkeletonType {
        DIAMOND_RESERVE_SKELETON,
        OBSIDIAN_SANCTUARY_SKELETON
    }

    private SkeletonType type;

    public Skeleton(String type) {
        super(EntityType.SKELETON);

        this.type = SkeletonType.valueOf(type);

        switch (SkeletonType.valueOf(type)) {
            case DIAMOND_RESERVE_SKELETON:
                loadStats(250, 150, true, false, true, new Equipment(new ItemStack(Material.DIAMOND_HELMET),
                        new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.BOW)), "Skeleton", 15, 20, "diamond_skeleton");
                break;
            case OBSIDIAN_SANCTUARY_SKELETON:
                loadStats(300, 200, true, false, true, new Equipment(new ItemBuilder(Material.DIAMOND_BLOCK).addEnchantmentGlint().toItemStack(),
                        new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack(), new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchantmentGlint().toItemStack(), new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantmentGlint().toItemStack(),
                        new ItemBuilder(Material.BOW).addEnchantmentGlint().toItemStack()), "Skeleton", 15, 20, "obsidian_skeleton");
                break;
        }
    }

    @Override
    public List<EntityDrop> getRareDrops() {
        List<EntityDrop> drops = new ArrayList<>();

        if (type.equals(SkeletonType.DIAMOND_RESERVE_SKELETON) || type.equals(SkeletonType.OBSIDIAN_SANCTUARY_SKELETON)) drops.addAll(Arrays.asList(new EntityDrop("TANK_MINER_HELMET", 1, 1), new EntityDrop("TANK_MINER_CHESTPLATE", 1, 1), new EntityDrop("TANK_MINER_LEGGINGS", 1, 1), new EntityDrop("TANK_MINER_BOOTS", 1, 1)));


        return drops;
    }
}
