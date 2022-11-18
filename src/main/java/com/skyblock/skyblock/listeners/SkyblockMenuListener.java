package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class SkyblockMenuListener implements Listener {

    public static final String MENU_NAME = "Skyblock Menu";
    public static final String EMPTY_NAME = " ";

    public static final String ITEM_NAME = ChatColor.GREEN + "Skyblock Menu" + ChatColor.GRAY + " (Right Click)";

    private final Skyblock skyblock;

    public SkyblockMenuListener(Skyblock skyblock) {
        this.skyblock = skyblock;

        this.registerGui();
    }

    public void registerGui() {
        Gui skyblockMenu = new Gui(MENU_NAME, 54, new HashMap<>());

        skyblockMenu.addItem(13, new ItemBuilder(
                ChatColor.GREEN + "Your SkyBlock Profile",
                Material.SKULL_ITEM,
                1, (short) 3)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addLore(
                        ChatColor.GRAY + "View your equipment, stats,",
                        ChatColor.GRAY + "and more!",
                        "",
                        ChatColor.RED + " ❤ Health " + ChatColor.WHITE + "100",
                        ChatColor.GREEN + " ❈ Defence " + ChatColor.WHITE + "0",
                        ChatColor.RED + " ❁ Strength " + ChatColor.WHITE + "0",
                        ChatColor.WHITE + " ✦ Speed " + ChatColor.WHITE + "0",
                        ChatColor.BLUE + " ☣ Crit Chance " + ChatColor.WHITE + "0",
                        ChatColor.BLUE + " ☠ Crit Damage " + ChatColor.WHITE + "0",
                        ChatColor.AQUA + " ✎ Intelligence " + ChatColor.WHITE + "0",
                        ChatColor.YELLOW + " ⚔ Attack Speed " + ChatColor.WHITE + "0",
                        ChatColor.DARK_AQUA + " ∞ Sea Creature Chance " + ChatColor.WHITE + "0",
                        ChatColor.AQUA + " ✯ Magic Find " + ChatColor.WHITE + "0",
                        ChatColor.LIGHT_PURPLE + " ♣ Pet Luck " + ChatColor.WHITE + "0",
                        ChatColor.WHITE + " ❂ True Defense " + ChatColor.WHITE + "0",
                        ChatColor.RED + " ⫽ Ferocity " + ChatColor.WHITE + "0",
                        ChatColor.RED + " ✹ Ability Damage " + ChatColor.WHITE + "0",
                        ChatColor.GOLD + " ☘ Mining Fortune " + ChatColor.WHITE + "0",
                        ChatColor.GOLD + " ⸕ Mining Speed " + ChatColor.WHITE + "0",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                ).toItemStack());

        skyblockMenu.addItem(19, new ItemBuilder(
                ChatColor.GREEN + "Your Skills",
                Material.DIAMOND_SWORD)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View your Skill progression and",
                        ChatColor.GRAY + "rewards.", "",
                        ChatColor.GOLD + "14.8 Skill Avg. " + ChatColor.DARK_GRAY + "(non-cosmetic)",
                        "",
                        ChatColor.YELLOW + "Click to view!")
                .toItemStack());

        skyblockMenu.addItem(20, new ItemBuilder(
                ChatColor.GREEN + "Collection",
                Material.ITEM_FRAME)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View all of the items available",
                        ChatColor.GRAY + "in SkyBlock. Collect more of an",
                        ChatColor.GRAY + "item to unlock rewards on your",
                        ChatColor.GRAY + "way to becoming a master of",
                        ChatColor.GRAY + "SkyBlock!",
                        "",
                        ChatColor.GRAY + "Collection Unlocked: " + ChatColor.YELLOW + "82.1" + ChatColor.GOLD + "%",
                        ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯" + ChatColor.WHITE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "⎯⎯⎯⎯" + ChatColor.YELLOW + " 55" + ChatColor.GOLD + "/" + ChatColor.YELLOW + "67",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack());

        skyblockMenu.addItem(21, new ItemBuilder(
            ChatColor.GREEN + "Recipe Book",
            Material.BOOK)
            .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
            .addLore(
                    ChatColor.GRAY + "Through your adventure, you will",
                    ChatColor.GRAY + "unlock recipes for all kinds of",
                    ChatColor.GRAY + "special items! You can view how",
                    ChatColor.GRAY + "to craft these items here.",
                    "",
                    ChatColor.GRAY + "Recipe Book Unlocked: " + ChatColor.YELLOW + "38.4" + ChatColor.GOLD + "%",
                    ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "⎯⎯⎯⎯⎯⎯⎯⎯" + ChatColor.WHITE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯" + ChatColor.YELLOW + " 324" + ChatColor.GOLD + "/" + ChatColor.YELLOW + "844",
                    "",
                    ChatColor.YELLOW + "Click to view!"
            )
            .toItemStack());

        skyblockMenu.addItem(22, new ItemBuilder(
                ChatColor.GREEN + "Trades",
                Material.EMERALD)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View your available trades.",
                        ChatColor.GRAY + "These trades are always",
                        ChatColor.GRAY + "Availiable and accessible through",
                        ChatColor.GRAY + "the SkyBlock Menu.",
                        "",
                        ChatColor.GRAY + "Trades Unlocked: " + ChatColor.YELLOW + "83.3" + ChatColor.GOLD + "%",
                        ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯" + ChatColor.WHITE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "⎯⎯⎯⎯" + ChatColor.YELLOW + " 20" + ChatColor.GOLD + "/" + ChatColor.YELLOW + "24",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack());

        skyblockMenu.addItem(23, new ItemBuilder(
                ChatColor.GREEN + "Quest Log",
                Material.BOOK_AND_QUILL)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View your active quests,",
                        ChatColor.GRAY + "progress, and rewards.",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack());

        skyblockMenu.addItem(24, new ItemBuilder(
                ChatColor.GREEN + "Calendar and Events",
                Material.WATCH)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View the SkyBlock Calendar,",
                        ChatColor.GRAY + "upcoming events, and event",
                        ChatColor.GRAY + "rewards!",
                        "",
                        ChatColor.GRAY + "Next Event: " + ChatColor.RED + "211th Season of Jerry",
                        ChatColor.GRAY + "Starting in: " + ChatColor.YELLOW + "0d 22h 24m 35s",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack());

        skyblockMenu.addItem(25, new ItemBuilder(
                ChatColor.GREEN + "Ender Chest",
                Material.ENDER_CHEST)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "Store global items that you want",
                        ChatColor.GRAY + "to access at any time from",
                        ChatColor.GRAY + "anywhere here.",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack());

        skyblockMenu.addItem(32, new ItemBuilder(
                ChatColor.GREEN + "Active Effects",
                Material.POTION)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View and manage all of your",
                        ChatColor.GRAY + "active potion effects.",
                        "",
                        ChatColor.GRAY + "Drink Potions or splash them",
                        ChatColor.GRAY + "on the ground to buff yourself!",
                        "",
                        ChatColor.GRAY + "Currently Active: " + ChatColor.YELLOW + "0",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack());

        skyblockMenu.addItem(30, new ItemBuilder(
                ChatColor.GREEN + "Pets",
                Material.BONE)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View and manage all of your",
                        ChatColor.GRAY + "Pets.",
                        "",
                        ChatColor.GRAY + "Level up your pets faster by",
                        ChatColor.GRAY + "gaining xp in their favorite",
                        ChatColor.GRAY + "skill!",
                        "",
                        ChatColor.GRAY + "Selected pet: " + ChatColor.DARK_PURPLE + "Lion",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack());

        skyblockMenu.addItem(31, new ItemBuilder(
                ChatColor.GREEN + "Crafting Table",
                Material.WORKBENCH)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "Opens the crafting grid.",
                        "",
                        ChatColor.YELLOW + "Click to open!"
                )
                .toItemStack());

        skyblockMenu.fillEmpty(new ItemBuilder(EMPTY_NAME, Material.STAINED_GLASS_PANE, (short) 15).toItemStack());

        skyblock.getGuiHandler().registerGui("skyblock_menu", skyblockMenu);
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
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ITEM_NAME)) event.setCancelled(true);
    }

}
