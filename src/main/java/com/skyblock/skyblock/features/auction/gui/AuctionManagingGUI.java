package com.skyblock.skyblock.features.auction.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionHouse;
import com.skyblock.skyblock.features.auction.AuctionSettings;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class AuctionManagingGUI extends Gui {

    private static final AuctionHouse ah = Skyblock.getPlugin().getAuctionHouse();

    public AuctionManagingGUI(Player player) {
        super("Manage Auctions", 27, new HashMap<>());

        Util.fillBorder(this);

        for (int i = getSlots() - 9; i < getSlots(); i++) {
            addItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 15).toItemStack());
        }

        List<Auction> auctions = ah.getOwnedAuctions(player);
        List<Auction> collected = new ArrayList<>();

        AuctionSettings settings = SkyblockPlayer.getPlayer(player).getAuctionSettings();

        auctions.sort((o1, o2) -> {
            switch (settings.getSort()) {
                case HIGHEST:
                    return (int) (o2.getPrice() - o1.getPrice());
                case LOWEST:
                    return (int) (o1.getPrice() - o2.getPrice());
                case ENDING:
                    return (int) (o1.getTimeLeft() - o2.getTimeLeft());
                case MOST:
                    return (o2.getBidHistory().size() - o1.getBidHistory().size());
            }

            return 0;
        });

        int endedAuctions = 0;

        for (Auction auction : auctions) {
            if (auction.claimed(player)) {
                collected.add(auction);
                continue;
            }

            if (auction.isExpired()) endedAuctions++;
        }

        collected.forEach(auctions::remove);

        ItemBuilder ended = new ItemBuilder(ChatColor.GREEN + "Claim All", Material.CAULDRON_ITEM);
        ended.addLore(ChatColor.DARK_GRAY + "Ended Auctions", " ", ChatColor.GRAY + "You got " + ChatColor.GREEN + endedAuctions + " item" + (endedAuctions != 1 ? "s" : "") + ChatColor.GRAY + " to", ChatColor.GRAY + "collect sales/reclaim items.", " ", ChatColor.YELLOW + "Click to claim!");

        if (endedAuctions > 0) addItem(21, ended.toItemStack());

        clickEvents.put(ChatColor.GREEN + "Claim All", () -> {
            for (Auction auction : auctions) {
                if (auction.isExpired()) auction.claim(player);
            }

            player.closeInventory();
        });

        addItem(22, Util.buildBackButton("&7To Auction House"));

        List<String> sortList = new ArrayList<>(Arrays.asList("Highest Bid", "Lowest Bid", "Ending soon", "Most Bids"));
        sortList.forEach((s) -> {
            if (s.split(" ")[0].toUpperCase().equals(settings.getSort().name())) {
                sortList.set(sortList.indexOf(s), ChatColor.AQUA + s);
            } else {
                sortList.set(sortList.indexOf(s), ChatColor.GRAY + s);
            }
        });

        addItem(23, new ItemBuilder(ChatColor.GREEN + "Sort", Material.HOPPER).addLore(" ").addLore(sortList).addLore(" ", ChatColor.YELLOW + "Click to switch!").toItemStack());

        clickEvents.put(ChatColor.GREEN + "Sort", () -> {
            settings.incrementSort();
            new AuctionManagingGUI(player).show(player);
        });

        addItem(24, new ItemBuilder(ChatColor.GREEN + "Create Auction", Material.GOLD_BARDING).addLore(ChatColor.GRAY + "Set your own items on", ChatColor.GRAY + "auction for other players", ChatColor.GRAY + "to purchase.", " ", ChatColor.YELLOW + "Click to become rich!").toItemStack());

        clickEvents.put(ChatColor.GREEN + "Create Auction", () -> {
            if (auctions.size() >= 7) {
                player.sendMessage(ChatColor.RED + "You cannot create any more auctions!");
                return;
            }

            new AuctionCreationGUI(player).show(player);
        });

        int j = 0;
        for (int i = 0; i < 54; i++) {
            if (getItems().containsKey(i)) continue;

            if (j == auctions.size()) break;

            Auction auction = auctions.get(j);

            addItem(i, auction.getDisplayItem(false, true));

            specificClickEvents.put(getItems().get(i), () -> {
                new AuctionInspectGUI(auction, player).show(player);
            });

            j++;
        }
    }
}
