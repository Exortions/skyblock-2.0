package com.skyblock.skyblock.features.collections;

import lombok.Data;
import org.bukkit.Material;

@Data
public class CollectionCategory {

    private final String name;
    private final Material icon;
    private final short data;

    public CollectionCategory(String name, Material icon, short... data) {
        this.name = name;
        this.icon = icon;

        if (data.length == 0) {
            this.data = 0;
        } else {
            this.data = data[0];
        }
    }

}
