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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuctionBidsGUI extends Gui {

    private static final AuctionHouse ah = Skyblock.getPlugin().getAuctionHouse();

    public AuctionBidsGUI(Player player) {
        super("Your Bids", 27, new HashMap<>());

        Util.fillBorder(this);

        for (int i = getSlots() - 9; i < getSlots(); i++) {
            addItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 15).toItemStack());
        }

        List<Auction> auctions = ah.getBiddedAuctions(player);
        List<Auction> collected = new ArrayList<>();
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
        ended.addLore(ChatColor.DARK_GRAY + "Ended Auctions", " ", ChatColor.GRAY + "You got " + ChatColor.GREEN + endedAuctions + " item" + (endedAuctions != 1 ? "s" : "") + ChatColor.GRAY + " to", ChatColor.GRAY + "claim items/reclaim bids.", " ", ChatColor.YELLOW + "Click to claim!");

        if (endedAuctions > 0) addItem(21, ended.toItemStack());

        clickEvents.put(ChatColor.GREEN + "Claim All", () -> {
            for (Auction auction : auctions) {
                if (auction.isExpired()) auction.claim(player);
            }
            player.closeInventory();
        });

        addItem(22, Util.buildBackButton("&7To Auction House"));

        auctions.sort((o1, o2) -> (int) (o2.getPrice() - o1.getPrice()));

        int j = 0;
        for (int i = 0; i < 54; i++) {
            if (getItems().containsKey(i)) continue;

            if (j == auctions.size()) break;

            Auction auction = auctions.get(j);

            addItem(i, auction.getDisplayItem(false, false));

            specificClickEvents.put(getItems().get(i), () -> {
                new AuctionInspectGUI(auction, player).show(player);
            });

            j++;
        }
    }
}
