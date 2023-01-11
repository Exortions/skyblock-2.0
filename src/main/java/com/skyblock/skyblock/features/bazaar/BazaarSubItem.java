package com.skyblock.skyblock.features.bazaar;

import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface BazaarSubItem {

    BazaarItem getParent();

    ItemStack getIcon();
    Rarity getCommodity();
    int getSlot();
    String getMaterial();

    List<BazaarOffer> getOrders();
    List<BazaarOffer> getOffers();

    double getLowestSellPrice();
    double getHighestBuyPrice();

    double getLowestSellPrice(int requiredAmount);
    double getHighestBuyPrice(int requiredAmount);

    default ItemBuilder getNamedIcon() {
        return new ItemBuilder(getIcon()).setDisplayName(getParent().getCategory().getColor() + ChatColor.stripColor(getIcon().getItemMeta().getDisplayName() == null ? WordUtils.capitalize(getIcon().getType().name().toLowerCase().replace("_", " ")) : getIcon().getItemMeta().getDisplayName())).addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
    }

    default ItemStack getItem() {
        return Util.getItem(getMaterial());
    }

}
