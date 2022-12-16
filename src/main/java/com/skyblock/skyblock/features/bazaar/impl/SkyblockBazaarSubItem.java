package com.skyblock.skyblock.features.bazaar.impl;

import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.features.bazaar.BazaarItem;
import com.skyblock.skyblock.features.bazaar.BazaarOffer;
import com.skyblock.skyblock.features.bazaar.BazaarSubItem;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class SkyblockBazaarSubItem implements BazaarSubItem {

    private BazaarItem parent;

    private final ItemStack icon;
    private final Rarity commodity;
    private final int slot;
    private final String material;

    private final List<BazaarOffer> orders;
    private final List<BazaarOffer> offers;

}
