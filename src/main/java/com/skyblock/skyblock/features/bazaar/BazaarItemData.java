package com.skyblock.skyblock.features.bazaar;

public interface BazaarItemData {

    interface BazaarItemVolume {
        int getAmount();
        int getVolume();

        int instantVolume();
    }

    int getProductAmount();

    int getBuyPrice();
    int getSellPrice();

    BazaarItemVolume getBuyVolume();
    BazaarItemVolume getLast7dInstantBuyVolume();

    BazaarItemVolume getSellVolume();
    BazaarItemVolume getLast7dInstantSellVolume();

}
