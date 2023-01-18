package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.crafting.gui.RecipeBookGUI;
import com.skyblock.skyblock.features.objectives.gui.QuestLogGui;
import com.skyblock.skyblock.features.pets.gui.PetsGUI;
import com.skyblock.skyblock.features.time.gui.CalendarEventsGUI;
import com.skyblock.skyblock.features.trades.gui.TradeGui;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
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
                open(() -> skyblock.getGuiHandler().show("profile", player), skyblockPlayer);
                break;
            case "Your Skills":
                open(() -> skyblock.getGuiHandler().show("skills", player), skyblockPlayer);
                break;
            case "Collection":
                open(() -> skyblock.getGuiHandler().show("collection", player), skyblockPlayer);
                break;
            case "Recipe Book":
                open(() -> new RecipeBookGUI(player).show(player), skyblockPlayer);
                break;
            case "Trades":
                open(() -> new TradeGui(player).show(player), skyblockPlayer);
                break;
            case "Quest Log":
                open(() -> new QuestLogGui(player).show(player), skyblockPlayer);
                break;
            case "Calendar and Events":
                open(() -> new CalendarEventsGUI(player).show(player), skyblockPlayer);
                break;
            case "Ender Chest":
                open(() -> skyblock.getGuiHandler().show("ender_chest", player), skyblockPlayer);
                break;
            case "Settings":
                open(() -> player.performCommand("sb settings"), skyblockPlayer);
                break;
            case "Active Effects":
                open(() -> player.performCommand("sb effects"), skyblockPlayer);
                break;
            case "Pets":
                open(()-> new PetsGUI(player).show(player), skyblockPlayer);
                break;
            case "Crafting Table":
                open(() -> skyblock.getGuiHandler().show("crafting_table", player), skyblockPlayer);
                break;
            case "Personal Bank":
                if (skyblockPlayer.hasExtraData("personalBankLastUsed") && (long) skyblockPlayer.getExtraData("personalBankLastUsed") < System.currentTimeMillis() - (int) skyblockPlayer.getValue("bank.personal.cooldown") * 60000) {
                    open(() -> {
                        skyblockPlayer.setExtraData("personalBankUsed", true);
                        player.performCommand("sb banker");
                    }, skyblockPlayer);
                }
                break;
            case "Close":
                player.closeInventory();
                break;
            default:
                break;
        }
    }

    public void open(Runnable to, SkyblockPlayer player) {
        if (player.getBoolValue("settings.menuSounds"))
            player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.CLICK, 1, 1);

        to.run();
    }

    @EventHandler
    public void onSkyblockMenuOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR) || player.getItemInHand().getItemMeta().getDisplayName() == null)
            return;

        if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ITEM_NAME))
            skyblock.getGuiHandler().show("skyblock_menu", player);
    }

    @EventHandler
    public void onSkyblockMenuDrag(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR) || event.getCurrentItem().getItemMeta().getDisplayName() == null)
            return;

        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ITEM_NAME)) event.setCancelled(true);
    }

}
