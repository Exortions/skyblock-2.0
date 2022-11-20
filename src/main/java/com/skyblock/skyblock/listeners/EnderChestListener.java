package com.skyblock.skyblock.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EnderChestListener implements Listener {

    @EventHandler
    public void onEnderChestClose(InventoryCloseEvent event) {
        if (!event.getInventory().getTitle().equals("Ender Chest")) return;

        event.getPlayer().getEnderChest().setContents(event.getInventory().getContents());

        ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.CHEST_CLOSE, 1, 1);
    }

}
