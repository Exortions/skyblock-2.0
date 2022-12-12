package com.skyblock.skyblock.features.auction.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionHouse;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AuctionHouseGUI extends Gui {

    public AuctionHouseGUI(Player player) {
        super("Auction House", 36, new HashMap<String, Runnable>() {{
            put(ChatColor.GOLD + "Auctions Browser", () -> {
                new AuctionBrowserGUI(player).show(player);
            });
        }});

        Util.fillEmpty(this);

        addItem(31, Util.buildCloseButton());

        AuctionHouse auctionHouse = Skyblock.getPlugin().getAuctionHouse();

        List<Auction> bidded = auctionHouse.getBiddedAuctions(player);
        List<Auction> topped = auctionHouse.getToppedAuctions(player);
        List<Auction> own = auctionHouse.getOwnedAuctions(player);

        int owned = own.size();
        int top = topped.size();
        int bid = bidded.size();

        AtomicInteger bidAmount = new AtomicInteger();
        AtomicLong amount = new AtomicLong();

        own.forEach((a) -> {
            bidAmount.addAndGet(a.getBidHistory().size());
            amount.addAndGet(a.getPrice());
        });

        ItemBuilder your = new ItemBuilder(ChatColor.GREEN + (owned > 0 ? "Manage Auctions" : "Create Auction"), Material.GOLD_BARDING);

        clickEvents.put(ChatColor.GREEN + (owned > 0 ? "Manage Auctions" : "Create Auction"), () -> {
           new AuctionManagingGUI(player).show(player);
        });

        if (owned > 0) {
            your.addLore("&7You own " + ChatColor.YELLOW + owned + " auction" + (owned != 1 ? "s" : "") + " &7in", "&7progress or which recently", "&7ended", " ", "&7Players can find your auctions", "&7using the Auctions Browser", "&7or using " + ChatColor.GREEN + "/ah " + player.getName());
        } else {
            your.addLore("&7Set your own items on auction", "&7for other players to purchase", " ", ChatColor.YELLOW + "Click to become rich!");
        }

        ItemBuilder bids = new ItemBuilder(ChatColor.YELLOW + "View Bids", Material.GOLDEN_CARROT);

        if (bid > 0) {
            bids.addLore("&7You placed " + ChatColor.GREEN + bid + " bids &7on", "&7pending auctions", " ", "&7You hold the top bid on", "&7(" + ChatColor.YELLOW + top + "&7/" + ChatColor.YELLOW + bid + "&7) of these", "&7auctions");
        } else {
            bids.addLore("&7You don't have any outstanding", "&7bids.", " ", "&7Use the Auctions Browser to", "&7find some cool items.");
        }

        clickEvents.put(ChatColor.YELLOW + "View Bids", () -> {
            new AuctionBidsGUI(player).show(player);
        });

        addItem(11, new ItemBuilder(ChatColor.GOLD + "Auctions Browser", Material.GOLD_BLOCK).addLore("&7Find items for sale by players", "&7across Hypixel Skyblock", " ", "&7Items offered here are for", ChatColor.GOLD + "auction &7, meaning you have to", "&7place the top bid to acquire", "&7them!", " ", ChatColor.YELLOW + "Click to browse!").toItemStack());
        addItem(13, bids.toItemStack());
        addItem(15, your.toItemStack());
    }
}
