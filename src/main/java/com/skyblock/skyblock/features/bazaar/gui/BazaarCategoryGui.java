package com.skyblock.skyblock.features.bazaar.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.bazaar.BazaarCategory;
import com.skyblock.skyblock.features.bazaar.BazaarItem;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class BazaarCategoryGui extends Gui {

    private final ItemStack directMode = new ItemBuilder(ChatColor.GREEN + "Direct Mode", Material.IRON_ORE).addLore(Arrays.asList(Util.buildLore("&8Bazaar View\n\n&7View buy and sell prices of each\n&7product.\n\n&eClick to toggle view!"))).toItemStack();
    private final ItemStack advancedMode = new ItemBuilder(ChatColor.GREEN + "Advanced Mode", Material.GOLD_ORE).addLore(Arrays.asList(Util.buildLore("&8Bazaar View\n\n&7View prices, orders volume,\n&7recent transactions and graphs\n&7for each product.\n\n&7Front page data accounts for\n&7enchanted recipes.\n\n&eClick to toggle view!"))).toItemStack();

    public BazaarCategoryGui(Player player, BazaarCategory category, boolean advanced) {
        super("Bazaar ➜ " + category.getName(), 54, new HashMap<String, Runnable>() {{
            for (BazaarCategory cat : Skyblock.getPlugin().getBazaar().getCategories()) {
                if (cat.getName().equals(category.getName())) continue;

                put(cat.getColor() + cat.getName(), () -> {
                    new BazaarCategoryGui(player, cat, advanced).show(player);
                });
            }

            for (BazaarItem item : category.getItems()) {
                put(category.getColor() + item.getName(), () -> {
                    new BazaarItemGui(player, category, item).show(player);
                });
            }

            put(ChatColor.GREEN + "Search", () -> {
                // TODO: implement search
            });

            put(ChatColor.GREEN + "Sell Inventory Now", () -> {
                // TODO: implement sell inventory
            });

            put(ChatColor.AQUA + "Sell Sacks Now", () -> {
                // TODO: implement sell sacks
            });

            put(ChatColor.RED + "Close", player::closeInventory);

            put(ChatColor.GREEN + "Manage Orders", () -> {
                // TODO: implement manage orders
            });

            put(ChatColor.GREEN + "Direct Mode", () -> {
                new BazaarCategoryGui(player, category, true).show(player);
            });

            put(ChatColor.GREEN + "Advanced Mode", () -> {
                new BazaarCategoryGui(player, category, false).show(player);
            });
        }});

        int categoryIndex = 0;
        for (BazaarCategory cat : Skyblock.getPlugin().getBazaar().getCategories()) {
            if (!cat.getName().equals(category.getName())) {
                addItem(categoryIndex, new ItemBuilder(cat.getColor() + cat.getName(), cat.getIcon()).addLore(Arrays.asList(Util.buildLore(
                        "&8Category\n\n&eClick to view!"
                ))).addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).toItemStack());
            } else {
                addItem(categoryIndex, new ItemBuilder(cat.getColor() + cat.getName(), cat.getIcon()).addLore(Arrays.asList(Util.buildLore(
                        "&8Category\n\n&aCurrently viewing!"
                ))).addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).addEnchantmentGlint().toItemStack());
            }
            categoryIndex += 9;
        }

        addItem(45, new ItemBuilder(ChatColor.GREEN + "Search", Material.SIGN).addLore(Arrays.asList(Util.buildLore("&7Find products by name!\n\n&eClick to search!"))).toItemStack());
        addItem(47, new ItemBuilder(ChatColor.GREEN + "Sell Inventory Now", Material.CHEST).addLore(Arrays.asList(Util.buildLore(
                "&7Instantly sell anything in your\n&7inventory that can be sold on\n&7the Bazaar.\n\n" +
                        // TODO: dynamically change this:
                        "&cYou don't have items to sell!"
        ))).toItemStack());
        addItem(48, new ItemBuilder(ChatColor.AQUA + "Sell Sacks Now", Material.CAULDRON_ITEM).addLore(Arrays.asList(Util.buildLore(
                "&7Instantly sell everything in your\n&7sacks on the Bazaar.\n\n" +
                        // TODO: dynamically change this:
                        "&a#&7x &f<#item> &7for &6# coins\n\n&7You earn: &6# coins" +
                        "\n\n&eClick to sell!"
        ))).toItemStack());
        addItem(49, Util.buildCloseButton());
        addItem(50, new ItemBuilder(ChatColor.GREEN + "Manage Orders", Material.BOOK).addLore(Arrays.asList(Util.buildLore(
                // TODO: dynamically change this:
                "&7You don't have any ongoing\n&7orders.\n\n&eClick to manage!"))).toItemStack());
        addItem(51, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 15).toItemStack());
        addItem(52, advanced ? advancedMode : directMode);

        Util.fillSidesLeftOneIndented(this, Material.STAINED_GLASS_PANE, category.getPaneColor());

        for (BazaarItem item : category.getItems()) {
            addItem(new ItemBuilder(category.getColor() + item.getName(), item.getSubItems().get(0).getIcon().getType()).addLore(Arrays.asList(Util.buildLore(
                    "&8" + item.getProductAmount() + " product" + (item.getProductAmount() == 1 ? "" : "s") + "\n\n" +
                            "&7Buy Price: &6" + Skyblock.getPlugin().getBazaar().getEscrow().getBuyPrice(item.getSubItems().get(0)) + " coins\n" +
                            "&7Sell Price: &6" + Skyblock.getPlugin().getBazaar().getEscrow().getSellPrice(item.getSubItems().get(0)) + " coins\n\n" +
                            "&eClick to view product!"
            ))).addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).toItemStack());
        }
    }

    public BazaarCategoryGui(Player player) {
        this(player, Objects.requireNonNull(Skyblock.getPlugin().getBazaar().getCategories().stream().filter(category -> category.getName().equals("Farming")).findFirst().orElse(null)), false);
    }

}
