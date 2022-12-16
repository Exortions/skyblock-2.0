package com.skyblock.skyblock.features.bazaar.impl;

import com.skyblock.skyblock.features.bazaar.BazaarCategory;
import com.skyblock.skyblock.features.bazaar.BazaarItem;
import com.skyblock.skyblock.features.bazaar.BazaarSubItem;
import lombok.Data;

import java.util.List;

@Data
public class SkyblockBazaarItem implements BazaarItem {

    private BazaarCategory category;
    private final String name;
    private final List<BazaarSubItem> subItems;
    private final int inventorySize;

}
