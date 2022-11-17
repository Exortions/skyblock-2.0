package com.skyblock.skyblock.utilities.gui;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
public class Gui implements Listener {

    public final HashMap<String, Runnable> clickEvents;
    public final HashMap<Integer, ItemStack> items;
    public final String name;
    public final int slots;

    public Gui(String name, int slots, HashMap<String, Runnable> clickEvents) {
        this.name = name;
        this.slots = slots;

        this.clickEvents = clickEvents;

        this.items = new HashMap<>();
    }

    public void show(Player player) {
        Inventory inventory = player.getServer().createInventory(null, slots, name);

        for (int i = 0; i < slots; i++) {
            if (items.containsKey(i)) inventory.setItem(i, items.get(i));
        }

        player.openInventory(inventory);
    }

    public void hide(Player player) {
        player.closeInventory();
    }

    public void addItem(int slot, ItemStack stack) {
        this.items.put(slot, stack);
    }

    public void fillEmpty(ItemStack stack) {
        for (int i = 0; i < this.items.size(); i++) {
            this.items.putIfAbsent(i, stack);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equals(name)) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            if (clickEvents.containsKey(event.getCurrentItem().getItemMeta().getDisplayName())) clickEvents.get(event.getCurrentItem().getItemMeta().getDisplayName()).run();
        }
    }

}
