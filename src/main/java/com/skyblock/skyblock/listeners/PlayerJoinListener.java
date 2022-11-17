package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ItemBuilder skyblockMenu = new ItemBuilder(SkyblockMenuListener.ITEM_NAME, Material.NETHER_STAR).addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS).addLore(ChatColor.GRAY + "View all of your SkyBlock", ChatColor.GRAY + "progress, including your Skills,", ChatColor.GRAY + "Collections, Recipes, and more!", "", ChatColor.YELLOW + "Click to open!");

        event.getPlayer().getInventory().setItem(9, skyblockMenu.toItemStack());
    }

}
