package com.skyblock.skyblock.features.enchantment.enchantments.armor;

import com.skyblock.skyblock.features.enchantment.types.ArmorEnchantment;
import org.bukkit.ChatColor;

public class FireProtectionEnchantment extends ArmorEnchantment {

    //Bugged in real Skyblock
    public FireProtectionEnchantment() {
        super("fire_protection", "Fire Protection", (level) -> "&7Grants " + ChatColor.GREEN + "+" + level * 4 + " ❈ Defense\n&7against fire and lava", 6);
    }
}
