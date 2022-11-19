package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class SkyblockMenuListener implements Listener {

    public static final String MENU_NAME = "Skyblock Menu";

    public static final String ITEM_NAME = ChatColor.GREEN + "Skyblock Menu" + ChatColor.GRAY + " (Right Click)";

    private final Skyblock skyblock;

    public SkyblockMenuListener(Skyblock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onSkyblockMenuClick(InventoryClickEvent event) {
        if (
                !Objects.equals(event.getClickedInventory().getTitle(), MENU_NAME)
                || event.getCurrentItem() == null
        ) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        ItemStack clicked = event.getCurrentItem();
        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (name.equals(" ")) return;

        switch (name) {
            case "Your SkyBlock Profile":
                skyblock.getGuiHandler().show("profile", player);
                break;
            case "Your Skills":
                skyblock.getGuiHandler().show("skills", player);
                break;
            case "Collection":
                skyblock.getGuiHandler().show("collection", player);
                break;
            case "Recipe Book":
                skyblock.getGuiHandler().show("recipe_book", player);
                break;
            case "Trades":
                skyblock.getGuiHandler().show("trades", player);
                break;
            case "Quest Log":
                skyblock.getGuiHandler().show("quest_log", player);
                break;
            case "Calendar and Events":
                skyblock.getGuiHandler().show("calendar_and_events", player);
                break;
            case "Ender Chest":
                skyblock.getGuiHandler().show("ender_chest", player);
                break;
            case "Settings":
                skyblock.getGuiHandler().show("settings", player);
                break;
            case "Active Effects":
                skyblock.getGuiHandler().show("active_effects", player);
                break;
            case "Pets":
                skyblock.getGuiHandler().show("pets", player);
                break;
            case "Crafting Table":
                skyblock.getGuiHandler().show("crafting_table", player);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onSkyblockMenuOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR) || player.getItemInHand().getItemMeta().getDisplayName() == null) return;

        if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ITEM_NAME)) skyblock.getGuiHandler().show("skyblock_menu", player);
    }

    @EventHandler
    public void onSkyblockMenuDrag(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR) || event.getCurrentItem().getItemMeta().getDisplayName() == null) return;

        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ITEM_NAME)) event.setCancelled(true);
    }

}
