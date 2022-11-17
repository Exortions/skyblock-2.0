package com.skyblock.skyblock.utilities.gui;

import com.skyblock.skyblock.Skyblock;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin(Skyblock.class));
    }

    public void show(Player player) {
        Inventory inventory = player.getServer().createInventory(null, slots, name);

        for (int i = 0; i < slots; i++) {
            if (items.containsKey(i)) {
                if (Objects.equals(this.getName(), "Skyblock Menu") && items.get(i).getType().equals(Material.SKULL_ITEM) && items.get(i).getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Your SkyBlock Profile")) {
                    ItemStack stack = items.get(i);

                    SkullMeta meta = (SkullMeta) stack.getItemMeta();

                    meta.setOwner(player.getName());

                    stack.setItemMeta(meta);

                    inventory.setItem(i, stack);

                    continue;
                }

                inventory.setItem(i, items.get(i));
            }
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
        for (int i = 0; i < this.slots; i++) {
            if (!this.items.containsKey(i)) this.items.put(i, stack);
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

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        HandlerList.unregisterAll(this);
    }
}
