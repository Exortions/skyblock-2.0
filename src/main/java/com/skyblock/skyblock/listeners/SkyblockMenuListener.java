package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class SkyblockMenuListener implements Listener {

    public static final String MENU_NAME = "Skyblock Menu";
    public static final String EMPTY_NAME = " ";

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

        if (name.equals(EMPTY_NAME)) return;

        switch (name) {
            case "Your SkyBlock Profile":
                player.sendMessage("Your SkyBlock Profile");

                skyblock.getGuiHandler().show("profile", player);
                break;
            case "Your Skills":
                player.sendMessage("Your Skills");

                skyblock.getGuiHandler().show("skills", player);
                break;
            case "Collection":
                player.sendMessage("Collection");

                skyblock.getGuiHandler().show("collection", player);
                break;
            case "Recipe Book":
                player.sendMessage("Recipe Book");

                skyblock.getGuiHandler().show("recipe_book", player);
                break;
            case "Trades":
                player.sendMessage("Trades");

                skyblock.getGuiHandler().show("trades", player);
                break;
            case "Quest Log":
                player.sendMessage("Quest Log");

                skyblock.getGuiHandler().show("quest_log", player);
                break;
            case "Calendar and Events":
                player.sendMessage("Calendar and Events");

                skyblock.getGuiHandler().show("calendar_and_events", player);
                break;
            case "Ender Chest":
                player.sendMessage("Ender Chest");

                skyblock.getGuiHandler().show("ender_chest", player);
                break;
            case "Settings":
                player.sendMessage("Settings");

                skyblock.getGuiHandler().show("settings", player);
                break;
            case "Active Effects":
                player.sendMessage("Active Effects");

                skyblock.getGuiHandler().show("active_effects", player);
                break;
            case "Pets":
                player.sendMessage("Pets");

                skyblock.getGuiHandler().show("pets", player);
                break;
            case "Crafting Table":
                player.sendMessage("Crafting Table");

                skyblock.getGuiHandler().show("crafting_table", player);
                break;
            default:
                break;
        }
    }

}
