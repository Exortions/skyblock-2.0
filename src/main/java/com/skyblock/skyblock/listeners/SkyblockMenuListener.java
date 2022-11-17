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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class SkyblockMenuListener implements Listener {

    public static final String MENU_NAME = "Skyblock Menu";
    public static final String EMPTY_NAME = " ";

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
                        ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯" + ChatColor.WHITE + ChatColor.BOLD + "⎯⎯⎯⎯ " + ChatColor.YELLOW + "55" + ChatColor.GOLD + "/" + ChatColor.YELLOW + "67",
                        "",
                        ChatColor.YELLOW + "Click to view!"
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
