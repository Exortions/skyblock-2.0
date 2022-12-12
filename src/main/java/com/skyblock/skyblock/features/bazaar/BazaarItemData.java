package com.skyblock.skyblock.features.bazaar;

public interface BazaarItemData {

    interface BazaarItemVolume {
        double getAmount();
        double getVolume();
    }

    double getProductAmount();

    double getBuyPrice();
    double getSellPrice();

    BazaarItemVolume getBuyVolume();
    int getLast7dInstantBuyVolume();

    BazaarItemVolume getSellVolume();
    int getLast7dInstantSellVolume();

}
