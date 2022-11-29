package com.skyblock.skyblock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum Rarity {

    VERY_SPECIAL(ChatColor.RED, 9),
    SPECIAL(ChatColor.RED, 8),
    DIVINE(ChatColor.AQUA, 7),
    MYTHIC(ChatColor.LIGHT_PURPLE, 6),
    LEGENDARY(ChatColor.GOLD, 5),
    EPIC(ChatColor.DARK_PURPLE, 4),
    RARE(ChatColor.BLUE, 3),
    UNCOMMON(ChatColor.GREEN, 2),
    COMMON(ChatColor.WHITE, 1);

    ChatColor color;
    int level;

}
