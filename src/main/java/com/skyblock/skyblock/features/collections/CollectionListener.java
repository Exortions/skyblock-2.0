package com.skyblock.skyblock.features.collections;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CollectionListener implements Listener {

    @EventHandler
    public void onItemCollect(PlayerPickupItemEvent event) {
        if (event.getItem() == null || event.getItem().getItemStack().getType().equals(Material.AIR)) return;

        for (Collection collection : Collection.getCollections()) {
            if (collection.getMaterial().equals(event.getItem().getItemStack().getType())) collection.collect(event.getPlayer(), event.getItem().getItemStack().getAmount());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null | event.getCurrentItem().getType().equals(Material.AIR)) return;

        if (event.getClickedInventory() == null) return;

        List<String> possibleInventories = new ArrayList<>();

        possibleInventories.add("Collection");

        Collection.getCollectionCategories().forEach(cat -> possibleInventories.add(cat.getName() + " Collection"));
        Collection.getCollections().forEach(col -> possibleInventories.add(col.getName() + " Collection"));

        if (!possibleInventories.contains(event.getClickedInventory().getName())) return;

        ItemStack item = event.getCurrentItem();

        Player player = (Player) event.getWhoClicked();

        if (item.getType().equals(Material.BARRIER)) player.closeInventory();

        if (event.getInventory().getName().equals("Collection")) {
            if (Collection.getCollectionCategories().stream().anyMatch(col -> col.getName().equalsIgnoreCase(ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace(" Collection", "")))) {
                player.performCommand("sb collection " + ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase().replace(" Collection", ""));
            }

            if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Go Back")) player.performCommand("sb gui skyblock_menu");
        }

        possibleInventories.remove(0);

        if (possibleInventories.contains(event.getInventory().getName())) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Go Back"))
                player.performCommand("sb collection");

            if (Collection.getCollections().stream().anyMatch(col -> col.getMaterial().equals(item.getType()))) {
                player.performCommand("sb collection " + Collection.getCollections().stream().filter(col -> col.getMaterial().equals(item.getType())).findFirst().get().getName().toLowerCase().replace(" ", "_"));
            }
        }

        event.setCancelled(true);
    }

}
