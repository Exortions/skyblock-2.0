package com.skyblock.skyblock.features.bazaar.impl;

import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.features.bazaar.BazaarSubItem;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class SkyblockBazaarSubItem implements BazaarSubItem {

    private final ItemStack icon;
    private final Rarity commodity;
    private final int slot;

}
