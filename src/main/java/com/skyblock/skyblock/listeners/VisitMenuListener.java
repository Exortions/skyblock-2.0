package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.features.island.IslandManager;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class VisitMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        if (!event.getClickedInventory().getTitle().startsWith("Visit ")) return;

        event.setCancelled(true);

        if (event.getCurrentItem().getType().equals(Material.BARRIER)) event.getWhoClicked().closeInventory();
        else if (event.getCurrentItem().getType().equals(Material.SKULL_ITEM)) {
            event.getWhoClicked().closeInventory();

            String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().replace("Visit ", ""));
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);

            event.getWhoClicked().teleport(new Location(IslandManager.getIsland(player.getPlayer()), 0, 100, 0));
        }
    }

}
