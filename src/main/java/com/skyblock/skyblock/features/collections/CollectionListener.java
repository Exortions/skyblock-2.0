package com.skyblock.skyblock.features.collections;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import javax.swing.*;

public class CollectionListener implements Listener {

    @EventHandler
    public void onItemCollect(PlayerPickupItemEvent event) {
        if (event.getItem() == null || event.getItem().getItemStack().getType().equals(Material.AIR)) return;

        for (Collection collection : Collection.getCollections()) {
            if (collection.getMaterial().equals(event.getItem().getItemStack().getType())) collection.collect(event.getPlayer(), event.getItem().getItemStack().getAmount());
        }
    }

}
