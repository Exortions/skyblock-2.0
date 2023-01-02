package com.skyblock.skyblock.features.minions.items.upgrades;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.features.minions.MinionBase;
import com.skyblock.skyblock.features.minions.items.MinionItem;
import com.skyblock.skyblock.features.minions.items.MinionItemType;

public class MinionExpander extends MinionItem {
    public MinionExpander() {
        super(plugin.getItemHandler().getItem("MINION_EXPANDER.json"), "minion_expander",  MinionItemType.UPGRADE, true, true);
    }

    @Override
    public void onEquip(MinionBase minion) {
        minion.additionalActionRadius += 1;
    }
    
    @Override
    public void onUnEquip(MinionBase minion) {
        minion.additionalActionRadius -= 1;
    }
}
