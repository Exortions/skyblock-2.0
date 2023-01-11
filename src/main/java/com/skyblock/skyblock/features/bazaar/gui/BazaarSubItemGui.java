package com.skyblock.skyblock.features.bazaar.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.bazaar.Bazaar;
import com.skyblock.skyblock.features.bazaar.BazaarSubItem;
import com.skyblock.skyblock.features.bazaar.escrow.EscrowTransaction;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BazaarSubItemGui extends Gui {

    public BazaarSubItemGui(Player player, BazaarSubItem item) {
        super(item.getParent().getCategory().getName() + " ➜ " + ChatColor.stripColor(item.getNamedIcon().toItemStack().getItemMeta().getDisplayName()), 36, new HashMap<String, Runnable>() {{
            Runnable back = () -> new BazaarCategoryGui(player, item.getParent().getCategory(), false).show(player);

            put(ChatColor.GREEN + "Go Back", back);
            put(ChatColor.GOLD + "Go Back", back);
            put(ChatColor.GREEN + "Manage Orders", () -> {
                // TODO: implement manage orders
            });
            put(ChatColor.GREEN + "View Graphs", () -> {
                // TODO: implement graphs
            });

            put(ChatColor.GREEN + "Buy Instantly", () -> new BazaarInstantBuyGui(player, item, 0).show(player));
        }});

        Util.fillEmpty(this);

        String name = ChatColor.stripColor(item.getNamedIcon().toItemStack().getItemMeta().getDisplayName());
        AtomicInteger amountToSell = new AtomicInteger();
        Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).map(stack -> new ItemBuilder(stack.clone()).setDisplayName(ChatColor.stripColor(stack.getItemMeta().getDisplayName())).toItemStack()).filter(stack -> stack.isSimilar(new ItemBuilder(item.getItem().clone()).setDisplayName(ChatColor.stripColor(item.getItem().getItemMeta().getDisplayName())).toItemStack())).forEach(stack -> amountToSell.addAndGet(stack.getAmount()));

        List<EscrowTransaction> top6SellOrders = Skyblock.getPlugin().getBazaar().getEscrow().getRankedSellOrders().stream().filter(transaction -> transaction.getSubItem().equals(item)).limit(6).collect(Collectors.toList());
        List<EscrowTransaction> top6BuyOrders = Skyblock.getPlugin().getBazaar().getEscrow().getRankedBuyOrders().stream().filter(transaction -> transaction.getSubItem().equals(item)).limit(6).collect(Collectors.toList());

        this.addItem(10, new ItemBuilder(ChatColor.GREEN + "Buy Instantly", Material.GOLD_BARDING).addLore(Arrays.asList(Util.buildLore(
                "&8" + name + "\n\n" +
                        "&7Price per unit: &6" + Skyblock.getPlugin().getBazaar().getEscrow().getBuyPrice(item) + "\n" +
                        "&7Stack price: &6" + Skyblock.getPlugin().getBazaar().getEscrow().getBuyPrice(item) * 64 + "\n\n" +
                        "&eClick to pick amount!"
        ))).toItemStack());

        this.addItem(11, new ItemBuilder(ChatColor.GOLD + "Sell Instantly", Material.HOPPER).addLore(Arrays.asList(Util.buildLore(
                "&8" + name + "\n\n" +
                        "&7Inventory: &a" + amountToSell.get() + "\n\n" +
                        "&7Amount: &a" + amountToSell.get() + "&7x\n" +
                        "&7Total: &6" + Skyblock.getPlugin().getBazaar().getEscrow().getSellPrice(item) * amountToSell.get() + " coins\n" +
                        "&8Current tax: " + Bazaar.BAZAAR_TAX + "%\n\n" +
                        "&bRight-Click to pick amount!\n&eClick to sell inventory!"
        ))).toItemStack());

        this.addItem(13, item.getItem());

        this.addItem(15, new ItemBuilder(ChatColor.GREEN + "Create Buy Order", Material.MAP).addLore(Arrays.asList(Util.buildLore(
                "&8" + name + "\n\n" +
                        "&aTop Orders:\n" + (top6BuyOrders.isEmpty() ? "&c No buy orders!" : top6BuyOrders.stream().map(transaction -> "&8- &6" + transaction.getPrice() + " coins&7 each | &a" + transaction.getAmount() + "&7x from &f1 &7offer").collect(Collectors.joining("\n"))) +
                        (top6BuyOrders.isEmpty() ? "" : "\n&eClick to setup buy order!")
        ))).toItemStack());

        this.addItem(16, new ItemBuilder(ChatColor.GOLD + "Create Sell Offer", Material.EMPTY_MAP).addLore(Arrays.asList(Util.buildLore(
                "&8" + name + "\n\n" +
                        "&6Top Offers:\n" + (top6SellOrders.isEmpty() ? "&c No sell orders!" : top6SellOrders.stream().map(transaction -> "&8- &6" + transaction.getPrice() + " coins&7 each | &a" + transaction.getAmount() + "&7x from &f1 &7offer").collect(Collectors.joining("\n"))) +
                        (top6SellOrders.isEmpty() ? "" : (amountToSell.get() < 1 ? "\n&8None in inventory!" : "\n&eClick to setup sell order!"))
        ))).toItemStack());
    }

}
