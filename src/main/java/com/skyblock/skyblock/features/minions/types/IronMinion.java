package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class IronMinion extends MiningMinion {
    public IronMinion() {
        this(UUID.randomUUID());
    }
    public IronMinion(UUID uuid) {
        super(uuid, "Iron", "mining", Color.fromRGB(138, 138, 138), Material.IRON_ORE);

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
                return 17;
            case 3:
            case 4:
                return 15;
            case 5:
            case 6:
                return 14;
            case 7:
            case 8:
                return 12;
            case 9:
            case 10:
                return 10;
            case 11:
                return 8;
            default:
                return 8;
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
        return new ArrayList<ItemStack>(Arrays.asList(new ItemStack(Material.IRON_ORE)));
        
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

