package com.skyblock.skyblock.features.enchantment.types;

import com.skyblock.skyblock.features.enchantment.SkyblockEnchantment;

import java.util.HashMap;

public abstract class BootEnchantment extends SkyblockEnchantment {

    public BootEnchantment(String name, HashMap<Integer, String> description, int maxLevel) {
        super(name, description, maxLevel);
    }

}
