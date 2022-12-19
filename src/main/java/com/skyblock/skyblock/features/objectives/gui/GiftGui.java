package com.skyblock.skyblock.features.objectives.gui;

import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class GiftGui extends Gui {
    public GiftGui(Player player, ItemStack reward) {
        super("Claim Reward", 54, new HashMap<>());

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
            player.getInventory().addItem(reward);
            player.closeInventory();
        });
    }
}
