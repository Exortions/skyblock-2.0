package com.skyblock.skyblock.features.merchants;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class MerchantItem {

    private final ItemStack item;
    private final String rewardCommand;
    private final int cost;

    private final boolean trade;
    private final String tradeItem;
    private final int tradeAmount;

}
