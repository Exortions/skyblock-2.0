package com.skyblock.skyblock.features.trades.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.trades.Trade;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TradeGui extends Gui {

    public TradeGui(Player opener) {
        super("Trades", 54, new HashMap<String, Runnable>() {{

        }});

        Util.fillBorder(this);

        this.addItem(48, Util.buildBackButton("&7To SkyBlock Menu"));
        this.addItem(49, Util.buildCloseButton());

        List<Trade> trades = Skyblock.getPlugin().getTradeHandler().getTrades();

        SkyblockPlayer player = SkyblockPlayer.getPlayer(opener);

        for (Trade trade : trades) {
            String name = trade.getItem();

            if (((ArrayList<String>) player.getValue("trades.unlocked")).contains(name)) this.addItem(trade.toItem());
            else {
                this.addItem(new ItemBuilder(ChatColor.RED + "???", Material.INK_SACK, 1, (short) 8)
                        .addLore(Util.buildLore("&7Progress through your item\n&7collections and explore the\n&7world to unlock new trades!")).toItemStack());
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() == null || event.getClickedInventory() == null || !Objects.equals(event.getClickedInventory().getTitle(), this.getName())) return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        ItemStack item = event.getCurrentItem();

        if (item.getType().equals(Material.BARRIER)) {
            event.getWhoClicked().closeInventory();
            return;
        }

        if (item.getType().equals(Material.ARROW)) {
            ((Player) event.getWhoClicked()).performCommand("sb menu");
            event.setCancelled(true);
            return;
        }

        if (item.getType().equals(Material.STAINED_GLASS_PANE)) {
            event.setCancelled(true);
            return;
        }

        if (item.getItemMeta().getDisplayName().equals(ChatColor.RED + "???")) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "This item is locked!");
            event.setCancelled(true);
            return;
        }

        NBTItem nbt = new NBTItem(item);

        if (!nbt.getBoolean("trade.is_trade")) return;

        String tradeItem = nbt.getString("trade.item");
        int tradeAmount = nbt.getInteger("trade.amount");
        String costType = nbt.getString("trade.cost_type");
        int costAmount = nbt.getInteger("trade.cost_amount");
        String costItem = nbt.getString("trade.cost_item");

        event.setCancelled(true);

        SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getWhoClicked());

        ItemStack trade = Util.getItem(tradeItem).clone();
        trade.setAmount(tradeAmount);

        String suffix = tradeAmount > 1 ? ChatColor.DARK_GRAY + " x" + tradeAmount : "";

        if (costType.equals("coin") || costItem.equals("NONE")) {
            if (player.getCoins() < costAmount) {
                player.getBukkitPlayer().sendMessage(ChatColor.RED + "You don't have the required items!");
                return;
            }

            player.subtractCoins(costAmount);
        } else {
            ItemStack cost = Util.getItem(costItem).clone();
            cost.setAmount(costAmount);

            if (!player.getBukkitPlayer().getInventory().containsAtLeast(cost, costAmount)) {
                player.getBukkitPlayer().sendMessage(ChatColor.RED + "You don't have the required items!");
                return;
            }

            player.getBukkitPlayer().getInventory().removeItem(cost);
        }

        player.getBukkitPlayer().getInventory().addItem(trade);
        player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You bought " + trade.getItemMeta().getDisplayName() + suffix + ChatColor.GREEN + "!");
    }

}
