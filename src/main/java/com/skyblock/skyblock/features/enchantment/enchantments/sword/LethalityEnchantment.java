package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import org.bukkit.ChatColor;

public class LethalityEnchantment extends SwordEnchantment {

    // Bugged in Skyblock so does nothing
    public LethalityEnchantment() {
        super("lethality", "Lethality", (level) -> {
            return ChatColor.GRAY + "Reduces the armor of your target\n&7by " + ChatColor.GREEN + level + "% &7for 8 seconds\n&7each time you hit them\n&7melee. Stacks up to 5 times";
        }, 4);
    }
}
