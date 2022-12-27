package com.skyblock.skyblock.features.trades.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.trades.Trade;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TradeGui extends Gui {

    public TradeGui(Player opener) {
        super("Trades", 54, new HashMap<String, Runnable>() {{

        }});

        Util.fillBorder(this);

        this.addItem(48, Util.buildBackButton("To SkyBlock Menu"));
        this.addItem(49, Util.buildCloseButton());

        List<Trade> trades = Skyblock.getPlugin().getTradeHandler().getTrades();

        for (Trade trade : trades) {
            this.addItem(trade.toItem());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() == null || event.getClickedInventory() == null || !Objects.equals(event.getClickedInventory().getTitle(), this.getName())) return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        ItemStack item = event.getCurrentItem();
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
