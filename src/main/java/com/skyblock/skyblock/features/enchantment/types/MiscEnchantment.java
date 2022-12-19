package com.skyblock.skyblock.features.enchantment.types;

import com.skyblock.skyblock.features.enchantment.SkyblockEnchantment;

import java.util.function.Function;

public abstract class MiscEnchantment extends SkyblockEnchantment {
    public MiscEnchantment(String id, String name, Function<Integer, String> description, int maxLevel) {
        super(id, name, description, maxLevel);
    }
}
