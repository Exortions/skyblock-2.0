package com.skyblock.skyblock.features.bazaar;

import java.util.List;

public interface BazaarItem {

    BazaarCategory getCategory();
    String getName();
    List<BazaarSubItem> getSubItems();

    default int getInventorySize() { return 36; }
    default int getProductAmount() {
        return this.getSubItems().size();
    }

}
