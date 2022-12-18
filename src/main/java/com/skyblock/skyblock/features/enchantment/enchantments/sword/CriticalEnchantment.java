package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;

import java.util.function.Function;

public class CriticalEnchantment extends SwordEnchantment {

    public static final Function<Integer, Integer> getCritDamageIncrease = (level) -> {
        if (level <= 5) return level * 10;
        else if (level == 6) return 70;

        return 0;
    };

    public CriticalEnchantment() {
        super("critical", "Critical", level -> "&7Increases critical damage by &a" + getCritDamageIncrease.apply(level) + "%&7.", 6);
    }

}
