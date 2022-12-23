package com.skyblock.skyblock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public static String[] stringValues() {
        String[] strings = new String[values().length];
        Rarity[] values = values();

        Collections.reverse(Arrays.asList(values));

        for (int i = 0; i < strings.length; i++){
            strings[i] = values[i].getColor() + WordUtils.capitalize(values[i].name().toLowerCase().replace("_", " "));
        }

        return strings;
    }

    @Override
    public String toString() {
        return WordUtils.capitalize(name().toLowerCase().replace("_", " "));
    }

    public String coloredString() {
        return this.getColor() + "" + ChatColor.BOLD + this.toString().toUpperCase();
    }

}
