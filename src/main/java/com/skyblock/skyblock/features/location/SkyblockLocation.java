package com.skyblock.skyblock.features.location;

import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Location;

@Data
public class SkyblockLocation {

    private final Location position1;
    private final Location position2;

    private final ChatColor color;
    private final String name;

    private final int weight;

}
