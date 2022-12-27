package com.skyblock.skyblock.features.guis;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class LiftOperatorGui extends Gui {

    public LiftOperatorGui(Player opener) {
        super("Lift Operator", 54, new HashMap<String, Runnable>() {{
            put(ChatColor.RED + "Close", opener::closeInventory);

            put(ChatColor.GREEN + "Gunpowder Mines", () -> {
                opener.teleport(new Location(Bukkit.getWorld("deep_caverns"), 52.5, 150, 15.5, 90, 0));
            });

            put(ChatColor.GREEN + "Lapis Quarry", () -> {
                opener.teleport(new Location(Bukkit.getWorld("deep_caverns"), 52.5, 121, 15.5, 90, 0));
            });

            put(ChatColor.GREEN + "Pigman's Den", () -> {
                opener.teleport(new Location(Bukkit.getWorld("deep_caverns"), 52.5, 101, 15.5, 90, 0));
            });

            put(ChatColor.GREEN + "Slimehill", () -> {
                opener.teleport(new Location(Bukkit.getWorld("deep_caverns"), 52.5, 66, 15.5, 90, 0));
            });

            put(ChatColor.GREEN + "Diamond Reserve", () -> {
                opener.teleport(new Location(Bukkit.getWorld("deep_caverns"), 52.5, 38, 15.5, 90, 0));
            });

            put(ChatColor.GREEN + "Obsidian Sanctuary", () -> {
                opener.teleport(new Location(Bukkit.getWorld("deep_caverns"), 52.5, 13, 15.5, 90, 0));
            });
        }});

        Util.fillEmpty(this);

        SkyblockPlayer player = SkyblockPlayer.getPlayer(opener);

        addItem(49, Util.buildCloseButton());

        addItem(10, generateLiftItem(player, "Gunpowder Mines", Material.GOLD_INGOT));
        addItem(12, generateLiftItem(player, "Lapis Quarry", Material.INK_SACK, 4));
        addItem(14, generateLiftItem(player, "Pigman's Den", Material.REDSTONE));
        addItem(16, generateLiftItem(player, "Slimehill", Material.EMERALD));
        addItem(28, generateLiftItem(player, "Diamond Reserve", Material.DIAMOND));
        addItem(30, generateLiftItem(player, "Obsidian Sanctuary", Material.OBSIDIAN));
    }

    public ItemStack generateLiftItem(SkyblockPlayer player, String name, Material icon) {
        return generateLiftItem(player, name, icon, 0);
    }

    public ItemStack generateLiftItem(SkyblockPlayer player, String name, Material icon, int data) {
        if (!((ArrayList<String>) player.getValue("locations.found")).contains(name)) {
            return new ItemBuilder(ChatColor.RED + name, Material.INK_SACK, 1, (short) 8).addLore(Util.buildLore("&7Travel down to this location\n&7to permanently unlock it!")).toItemStack();
        }
        return new ItemBuilder(ChatColor.GREEN + name, icon, (short) data).addLore(Util.buildLore("&7Teleports you to the\n&b" + name + "&7!\n\n&eClick to travel!")).toItemStack();
    }

}
