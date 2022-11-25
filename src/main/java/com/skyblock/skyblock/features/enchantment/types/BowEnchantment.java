package com.skyblock.skyblock.features.enchantment.types;

import com.skyblock.skyblock.features.enchantment.SkyblockEnchantment;

import java.util.function.Function;

public abstract class BowEnchantment extends SkyblockEnchantment {

    public BowEnchantment(String id, String name, Function<Integer, String> description, int maxLevel) {
        super(id, name, description, maxLevel);
    }

}
