package com.skyblock.skyblock.features.bazaar.impl;

import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.features.bazaar.BazaarItem;
import com.skyblock.skyblock.features.bazaar.BazaarOffer;
import com.skyblock.skyblock.features.bazaar.BazaarSubItem;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
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

    @Override
    public double getLowestSellPrice() {
        return offers.stream().mapToDouble(BazaarOffer::getPrice).min().orElse(0);
    }

    @Override
    public double getHighestBuyPrice() {
        return orders.stream().mapToDouble(BazaarOffer::getPrice).max().orElse(0);
    }

    @Override
    public double getLowestSellPrice(int requiredAmount) {
        return offers.stream()
                .sorted(Comparator.comparingDouble(BazaarOffer::getPrice))
                .filter(offer -> offer.getAmount() >= requiredAmount)
                .mapToDouble(BazaarOffer::getPrice)
                .findFirst()
                .orElse(0);
    }

    @Override
    public double getHighestBuyPrice(int requiredAmount) {
        return orders.stream()
                .sorted(Comparator.comparingDouble(BazaarOffer::getPrice).reversed())
                .filter(offer -> offer.getAmount() >= requiredAmount)
                .mapToDouble(BazaarOffer::getPrice)
                .findFirst()
                .orElse(0);
    }
}
