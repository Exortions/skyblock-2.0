package com.skyblock.skyblock.features.collections;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CollectionListener implements Listener {

    @EventHandler
    public void onItemCollect(PlayerPickupItemEvent event) {
        if (event.getItem() == null || event.getItem().getItemStack().getType().equals(Material.AIR)) return;

        ItemStack item = event.getItem().getItemStack();

        for (Collection collection : Collection.getCollections()) {
            if (collection.getMaterial().equals(event.getItem().getItemStack().getType()) && collection.getData() == event.getItem().getItemStack().getData().getData()) {
                boolean success = collection.collect(event.getPlayer(), event.getItem().getItemStack().getAmount(), event.getItem().getItemStack());

                if (!success) continue;

                ItemStack clone = Util.toSkyblockItem(item).clone();
                clone.setAmount(item.getAmount());

                NBTItem nbt = new NBTItem(clone);

                nbt.setBoolean("collected", true);

                event.setCancelled(true);

                event.getPlayer().getInventory().addItem(clone);
                event.getPlayer().playSound(event.getItem().getLocation(), Sound.ITEM_PICKUP, 0.1f, 1);

                event.getItem().remove();

                return;
            }
        }

        ItemStack clone = Util.toSkyblockItem(item).clone();
        clone.setAmount(item.getAmount());

        event.setCancelled(true);

        event.getPlayer().getInventory().addItem(clone);
        event.getPlayer().playSound(event.getItem().getLocation(), Sound.ITEM_PICKUP, 0.1f, 1);

        event.getItem().remove();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

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
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Go Back")) {
                Collection collection = null;

                for (Collection col : Collection.getCollections()) {
                    if (col.getName().equalsIgnoreCase(event.getInventory().getName().replace(" Collection", "")))
                        collection = col;
                }

                if (collection == null) player.performCommand("sb collection");
                else player.performCommand("sb collection " + collection.getCategory().toLowerCase());
            }

            if (Collection.getCollections().stream().anyMatch(col -> col.getMaterial().equals(item.getType()))) {
                AtomicBoolean unlocked = new AtomicBoolean(false);

                Collection.getCollections().stream().filter(col -> col.getMaterial().equals(item.getType())).forEach(col -> {
                    if (SkyblockPlayer.getPlayer(player).getValue("collection." + col.getName().toLowerCase() + ".unlocked").equals(true)) unlocked.set(true);
                });

                if (!unlocked.get()) {
                    player.sendMessage(ChatColor.RED + "You have not unlocked this collection yet!");

                    event.setCancelled(true);

                    return;
                }

                player.performCommand("sb collection " + Collection.getCollections().stream().filter(col -> col.getMaterial().equals(item.getType()) && col.getData() == item.getDurability()).findFirst().get().getName().toLowerCase().replace(" ", "_"));
            }
        }

        event.setCancelled(true);
    }

}
