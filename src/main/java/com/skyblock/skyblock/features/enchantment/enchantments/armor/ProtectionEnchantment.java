package com.skyblock.skyblock.features.enchantment.enchantments.armor;

import com.skyblock.skyblock.features.enchantment.types.ArmorEnchantment;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;

public class ProtectionEnchantment extends ArmorEnchantment {
    public ProtectionEnchantment() {
        super("protection", "Protection", (level) -> {
            String description = ChatColor.GRAY + "Grants " + ChatColor.GREEN + "+%s ❈ Defense";
            return String.format(description, level * 3);
        }, 6);
    }

    @Override
    public void onEnchant(ItemBase base) {
        int level = base.getEnchantment(this.getName()).getLevel();
        base.setDefense(base.getDefense() + level * 3);
    }

    @Override
    public void onUnEnchant(ItemBase base) {
        int level = base.getEnchantment(this.getName()).getLevel();
        base.setDefense(base.getDefense() - level * 3);
    }
}
