package com.skyblock.skyblock.features.trades;

import lombok.Data;

@Data
public class Trade {

    private final String item;
    private final int amount;

    private final String costType;
    private final String costItem;
    private final int costAmount;

}
