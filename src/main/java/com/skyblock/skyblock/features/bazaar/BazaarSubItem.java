package com.skyblock.skyblock.features.bazaar;

import com.skyblock.skyblock.enums.Rarity;
import org.bukkit.inventory.ItemStack;

public interface BazaarSubItem {

    ItemStack getIcon();
    Rarity getCommodity();
    int getSlot();

}
