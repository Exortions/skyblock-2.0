package com.skyblock.skyblock.features.minions.items;

import org.bukkit.inventory.ItemStack;

public abstract class MinionFuel extends MinionItem {
    public final int duration;

    public MinionFuel(ItemStack baseItem, String internalName, boolean stackable, int duration) {
        super(baseItem, internalName, MinionItemType.FUEL, stackable, true);
        this.duration = duration;
    }
}
