package com.skyblock.skyblock.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EnderChestListener implements Listener {

    @EventHandler
    public void onEnderChestClose(InventoryCloseEvent event) {
        if (!event.getInventory().getTitle().equals("Ender Chest")) return;

        event.getPlayer().getEnderChest().setContents(event.getInventory().getContents());
    }

}
