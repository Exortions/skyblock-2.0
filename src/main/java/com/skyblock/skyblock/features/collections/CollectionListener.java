package com.skyblock.skyblock.features.collections;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

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

        Collection.getCollectionCategories().forEach(cat -> possibleInventories.add(cat.getName()));
        Collection.getCollections().forEach(col -> possibleInventories.add(col.getName()));

        if (!possibleInventories.contains(event.getClickedInventory().getName())) return;

        event.setCancelled(true);
    }

}
