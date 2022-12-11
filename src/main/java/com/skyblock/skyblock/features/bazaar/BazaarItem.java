package com.skyblock.skyblock.features.bazaar;

import java.util.List;

public interface BazaarItem {

    String getName();
    List<BazaarSubItem> getSubItems();

    default int getProductAmount() {
        return this.getSubItems().size();
    }

}
