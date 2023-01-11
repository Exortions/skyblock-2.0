package com.skyblock.skyblock.features.enchantment.enchantments.tool;

import com.skyblock.skyblock.features.enchantment.types.ToolEnchantment;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.enchantments.Enchantment;

public class EfficiencyEnchantment extends ToolEnchantment {
    public EfficiencyEnchantment() {
        super("efficiency", "Efficiency", (level) -> "&7Increases how quickly your tool\nbreaks blocks", 6);
    }

    @Override
    public void onEnchant(ItemBase base) {
        int level = base.getEnchantment(this.getName()).getLevel();
        base.getOrig().addUnsafeEnchantment(Enchantment.DIG_SPEED, level);
    }

    @Override
    public void onUnEnchant(ItemBase base) {
        base.getOrig().removeEnchantment(Enchantment.DIG_SPEED);
    }
}
