package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.crafting.gui.RecipeBookGUI;
import com.skyblock.skyblock.features.objectives.gui.QuestLogGui;
import com.skyblock.skyblock.features.pets.gui.PetsGUI;
import com.skyblock.skyblock.features.time.gui.CalendarEventsGUI;
import com.skyblock.skyblock.features.trades.gui.TradeGui;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
                event.getClickedInventory() == null
                || !Objects.equals(event.getClickedInventory().getTitle(), MENU_NAME)
                || event.getCurrentItem() == null
        ) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        ItemStack clicked = event.getCurrentItem();
        NBTItem item = new NBTItem(clicked);

        if (item.hasKey("isBag") && item.getBoolean("isBag")) {
            this.skyblock.getBagManager().show(item.getString("bagId"), player);

            return;
        }

        if (item.hasKey("skyblock.warp.destination.command")) {
            player.performCommand(item.getString("skyblock.warp.destination.command"));

            return;
        }

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
                new RecipeBookGUI(player).show(player);
                break;
            case "Trades":
                new TradeGui(player).show(player);
                break;
            case "Quest Log":
                new QuestLogGui(player).show(player);
                break;
            case "Calendar and Events":
                new CalendarEventsGUI(player).show(player);
                break;
            case "Ender Chest":
                skyblock.getGuiHandler().show("ender_chest", player);
                break;
            case "Settings":
                skyblock.getGuiHandler().show("settings", player);
                break;
            case "Active Effects":
                player.performCommand("sb effects");
                break;
            case "Pets":
                new PetsGUI(player).show(player);
                break;
            case "Crafting Table":
                skyblock.getGuiHandler().show("crafting_table", player);
                break;
            case "Close":
                player.closeInventory();
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
