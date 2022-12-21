package com.skyblock.skyblock.features.enchantment.enchantments.misc;

import com.skyblock.skyblock.features.enchantment.types.MiscEnchantment;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.enchantments.Enchantment;

import java.util.function.Function;

public class EfficiencyEnchantment extends MiscEnchantment {
    public EfficiencyEnchantment() {
        super("efficiency", "Efficiency", (level) -> "&7Increases how quickly your tool\nbreaks blocks", 6);
    }

    @Override
    public void onEnchant(ItemBase base) {
        int level = base.getEnchantment(this.getName()).getLevel();
        base.getOrig().addEnchantment(Enchantment.DIG_SPEED, level);
    }

    @Override
    public void onUnEnchant(ItemBase base) {
        base.getOrig().removeEnchantment(Enchantment.DIG_SPEED);
    }
}
