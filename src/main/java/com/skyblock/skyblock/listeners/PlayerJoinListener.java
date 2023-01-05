package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void giveSkyblockMenu(PlayerJoinEvent event) {
        event.getPlayer().getInventory().setItem(8, Util.createSkyblockMenu());
    }

    @EventHandler
    public void createIsland(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        IslandManager.createIsland(event.getPlayer());
    }

}
