package com.skyblock.skyblock.features.auction.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import com.skyblock.skyblock.utilities.sign.SignClickCompleteHandler;
import com.skyblock.skyblock.utilities.sign.SignCompleteEvent;
import com.skyblock.skyblock.utilities.sign.SignGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class AuctionDurationGUI extends Gui {

    public AuctionDurationGUI(Player player) {
        super("Auction Duration", 36, new HashMap<>());

        Util.fillEmpty(this);

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        addItem(10, createTime((short) 14, 1, skyblockPlayer));
        addItem(11, createTime((short) 6, 6, skyblockPlayer));
        addItem(12, createTime((short) 1, 12, skyblockPlayer));
        addItem(13, createTime((short) 4, 24, skyblockPlayer));
        addItem(14, createTime((short) 0, 48, skyblockPlayer));

        addItem(16, new ItemBuilder(ChatColor.GREEN + "Custom Duration", Material.WATCH).addLore(ChatColor.GRAY + "Specify how long you want", ChatColor.GRAY + "the auction to last.",
                " ", ChatColor.AQUA + "Right-click for minutes!", ChatColor.YELLOW + "Click to set hours!").toItemStack());

        clickEvents.put(ChatColor.GREEN + "Custom Duration", () -> {
            SignGui sign = new SignGui(Skyblock.getPlugin().getSignManager(), event -> {
                long l = 0;
                try {
                    l = Long.parseLong(event.getLines()[0]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Could not read this number!");
                }
                skyblockPlayer.getProgress().setDuration(l * 3600000);
                new AuctionDurationGUI(player).show(player);
            });

            sign.open(player);
        });

        addItem(31, Util.buildBackButton("&7To Create Auction"));
        clickEvents.put(ChatColor.GREEN + "Go Back", () -> {
            new AuctionCreationGUI(player).show(player);
        });
    }

    private ItemStack createTime(short color, int hours, SkyblockPlayer skyblockPlayer) {
        long millis = hours * 3600000L;

        ItemBuilder builder = new ItemBuilder(ChatColor.GREEN + Auction.formatTime(millis), Material.STAINED_CLAY, color).addLore(ChatColor.YELLOW + "Click to pick!");

        clickEvents.put(ChatColor.GREEN + Auction.formatTime(millis), () -> {
            skyblockPlayer.getProgress().setDuration(millis / 50);
            new AuctionDurationGUI(skyblockPlayer.getBukkitPlayer()).show(skyblockPlayer.getBukkitPlayer());
        });

        if (millis / 50 == skyblockPlayer.getProgress().getDuration()) builder.addEnchantmentGlint();

        return builder.toItemStack();
    }
}
