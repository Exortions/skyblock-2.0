package com.skyblock.skyblock.features.bazaar.impl;

import com.skyblock.skyblock.features.bazaar.BazaarCategory;
import com.skyblock.skyblock.features.bazaar.BazaarItem;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

@Data
public class SkyblockBazaarCategory implements BazaarCategory {

    private final String name;
    private final Material icon;
    private final ChatColor color;
    private final Short paneColor;

    private final List<BazaarItem> items;

}
