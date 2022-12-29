package com.skyblock.skyblock.features.auction.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionHouse;
import com.skyblock.skyblock.features.auction.AuctionSettings;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import com.skyblock.skyblock.utilities.sign.SignGui;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionCreationGUI extends Gui {

    private static final AuctionHouse ah = Skyblock.getPlugin().getAuctionHouse();

    public AuctionCreationGUI(Player player) {
        super("Create Auction", 54, new HashMap<>());

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        AuctionSettings settings = skyblockPlayer.getAuctionSettings();

        Util.fillEmpty(this);

        addItem(49, Util.buildBackButton("&7To Auction House"));

        ItemStack auctioning = (ItemStack) skyblockPlayer.getValue("auction.auctioningItem");

        if (auctioning != null) {
            ItemStack stack = auctioning.clone();
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>();

            ItemBase base = new ItemBase(auctioning);

            meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "AUCTION FOR ITEM:");

            lore.add(" ");
            lore.add(ChatColor.GRAY + "" + auctioning.getAmount() + "x " + auctioning.getItemMeta().getDisplayName());
            lore.add(base.getRarity());
            lore.add(" ");
            lore.add(ChatColor.YELLOW + "Click to pickup!");

            meta.setLore(lore);
            stack.setItemMeta(meta);

            addItem(13, stack);

            clickEvents.put(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "AUCTION FOR ITEM:", () -> {
                player.getInventory().addItem(auctioning);
                skyblockPlayer.setValue("auction.auctioningItem", null);
                player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
                new AuctionCreationGUI(player).show(player);
            });
        } else {
            addItem(13, new ItemBuilder(ChatColor.YELLOW + "Click an item in your inventory!", Material.STONE_BUTTON).addLore(ChatColor.GRAY + "Selects it for auction").toItemStack());
        }

        ItemBuilder create = new ItemBuilder((auctioning != null ? ChatColor.GREEN : ChatColor.RED) + "Create Auction", Material.STAINED_CLAY, 1,
                (auctioning != null ? (short) 13 : (short) 14));

        AtomicReference<AuctionProgress> atomic = new AtomicReference<>(skyblockPlayer.getProgress());

        if (atomic.get() == null) atomic.set(new AuctionProgress());

        skyblockPlayer.setProgress(atomic.get());

        AuctionProgress progress = skyblockPlayer.getProgress();

        if (auctioning == null) {
            create.addLore(ChatColor.GRAY + "No item selected!", " ", ChatColor.GRAY + "Click an item in your", ChatColor.GRAY + "inventory to select it for", ChatColor.GRAY + "this auction.");
        } else {
            create.addLore(ChatColor.GRAY + "This item will be added to", ChatColor.GRAY + "the auction house for other", ChatColor.GRAY + "players to purchase.", " ", ChatColor.GRAY + "Item: " + auctioning.getAmount() + "x " + auctioning.getItemMeta().getDisplayName(),
                    ChatColor.GRAY + "Auction duration: " + ChatColor.YELLOW + Auction.formatTime(progress.getDuration() * 50), ChatColor.GRAY + "Starting bid: " + ChatColor.GOLD + Util.formatInt(progress.getStarter()) + " coins", " ",
                    ChatColor.GRAY + "Creation fee: " + ChatColor.GOLD + Util.formatInt(progress.getFee(settings.getBin())) + " coins", " ", ChatColor.YELLOW + "Click to submit!");
        }

        addItem(29, create.toItemStack());

        clickEvents.put(getItems().get(29).getItemMeta().getDisplayName(), () -> {
            if (auctioning == null) return;
            if (skyblockPlayer.getCoins() < progress.getFee(settings.getBin())) {
                player.sendMessage(ChatColor.RED + "You don't have enough coins to afford this!");
                return;
            }

            ah.createAuction(auctioning, player, progress.getStarter(), progress.getDuration(), settings.getBin());
            skyblockPlayer.subtractCoins(progress.getFee(settings.getBin()));

            player.sendMessage(ChatColor.YELLOW + "Auction started for " + auctioning.getItemMeta().getDisplayName() + ChatColor.YELLOW + "!");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

            skyblockPlayer.setProgress(null);
            skyblockPlayer.setValue("auction.auctioningItem", null);

            player.closeInventory();
        });

        ItemBuilder bid = new ItemBuilder(ChatColor.WHITE + (settings.getBin() ? "Item price: " : "Starting bid: ") + ChatColor.GOLD + Util.formatInt(progress.getStarter()) + " coins",
                settings.getBin() ? Material.GOLD_INGOT : Material.POWERED_RAIL);

        List<String> lore = new ArrayList<>();
        if (settings.getBin()) {
            lore.add(ChatColor.GRAY + "The price at which you want");
            lore.add(ChatColor.GRAY + "to sell this item.");
        } else {
            lore.add(ChatColor.GRAY + "The minimum price a player");
            lore.add(ChatColor.GRAY + "can offer to obtain your");
            lore.add(ChatColor.GRAY + "item.");
            lore.add(" ");
            lore.add(ChatColor.GRAY + "Once a player bids for your");
            lore.add(ChatColor.GRAY + "item, other players will");
            lore.add(ChatColor.GRAY + "have until the auction ends");
            lore.add(ChatColor.GRAY + "to make a higher bid.");
        }
        lore.add(" ");
        lore.add(ChatColor.GRAY + "Extra fee: " + ChatColor.GOLD + "+" + Util.formatInt(progress.getFee(settings.getBin())) + " coins " +
                ChatColor.YELLOW + "(" + (settings.getBin() ? 1 : 5) + "%)");
        lore.add(" ");
        lore.add(ChatColor.YELLOW + "Click to edit!");

        bid.addLore(lore);

        addItem(31, bid.toItemStack());

        clickEvents.put(getItems().get(31).getItemMeta().getDisplayName(), () -> {
            SignGui sign = new SignGui(Skyblock.getPlugin().getSignManager(), e -> {
                try {
                    int num = Integer.parseInt(e.getLines()[0]);

                    progress.setStarter(num);
                    skyblockPlayer.setProgress(progress);

                    Util.delay(() -> {
                        new AuctionCreationGUI(player).show(player);
                    }, 1);
                } catch (NumberFormatException ignored) { }
            });

            sign.open(player);
        });

        ItemBuilder duration = new ItemBuilder(ChatColor.WHITE + "Duration: " + ChatColor.YELLOW + Auction.formatTime(progress.getDuration() * 50), Material.WATCH);

        if (settings.getBin()) {
            duration.addLore(ChatColor.GRAY + "How long the item will be", ChatColor.GRAY + "up for sale.");
        } else {
            duration.addLore(ChatColor.GRAY + "How long players will be", ChatColor.GRAY + "able to place bids for.", " ", ChatColor.GRAY + "Note: Bids automatically", ChatColor.GRAY + "increase the duration of", ChatColor.GRAY + "auctions.");
        }
        duration.addLore(" ", ChatColor.YELLOW + "Click to edit!");

        addItem(33, duration.toItemStack());

        clickEvents.put(getItems().get(33).getItemMeta().getDisplayName(), () -> {
            settings.switchBin();
            player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
            new AuctionDurationGUI(player).show(player);
        });

        if (settings.getBin()) {
            addItem(48, new ItemBuilder(ChatColor.GREEN + "Click to Auction", Material.POWERED_RAIL).addLore(ChatColor.GRAY + "With traditional auctions,", ChatColor.GRAY + "multiple buyers compete for the", ChatColor.GRAY + "item by bidding turn by turn.", " ",
                    ChatColor.YELLOW + "Click to switch!").toItemStack());
        } else {
            addItem(48, new ItemBuilder(ChatColor.GREEN + "Switch to BIN", Material.GOLD_INGOT).addLore(ChatColor.GRAY + "BIN Auctions are simple.", " ", ChatColor.GRAY + "Set a price, then one player may",
                    ChatColor.GRAY + "buy the item at that price.", " ", ChatColor.DARK_GRAY + "(BIN means Buy It Now)", " ", ChatColor.YELLOW + "Click to switch!").toItemStack());
        }

        clickEvents.put(getItems().get(48).getItemMeta().getDisplayName(), () -> {
            settings.switchBin();
            new AuctionCreationGUI(player).show(player);
        });
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(name)) return;
        if (!e.getClickedInventory().equals(e.getWhoClicked().getInventory())) return;

        ItemStack current = e.getCurrentItem();
        if (current == null) return;
        if (current.getType() == Material.AIR) return;

        Player player = (Player) e.getWhoClicked();

        try {
            new ItemBase(current);
        } catch (IllegalArgumentException ex) {
            player.sendMessage(ChatColor.RED + "You cannot auction this item!");
            player.closeInventory();
            return;
        }

        e.setCancelled(true);

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        skyblockPlayer.setValue("auction.auctioningItem", current);
        player.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));

        player.playSound(player.getLocation(), Sound.CLICK, 10, 1);

        new AuctionCreationGUI(player).show(player);
    }

    @Data
    @AllArgsConstructor
    public static class AuctionProgress {
        private long duration;
        private int starter;

        public AuctionProgress() {
            duration = 72000; // 1 hour (ticks)
            starter = 100;
        }

        public int getFee(boolean bin) {
            return (int) Math.round(starter * (bin ? 0.01 : 0.05));
        }
    }
}
