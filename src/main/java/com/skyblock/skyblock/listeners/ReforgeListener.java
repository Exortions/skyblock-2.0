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
import org.bukkit.inventory.ItemStack;

public class ReforgeListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().getTitle().equals("Reforge Item")) return;

        Player player = (Player) event.getWhoClicked();

        if ((event.getCurrentItem() == null && event.getCursor() == null)) return;
        if (event.getCurrentItem() == null && event.getCursor() == null) return;

        ItemStack item = event.getCurrentItem();

        event.setCancelled(event.getSlot() != 13);

        if (
                event.getAction().equals(InventoryAction.PICKUP_ALL)
                || event.getAction().equals(InventoryAction.PLACE_ALL)
                || event.getAction().equals(InventoryAction.PICKUP_ONE)
                || event.getAction().equals(InventoryAction.PICKUP_HALF)
                || event.getAction().equals(InventoryAction.PICKUP_SOME)
                || event.getAction().equals(InventoryAction.PLACE_ONE)
                || event.getAction().equals(InventoryAction.PLACE_SOME)
        ) {
            if (event.getSlot() == 13) {
                boolean isPickup = event.getAction().equals(InventoryAction.PICKUP_ALL)
                        && !event.getAction().equals(InventoryAction.PICKUP_ONE)
                        && !event.getAction().equals(InventoryAction.PICKUP_HALF)
                        && !event.getAction().equals(InventoryAction.PICKUP_SOME);

                event.setCancelled(
                        !isPickup
                );

                ItemStack old = event.getCurrentItem();
                ItemStack newItem = event.getCursor();

                if (newItem == null) return;

                boolean isReforgeable = true;

                if (!newItem.getType().equals(Material.AIR)) isReforgeable = Util.isItemReforgeable(newItem);

                ItemStack reforgeItem =
                        isReforgeable || newItem.getType().equals(Material.AIR) ?
                                new ItemBuilder(ChatColor.YELLOW + "Reforge Item", Material.ANVIL)
                                        .addLore(Util.buildLore("&7Place an item above to reforge\n&7it! Reforging items adds a\n&7random modifier to the item that\n&7grants stat boosts."))
                                        .toItemStack() :
                                new ItemBuilder(ChatColor.RED + "Error!", Material.BARRIER)
                                        .addLore(Util.buildLore("&7You cannot reforge this item!"))
                                        .toItemStack();

                event.getClickedInventory().setItem(22, reforgeItem);
                event.getClickedInventory().setItem(13, newItem);

                player.updateInventory();

                if (!isPickup) player.setItemOnCursor(old);
                else {
                    player.getInventory().addItem(old);

                    player.updateInventory();
                }


                return;
            }
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
