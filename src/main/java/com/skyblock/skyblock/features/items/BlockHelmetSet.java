package com.skyblock.skyblock.features.items;

import com.skyblock.skyblock.utilities.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class BlockHelmetSet extends ArmorSet {
    public BlockHelmetSet(String helmet, String chest, String legs, String boots, String id) {
        super(helmet, chest, legs, boots, id);
    }

    public BlockHelmetSet(ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots, String id) {
        super(helmet, chest, legs, boots, id);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getClickedInventory().equals(e.getWhoClicked().getInventory())) return;
        if (e.getCursor() == null) return;
        if (e.getSlot() != 39) return;

        ItemStack cursor = e.getCursor().clone();
        Player player = (Player) e.getWhoClicked();

        if (Util.getSkyblockId(e.getCursor()).equalsIgnoreCase(Util.getSkyblockId(getHelmet()))) {
            e.setCancelled(true);
            player.setItemOnCursor(e.getCurrentItem());
            player.getInventory().setHelmet(cursor);
        }
    }
}
