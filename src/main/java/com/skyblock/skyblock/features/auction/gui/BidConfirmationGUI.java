package com.skyblock.skyblock.features.auction.gui;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BidConfirmationGUI extends Gui {

    public BidConfirmationGUI(Auction auction, long amount, Player player) {
        super("Confirm " + (auction.isBIN() ? "Purchase" : "Bid"), 27, new HashMap<String, Runnable>() {{
            put(ChatColor.GREEN + "Confirm", () -> {
                auction.bid(player, amount);
                player.closeInventory();
                if (!auction.isBIN()) {
                    player.sendMessage(ChatColor.YELLOW + "Bid placed on " + auction.getItem().getItemMeta().getDisplayName() + ChatColor.YELLOW + ".");
                }

                player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);
            });

            put(ChatColor.RED + "Cancel", player::closeInventory);
        }});

        int count = auction.getItem().getAmount();

        addItem(11, new ItemBuilder(ChatColor.GREEN + "Confirm", Material.STAINED_CLAY, 1, (short) 13).addLore(ChatColor.GRAY + (auction.isBIN() ? "Purchasing" : "Bidding on") + ": " + (count != 1 ? count + "x " : "") + auction.getItem().getItemMeta().getDisplayName(),
                ChatColor.GRAY + "Cost: " + ChatColor.GOLD + Util.formatInt((int) amount) + " coin" + (amount != 1 ? "s" : "")).toItemStack());

        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add((count != 1 ? count + "x " : "") + auction.getItem().getItemMeta().getDisplayName());
        lore.addAll(auction.getItem().getItemMeta().getLore());

        addItem(13, new ItemBuilder(ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "BIDDING ON ITEM", auction.getItem().getType()).addLore(lore).toItemStack());

        addItem(15, new ItemBuilder(ChatColor.RED + "Cancel", Material.STAINED_CLAY, 1, (short) 14).toItemStack());
    }
}
