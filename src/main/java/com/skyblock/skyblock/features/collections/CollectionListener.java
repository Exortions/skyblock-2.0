package com.skyblock.skyblock.features.collections;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerCollectItemEvent;
import com.skyblock.skyblock.events.SkyblockPlayerCollectionRewardEvent;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CollectionListener implements Listener {

    @EventHandler
    public void onItemCollect(PlayerPickupItemEvent event) {
        if (event.getItem() == null || event.getItem().getItemStack().getType().equals(Material.AIR)) return;

        ItemStack item = event.getItem().getItemStack();

        if (item.getItemMeta().hasDisplayName())
            if (item.getItemMeta().getDisplayName().startsWith("coins_")) return;

        for (Collection collection : Collection.getCollections()) {
            if (collection.getMaterial().equals(event.getItem().getItemStack().getType()) && collection.getData() == event.getItem().getItemStack().getData().getData()) {
                boolean success = collection.collect(event.getPlayer(), event.getItem().getItemStack().getAmount(), event.getItem().getItemStack());

                if (!success) continue;

                ItemStack clone = Util.toSkyblockItem(item).clone();
                clone.setAmount(item.getAmount());

                NBTItem nbt = new NBTItem(clone);

                nbt.setBoolean("collected", true);

                event.setCancelled(true);

                event.getPlayer().getInventory().addItem(nbt.getItem());
                event.getPlayer().playSound(event.getItem().getLocation(), Sound.ITEM_PICKUP, 0.1f, 1);

                event.getItem().remove();

                Bukkit.getPluginManager().callEvent(new SkyblockPlayerCollectItemEvent(SkyblockPlayer.getPlayer(event.getPlayer()), collection, item.getAmount()));

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

    @EventHandler
    public void onCollection(SkyblockPlayerCollectionRewardEvent e) {
        SkyblockPlayer player = e.getPlayer();
        String reward = e.getReward();
        if (reward.contains("Accessory Bag Upgrade")) {
            upgradeBag(player, "accessory_bag", Arrays.asList(0, 3, 9, 15, 21, 27, 33, 39, 45, 51, 57));
        } else if (reward.contains("Quiver Upgrade")) {
            upgradeBag(player, "quiver", Arrays.asList(0, 27, 36, 45));
        }
    }

    private void upgradeBag(SkyblockPlayer player, String bag, List<Integer> slots) {
        if (!player.getBoolValue("bag." + bag + ".unlocked")) player.setValue("bag." + bag + ".unlocked", true);

        int current = player.getIntValue("bag." + bag + ".slots");
        int next = slots.get(slots.indexOf(current) + 1);

        player.setValue("bag." + bag + ".slots", next);
    }
}
