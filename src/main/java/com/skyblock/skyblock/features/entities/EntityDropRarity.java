package com.skyblock.skyblock.features.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum EntityDropRarity {

    COMMON(ChatColor.WHITE),
    UNCOMMON(ChatColor.GREEN),
    RARE(ChatColor.BLUE),
    LEGENDARY(ChatColor.GOLD),
    PET(ChatColor.GOLD),
    RNGESUS(ChatColor.LIGHT_PURPLE);

    private final ChatColor color;

}
