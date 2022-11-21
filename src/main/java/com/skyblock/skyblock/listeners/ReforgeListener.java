package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ReforgeListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        if (event.getClickedInventory() == null) return;
        if ((event.getCurrentItem() == null && event.getCursor() == null)) return;
        if (event.getCurrentItem() == null && event.getCursor() == null) return;
        if (!event.getClickedInventory().getTitle().equals("Reforge Item")) return;

        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();

        event.setCancelled(event.getSlot() != 13);

        if (event.getSlot() == 13) {
            if (event.getCursor() == null || event.getCursor().getType().equals(Material.AIR)) return;

            boolean canReforge = Util.isItemReforgeable(event.getCursor());

            if (canReforge) {
                event.getClickedInventory().setItem(22, new ItemBuilder(ChatColor.YELLOW + "Reforge Item", Material.ANVIL).addLore(Util.buildLore("&7Place an item above to reforge\n&7it! Reforging items adds a\n&7random modifier to the item that\n&7grants stat boosts.")).addNBT("reforge", true).toItemStack());
            } else {
                event.getClickedInventory().setItem(22, new ItemBuilder(ChatColor.RED + "Error!", Material.BARRIER).addLore(Util.buildLore("&7You cannot reforge this item!")).addNBT("reforge", true).toItemStack());
            }

            event.setCancelled(false);

            InventoryClickEvent updater = new InventoryClickEvent(event.getView(), InventoryType.SlotType.CONTAINER, 1, ClickType.LEFT, event.getAction(), event.getHotbarButton());

            Bukkit.getPluginManager().callEvent(updater);

            return;
        }

        NBTItem nbt = new NBTItem(item);

        if (nbt.getBoolean("close").equals(true)) {
            player.closeInventory();
            return;
        }

        if (nbt.getBoolean("reforge").equals(true)) {
            if (item.getType().equals(Material.BARRIER)) return;

            player.sendMessage("reforge");
        }
    }

}
