package com.skyblock.skyblock.features.minions.types;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.minions.MiningMinion;
import com.skyblock.skyblock.utilities.Util;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class RedstoneMinion extends MiningMinion {
    public RedstoneMinion() {
        this(UUID.randomUUID());
    }
    public RedstoneMinion(UUID uuid) {
        super(uuid, "Redstone", "mining", Color.fromRGB(129, 47, 9), Material.REDSTONE_ORE);

        this.plugin = Skyblock.getPlugin();
    }

    @Override
    public ItemStack getHand(int level) {
        return new ItemStack(Material.STONE_PICKAXE, 1);
    }

    @Override
    public int getActionDelay(int level) {
        switch (level) {
            case 1:
            case 2:
                return 29;
            case 3:
            case 4:
                return 27;
            case 5:
            case 6:
                return 25;
            case 7:
            case 8:
                return 23;
            case 9:
            case 10:
                return 21;
            case 11:
                return 18;
            default:
                return 18;
        }
    }

    @Override
    public int getMaxStorage(int level) {
        switch (level) {
            case 1:
                return 1;
            case 2:
            case 3:
                return 3;
            case 4:
            case 5:
                return 6;
            case 6:
            case 7:
                return 9;
            case 8:
            case 9:
                return 12;
            default:
                return 15;
        }
    }

    @Override
    public ArrayList<ItemStack> calculateDrops(int level) {
        return new ArrayList<ItemStack>(Arrays.asList(new ItemStack(Material.REDSTONE, Util.random(3, 6))));
    }

    @Override
    public int getSlotLevelRequirement(int slot) {
        switch (slot) {
            case 0:
                return 1;
            case 1:
            case 2:
                return 2;
            case 3:
            case 4:
            case 5:
                return 4;
            case 6:
            case 7:
            case 8:
                return 6;
            case 9:
            case 10:
            case 11:
                return 8;
            case 12:
            case 13:
            case 14:
                return 10;
            default:
                return 0;
        }
    }
}

