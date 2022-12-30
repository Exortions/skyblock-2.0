package com.skyblock.skyblock.features.auction.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionBid;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

public class AuctionInspectGUI extends Gui {

    public AuctionInspectGUI(Auction auction, Player opener) {
        super((auction == null ? "" : (auction.isBIN() ? "BIN Auction House" : "Auction House")), 54, new HashMap<>());

        if (auction == null) {
            Skyblock.getPlugin().getAuctionHouse().refreshDisplays();
            return;
        }

        Util.fillEmpty(this);

        addItem(49, Util.buildBackButton("&7To Auction House"));
        addItem(13, auction.getDisplayItem(true, auction.isOwn(opener)));

        boolean own = auction.isOwn(opener);
        SkyblockPlayer player = SkyblockPlayer.getPlayer(opener);

        long bid = auction.nextBid();

        // screw ui, from: https://github.com/superischroma/Spectaculation/blob/main/src/main/java/me/superischroma/spectaculation/gui/AuctionViewGUI.java
        if (auction.isBIN()) {
            if (auction.isExpired()) {
                int topBidAmount = auction.getTopBid().getAmount();
                ItemBuilder collect = new ItemBuilder(ChatColor.GOLD + "Collect Auction", auction.getBidHistory().size() != 0 ? Material.GOLD_BLOCK : Material.GOLD_NUGGET);

                if (auction.getBidHistory().size() == 0) {
                    if (auction.isBIN())
                        collect.addLore(ChatColor.GRAY + "No one has bought your item.");
                    else
                        collect.addLore((ChatColor.GRAY + "No one has bid on your item."));
                    collect.addLore(ChatColor.GREEN + "You may pick it back up.", " ", ChatColor.YELLOW + "Click to pick up item!");
                } else {
                    collect.addLore(ChatColor.GRAY + "Item sold to " + ChatColor.GREEN + auction.getTopBidder().getName(), ChatColor.GRAY + "for " + ChatColor.GOLD + topBidAmount + " coin" + (topBidAmount != 1 ? "s" : "") + ChatColor.GRAY + "!", " ", ChatColor.YELLOW + "Click to collect coins!");
                }

                addItem(31, collect.toItemStack());

                clickEvents.put(getName(collect), () -> {
                    auction.claim(opener);
                    opener.closeInventory();
                });
            } else {
                Material icon = player.getCoins() < bid || own ? Material.POTATO_ITEM : Material.GOLD_NUGGET;
                if (auction.getTopBidder() != null && auction.getTopBidder().equals(opener)) icon = Material.GOLD_BLOCK;
                ItemBuilder buy = new ItemBuilder(ChatColor.GOLD + (auction.isBIN() ? "Buy Item Right Now" : "Submit Bid"), icon);

                buy.addLore(" ");
                if (auction.isBIN())
                    buy.addLore(ChatColor.GRAY + "Price: " + ChatColor.GOLD + Util.formatInt((int) auction.getPrice()) + " coin" + (auction.getPrice() != 1 ? "s" : ""));
                else
                {
                    buy.addLore(ChatColor.GRAY + "New bid: " + ChatColor.GOLD + Util.formatInt((int) bid) + " coin" + (bid != 1 ? "s" : ""));
                    AuctionBid auctionBid = auction.getBid(opener);
                    buy.addLore(ChatColor.GRAY + "Your previous bid: " + ChatColor.YELLOW + Util.formatInt(auctionBid.getAmount()) + " coin" + (auctionBid.getAmount() != 1 ? "s" : ""));
                }
                buy.addLore(" ");
                OfflinePlayer top = auction.getTopBidder();
                if (own)
                    buy.addLore(ChatColor.GREEN + "This is your own auction!");
                else if (top != null && top.equals(opener))
                    buy.addLore(ChatColor.GREEN + "Already top bid!");
                else if (player.getCoins() < bid)
                    buy.addLore(ChatColor.RED + "Cannot afford bid!");
                else
                    buy.addLore(ChatColor.YELLOW + "Click to " + (auction.isBIN() ? "buy" : "bid") + "!");

                addItem(own && auction.getBidHistory().size() == 0 ? 29 : 31, buy.toItemStack());

                clickEvents.put(getName(buy), () -> {
                    if (own) {
                        opener.sendMessage(ChatColor.RED + "This is your own auction!");
                        return;
                    }
                    if (auction.isExpired())
                    {
                        opener.sendMessage(ChatColor.RED + "The item you are trying to bid on has already expired!");
                        opener.closeInventory();
                        return;
                    }
                    OfflinePlayer topBidder = auction.getTopBidder();
                    if (top != null && topBidder.equals(opener))
                    {
                        opener.sendMessage(ChatColor.GREEN + "You are already top bid!");
                        return;
                    }
                    if (player.getCoins() < bid)
                    {
                        opener.sendMessage(ChatColor.RED + "You cannot afford this bid!");
                        return;
                    }
                    new BidConfirmationGUI(auction, bid, opener).show(opener);
                });
            }

            if (own && auction.getBidHistory().size() == 0 && !auction.isExpired()) {
                ItemBuilder cancel = new ItemBuilder(ChatColor.RED + "Cancel Auction", Material.STAINED_CLAY, (short) 14);
                addItem(33, cancel.addLore("&7You may cancel auctions as", "&7long as they have " + ChatColor.RED + "0 &7bids!", " ", ChatColor.YELLOW + "Click to cancel auction!").toItemStack());

                specificClickEvents.put(getItem(33), () -> {
                    if (auction.getBidHistory().size() > 0) {
                        opener.sendMessage(ChatColor.RED + "You cannot cancel this auction!");
                        return;
                    }

                    auction.claim(opener);
                    opener.closeInventory();
                });
            }
        } else {
            if (auction.isExpired()) {
                ItemBuilder collect = new ItemBuilder(ChatColor.GOLD + "Collect Auction", auction.getBidHistory().size() != 0 ? Material.GOLD_BLOCK : Material.GOLD_NUGGET);

                collect.addLore(" ");
                if (auction.getBidHistory().size() == 0) {
                    if (auction.isBIN()) {
                        collect.addLore(ChatColor.GRAY + "No one has bought your item.");
                    } else {
                        collect.addLore(ChatColor.GRAY + "No one has bid on your item.");
                    }

                    collect.addLore(ChatColor.GREEN + "You may pick it back up.", " ", ChatColor.YELLOW + "Click to pick up item!");
                } else {
                    collect.addLore(ChatColor.GRAY + "Item sold to " + ChatColor.GREEN + auction.getTopBidder().getName(), ChatColor.GRAY + "for " + ChatColor.GOLD + bid + " coin" + (bid != 1 ? "s" : "") + ChatColor.GRAY + "!", " ", ChatColor.YELLOW + "Click to collect coins!");
                }

                addItem(29, collect.toItemStack());

                clickEvents.put(getName(collect), () -> {
                    auction.claim(opener);
                    opener.closeInventory();
                });
            } else {
                ItemBuilder buy = new ItemBuilder(ChatColor.GOLD + (auction.isBIN() ? "Buy Item Right Now" : "Submit Bid"), player.getCoins() < bid || own ? Material.POTATO_ITEM : Material.GOLD_NUGGET);

                buy.addLore(" ");
                if (auction.isBIN())
                    buy.addLore(ChatColor.GRAY + "Price: " + ChatColor.GOLD + Util.formatInt((int) auction.getPrice()) + " coin" + (auction.getPrice() != 1 ? "s" : ""));
                else
                    buy.addLore(ChatColor.GRAY + "New bid: " + ChatColor.GOLD + Util.formatInt((int) bid) + " coin" + (bid != 1 ? "s" : ""));
                buy.addLore(" ");
                if (own)
                    buy.addLore(ChatColor.GREEN + "This is your own auction!");
                else if (player.getCoins() < bid)
                    buy.addLore(ChatColor.RED + "Cannot afford purchase!");
                else
                    buy.addLore(ChatColor.YELLOW + "Click to " + (auction.isBIN() ? "buy" : "bid") + "!");

                addItem(29, buy.toItemStack());

                clickEvents.put(getName(buy), () -> {
                    if (own) {
                        opener.sendMessage(ChatColor.RED + "This is your own auction!");
                        return;
                    }
                    if (auction.isExpired())
                    {
                        opener.sendMessage(ChatColor.RED + "The item you are trying to bid on has already expired!");
                        opener.closeInventory();
                        return;
                    }
                    OfflinePlayer topBidder = auction.getTopBidder();
                    if (auction.getTopBidder() != null && topBidder.equals(opener))
                    {
                        opener.sendMessage(ChatColor.GREEN + "You are already top bid!");
                        return;
                    }
                    if (player.getCoins() < bid)
                    {
                        opener.sendMessage(ChatColor.RED + "You cannot afford this bid!");
                        return;
                    }
                    new BidConfirmationGUI(auction, bid, opener).show(opener);
                });
            }

            if (own && auction.getBidHistory().size() == 0 && !auction.isExpired()) {
                ItemBuilder cancel = new ItemBuilder(ChatColor.RED + "Cancel Auction", Material.STAINED_CLAY, (short) 14);
                addItem(31, cancel.addLore("&7You may cancel auctions as", "&7long as they have " + ChatColor.RED + "0 &7bids!", " ", ChatColor.YELLOW + "Click to cancel auction!").toItemStack());
            }

            if (!own && !auction.isExpired() && auction.nextBid() <= player.getCoins()) {
                String display = ChatColor.GOLD + Util.formatInt((int) bid) + " coin" + (bid != 1 ? "s" : "");
                addItem(31, new ItemBuilder(ChatColor.WHITE + "Bid Amount: " + display, Material.GOLD_INGOT).addLore(ChatColor.GRAY + "You need to bid at least", display + ChatColor.GRAY + " to hold", ChatColor.GRAY + "the top bid on this", ChatColor.GRAY + "auction.", " ", ChatColor.GRAY + "The " + ChatColor.YELLOW + "top bid" + ChatColor.GRAY + " on auction", ChatColor.GRAY + "ends wins the item.", " ", ChatColor.GRAY + "If you do not win, you can", ChatColor.GRAY + "claim your bid coins back.", " ", ChatColor.YELLOW + "Click to edit amount!").toItemStack());
            }

            ItemBuilder history = new ItemBuilder(ChatColor.WHITE + "Bid History", Material.MAP);

            if (auction.getBidHistory().size() > 0) {
                history.addLore(ChatColor.GRAY + "Total bids: " + ChatColor.GREEN + auction.getBidHistory().size() + " bid" + (auction.getBidHistory().size() != 1 ? "s" : ""));
                for (int i = auction.getBidHistory().size() - 1; i >= 0; i--) {
                    AuctionBid auctionBid = auction.getBidHistory().get(i);

                    long time = auctionBid.getTimeStamp();

                    Date date = new Date(time);

                    String timeString = Util.calculateTimeAgoWithPeriodAndDuration(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), ZoneId.systemDefault());

                    history.addLore(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "------------", ChatColor.GRAY + "Bid: " + ChatColor.GOLD + auctionBid.getAmount() + " coin" + (auctionBid.getAmount() != 1 ? "s" : ""), ChatColor.GRAY + "By: " + ChatColor.GREEN + auctionBid.getBidder().getName(), ChatColor.AQUA + timeString);
                }
            } else {
                history.addLore(ChatColor.GRAY + "No bids have been placed on", ChatColor.GRAY + "this item yet.", " ", ChatColor.GRAY + "Be the first to bid on it!");
            }

            addItem(33, history.toItemStack());
        }
    }

    private String getName(ItemBuilder builder) {
        return builder.toItemStack().getItemMeta().getDisplayName();
    }
}
