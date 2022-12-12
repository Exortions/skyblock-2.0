package com.skyblock.skyblock.features.bazaar;

import com.skyblock.skyblock.enums.Rarity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface BazaarSubItem {

    ItemStack getIcon();
    Rarity getCommodity();
    int getSlot();

    List<BazaarOffer> getOrders();
    List<BazaarOffer> getOffers();

}
