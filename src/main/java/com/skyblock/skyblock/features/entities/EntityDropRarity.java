package com.skyblock.skyblock.features.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum EntityDropRarity {

    GUARANTEED(ChatColor.GREEN),
    COMMON(ChatColor.GREEN),
    OCCASIONAL(ChatColor.BLUE),
    RARE(ChatColor.GOLD),
    VERY_RARE(ChatColor.AQUA),
    EXTRAORDINARILY_RARE(ChatColor.DARK_PURPLE),
    CRAZY_RARE(ChatColor.LIGHT_PURPLE);

    private final ChatColor color;

}
