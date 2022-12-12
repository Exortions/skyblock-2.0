package com.skyblock.skyblock.features.bazaar.impl;

import com.skyblock.skyblock.features.bazaar.BazaarItemData;
import lombok.Data;

@Data
public class SkyblockBazaarItemData implements BazaarItemData {

    private final int productAmount;
    private final int buyPrice;
    private final int sellPrice;
    private final BazaarItemVolume buyVolume;
    private final BazaarItemVolume last7dInstantBuyVolume;
    private final BazaarItemVolume sellVolume;
    private final BazaarItemVolume last7dInstantSellVolume;

}
