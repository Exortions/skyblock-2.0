package com.skyblock.skyblock.features.minions.items.upgrades;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.features.minions.MinionBase;
import com.skyblock.skyblock.features.minions.items.MinionItem;
import com.skyblock.skyblock.features.minions.items.MinionItemType;

public class AutoSmelter extends MinionItem {
    public AutoSmelter() {
        super(plugin.getItemHandler().getItem("AUTO_SMELTER.json"), "auto_smelter",  MinionItemType.UPGRADE, false, true);
    }

    @Override
    public ItemStack[] onBlockCollect(MinionBase minion, ItemStack[] drops) {
        for (int i = 0; i < drops.length; ++i) {
            switch (drops[i].getType()) {
                case COBBLESTONE:
                    drops[i].setType(Material.STONE);
                    break;
                case SAND:
                    drops[i].setType(Material.GLASS);
                    break;
                case LOG:
                case LOG_2:
                    drops[i].setType(Material.COAL);
                    break;
                case GOLD_ORE:
                    drops[i].setType(Material.GOLD_INGOT);
                    break;
                case IRON_ORE:
                    drops[i].setType(Material.IRON_INGOT);
                    break;
                case CLAY_BALL:
                    drops[i].setType(Material.BRICK);
                    break;
                case CACTUS:
                    drops[i].setType(Material.INK_SACK);
                    drops[i].setDurability(new Integer(2).shortValue()); //cactus green
                    break;
            }
        }
        return drops;
    }
}
