package com.skyblock.skyblock.features.objectives.gui;

import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class GiftGui extends Gui {

    private boolean claimed = false;
    private final ItemStack reward;

    public GiftGui(Player player, ItemStack reward) {
        super("Claim Reward", 54, new HashMap<>());

        this.reward = reward;

        Util.fillEmpty(this);

        addItem(49, Util.buildCloseButton());

        ItemStack clone = reward.clone();
        ItemMeta meta = clone.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(" ");
        lore.add(ChatColor.YELLOW + "Click to claim!");
        meta.setLore(lore);
        clone.setItemMeta(meta);

        addItem(22, clone);

        specificClickEvents.put(clone, () -> {
            claimed = true;

            player.getInventory().addItem(reward);
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
            player.closeInventory();
        });
    }

    @Override
    public void onClose(Player p) {
        if (!claimed) {
            p.getInventory().addItem(reward);
        }
    }
}
