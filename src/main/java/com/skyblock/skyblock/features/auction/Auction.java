package com.skyblock.skyblock.features.auction;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import lombok.Data;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

@Data
public class Auction {

    private ItemStack item;
    private OfflinePlayer seller;
    private OfflinePlayer topBidder;
    private long price;
    private long timeLeft;
    private boolean isBIN;
    private boolean sold;
    private UUID uuid;
    private List<OfflinePlayer> participants;
    private List<AuctionBid> bidHistory;
    private AuctionCategory category;
    private boolean fake = false;

    public Auction(ItemStack item, OfflinePlayer seller, OfflinePlayer topBidder, long price, long timeLeft, boolean isBIN, boolean sold, UUID uuid, List<AuctionBid> bidHistory, List<OfflinePlayer> participants) {
        this.item = item;
        this.seller = seller;
        this.topBidder = topBidder;
        this.price = price;
        this.timeLeft = timeLeft;
        this.isBIN = isBIN;
        this.sold = sold;
        this.uuid = uuid;
        this.bidHistory = bidHistory;
        this.participants = participants;
    }


    public void tickPassed() {
        if (timeLeft > 0) this.timeLeft--;
    }

    public boolean isExpired() { return timeLeft == 0 || sold; }

    public AuctionBid getTopBid() {
        int max = 0;
        AuctionBid auctionBid = new AuctionBid(null, this, 0, 0);

        for (AuctionBid bid : bidHistory) {
            if (bid.getAmount() > max) {
                max = bid.getAmount();
                auctionBid = bid;
            }
        }

        return auctionBid;
    }

    public AuctionBid getBid(Player player) {
        for (int i = getBidHistory().size() - 1; i >= 0; i--) {
            AuctionBid bid = getBidHistory().get(i);
            if (bid.getBidder().equals(player)) return bid;
        }
        return null;
    }

    public OfflinePlayer getTopBidder() {
        if (getTopBid() != null)
            return getTopBid().getBidder();
        return null;
    }

    public long nextBid() {
        long top = getTopBid().getAmount();
        if (top == 0) return getPrice();
        return Math.round(top * 1.15D);
    }

    // screw ui, from: https://github.com/superischroma/Spectaculation/blob/main/src/main/java/me/superischroma/spectaculation/auction/AuctionItem.java
    public ItemStack getDisplayItem(boolean inspect, boolean yourAuction) {
        ItemStack stack = getItem().clone();
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------");
        lore.add(ChatColor.GRAY + "Seller: " + ChatColor.GREEN + getSeller().getName());
        OfflinePlayer top = (getTopBidder() != null ? getTopBidder() : null);
        if (isBIN())
            lore.add(ChatColor.GRAY + "Buy it now: " + ChatColor.GOLD + Util.formatInt((int) getPrice()) + " coins");
        else if (top == null)
            lore.add(ChatColor.GRAY + "Starting bid: " + ChatColor.GOLD + Util.formatInt((int) getPrice()) + " coins");
        else
        {
            lore.add(ChatColor.GRAY + "Bids: " + ChatColor.GREEN + getBidHistory().size() + " bids");
            lore.add(" ");
            lore.add(ChatColor.GRAY + "Top bid: " + ChatColor.GOLD + Util.formatInt((int) getPrice()) + " coins");
            lore.add(ChatColor.GRAY + "Bidder: " + ChatColor.GREEN + top.getName());
        }
        if (yourAuction)
        {
            lore.add(" ");
            lore.add(ChatColor.GREEN + "This is your own auction!");
        }
        lore.add(" ");
        lore.add(ChatColor.GRAY + "Ends in: " + ChatColor.YELLOW + formatTime(getTimeLeft() * 50));
        if (!inspect)
        {
            lore.add(" ");
            lore.add(ChatColor.YELLOW + "Click to inspect!");
        }
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public void claim(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);

        if (isOwn(player)) {
            if (bidHistory.size() == 0) {
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.YELLOW + "You collected " + getItem().getItemMeta().getDisplayName() + ChatColor.YELLOW + " back from an auction that nobody bought.");
            } else {
                skyblockPlayer.addCoins(getTopBid().getAmount());
                player.sendMessage(ChatColor.YELLOW + "You collected " + ChatColor.GOLD + Util.formatInt(getTopBid().getAmount()) + " coins " + ChatColor.YELLOW + " from selling " + getItem().getItemMeta().getDisplayName() + ChatColor.YELLOW + " to " + getTopBidder().getName() + ChatColor.YELLOW + " in an auction!");
            }

            removeBidderOrOwner(player);
            return;
        }
        AuctionBid top = getTopBid();

        if (top == null) return;

        AuctionBid bid = getBid(player);

        if (bid == null) return;

        if (top.getBidder().equals(player)) {
            player.getInventory().addItem(item);
            player.sendMessage(ChatColor.YELLOW + "You claimed " + getItem().getItemMeta().getDisplayName() + ChatColor.YELLOW + " from " + ChatColor.GREEN + getSeller().getName() + ChatColor.YELLOW + "'s auction!");
        } else {
            skyblockPlayer.addCoins(bid.getAmount());
            player.sendMessage(ChatColor.YELLOW + "You collected " + ChatColor.GOLD + Util.formatInt(getBid(player).getAmount()) + " coins " + ChatColor.YELLOW + "back from an auction which you didn't hold the top bid!");
        }

        removeBidderOrOwner(player);
    }

    private void removeBidderOrOwner(Player player) {
        participants.remove(player);

        if (participants.size() == 0) {
            Skyblock.getPlugin().getAuctionHouse().deleteAuction(this);
        }
    }

    public void bid(Player player, long amount) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        AuctionHouse.FAKE.remove(getUuid());
        setFake(false);

        AuctionBid prev = getBid(player);
        skyblockPlayer.subtractCoins((int) (amount - (prev != null ? prev.getAmount() : 0)));
        getBidHistory().add(new AuctionBid(player, this, (int) amount, System.currentTimeMillis()));

        participants.add(player);

        price = amount;

        setTopBidder(player);

        TextComponent message = new TextComponent(net.md_5.bungee.api.ChatColor.GOLD + "[Auction] " + net.md_5.bungee.api.ChatColor.GREEN + player.getName() + net.md_5.bungee.api.ChatColor.YELLOW + " bid " + net.md_5.bungee.api.ChatColor.GOLD + Util.formatInt((int) getPrice()) + " coin" + (getPrice() != 1 ? "s" : "") + net.md_5.bungee.api.ChatColor.YELLOW + " on " + getItem().getItemMeta().getDisplayName() + " ");

        ComponentBuilder cb = new ComponentBuilder("Click to inspect!").color(net.md_5.bungee.api.ChatColor.YELLOW);

        TextComponent click = new TextComponent("CLICK");
        click.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        click.setBold(true);
        click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb.create()));
        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sb auction " + uuid));

        if (isBIN) {
            sold = true;

            message = new TextComponent(net.md_5.bungee.api.ChatColor.GOLD + "[Auction] " + net.md_5.bungee.api.ChatColor.GREEN + player.getName() + net.md_5.bungee.api.ChatColor.YELLOW + " bought " + getItem().getItemMeta().getDisplayName() + net.md_5.bungee.api.ChatColor.YELLOW + " for " + net.md_5.bungee.api.ChatColor.GOLD + Util.formatInt((int) getPrice()) + " coin" + (getPrice() != 1 ? "s" : "") + " ");
            message.addExtra(click);

            sendToOwner(message);
            claim(player);
            return;
        }

        message.addExtra(click);

        sendToOwner(message);

        for (AuctionBid bid : getBidHistory()) {
            if (bid.getBidder().equals(player)) continue;
            OfflinePlayer otherBidder = bid.getBidder();
            if (otherBidder == null) continue;
            if (!otherBidder.isOnline()) continue;
            long diff = amount - bid.getAmount();

            message = new TextComponent(net.md_5.bungee.api.ChatColor.GOLD + "[Auction] " + net.md_5.bungee.api.ChatColor.GREEN + player.getName() + net.md_5.bungee.api.ChatColor.YELLOW + " outbid you by " + net.md_5.bungee.api.ChatColor.GOLD + diff + " coin" + (diff != 1 ? "s" : "") + net.md_5.bungee.api.ChatColor.YELLOW + " for " + item.getItemMeta().getDisplayName() + " ");
            message.addExtra(click);

            ((Player) otherBidder).spigot().sendMessage(message);
        }
    }

    public boolean claimed(Player player) {
        boolean found = false;

        for (OfflinePlayer p : participants) {
            if (p.getUniqueId().equals(player.getUniqueId())) {
                found = true;
                break;
            }
        }

        return !found;
    }

    public void sendToOwner(String message) {
        if (seller != null && seller.isOnline()) ((Player) seller).sendMessage(message);
    }

    public void sendToOwner(TextComponent message) {
        if (seller != null && seller.isOnline()) ((Player) seller).spigot().sendMessage(message);
    }

    public static String formatTime(long millis) {
        if (millis == 0)
            return "Ended!";
        if (millis >= 8.64E7)
            return Math.round(millis / 8.64E7) + "d";
        if (millis >= 2.16E7)
            return Math.round(millis / 3.6E6) + "h";
        long seconds = millis / 1000; // 86400
        long hours = seconds / 3600; // 24
        seconds -= hours * 3600; // 86400 - 84600 = 0
        long minutes = seconds / 60; // 0
        seconds -= minutes * 60; // 59 * 60 = 3540
        StringBuilder builder = new StringBuilder();
        if (hours > 0)
            builder.append(hours).append("h ");
        builder.append(minutes).append("m ").append(seconds).append("s");
        return builder.toString();
    }

    public boolean isOwn(Player opener) {
        return seller.getName().equalsIgnoreCase(opener.getName());
    }
}
