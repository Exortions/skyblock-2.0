package com.skyblock.skyblock.features.collections;

import lombok.Data;
import org.bukkit.Material;

import java.util.List;

@Data
public class CollectionCategory {

    private final String name;
    private final Material icon;
    private final List<String> lore;

}
