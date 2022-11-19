package com.skyblock.skyblock.features.enchantment;

import lombok.Data;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;

@Data
public abstract class SkyblockEnchantment implements Listener {

    protected final String name;
    protected final HashMap<Integer, String> description;

    protected final int maxLevel;

    public SkyblockEnchantment(String name, HashMap<Integer, String> description, int maxLevel) {
        this.name = name;
        this.description = description;
        this.maxLevel = maxLevel;
    }

    public String getDescription(int level) {
        return description.get(level);
    }

}
