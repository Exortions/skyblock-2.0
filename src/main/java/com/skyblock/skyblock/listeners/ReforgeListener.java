package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.Reforge;
import com.skyblock.skyblock.features.reforge.ReforgeHandler;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ReforgeListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        if (event.getInventory() == null) return;
        if (!event.getInventory().getTitle().equals("Reforge Item")) return;
        if (event.getInventory().getItem(13) == null) return;

        Player player = (Player) event.getPlayer();

        player.getInventory().addItem(event.getInventory().getItem(13));
    }

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

                ItemStack reforgeItem;

                if (isReforgeable || newItem.getType().equals(Material.AIR)) {
                    ItemBuilder builder = new ItemBuilder(ChatColor.YELLOW + "Reforge Item", Material.ANVIL)
                            .addLore(Util.buildLore("&7Place an item above to reforge\n&7it! Reforging items adds a\n&7random modifier to the item that\n&7grants stat boosts."))
                            .addNBT("reforge", true);

                    if (!newItem.getType().equals(Material.AIR)) {
                        int cost = Util.calculateReforgeCost(newItem);

                        builder.addLore(
                                Util.buildLore("\n&7Cost\n&6" + Util.formatInt(cost) + " Coin" + (cost == 1 ? "" : "s") + "\n\n&eClick to reforge!")
                        );
                    }

                    reforgeItem = builder.toItemStack();
                } else {
                    reforgeItem = new ItemBuilder(ChatColor.RED + "Error!", Material.BARRIER)
                            .addLore(Util.buildLore("&7You cannot reforge this item!"))
                            .addNBT("reforge", true)
                            .toItemStack();
                }

                event.getClickedInventory().setItem(22, reforgeItem);
                event.getClickedInventory().setItem(13, newItem);

                player.updateInventory();

                if (!isPickup) player.setItemOnCursor(old);
                else {
                    player.getInventory().addItem(old);

                    player.updateInventory();
                }

                if (!(isReforgeable && !item.getType().equals(Material.AIR))) {
                    Util.fillSides45Slots(event.getClickedInventory(), Material.STAINED_GLASS_PANE, 5);
                } else {
                    Util.fillSides45Slots(event.getClickedInventory(), Material.STAINED_GLASS_PANE, 14);
                }

                return;
            }
        }

        NBTItem nbt = new NBTItem(item);

        if (nbt.getBoolean("close").equals(true)) {
            player.closeInventory();
            return;
        }

        if (event.getSlot() == 22) {
            if (item.getType().equals(Material.BARRIER)) return;

            if (item.getItemMeta().getLore().stream().noneMatch(s -> s.contains("Click to reforge!"))) return;

            List<Reforge> reforges = Skyblock.getPlugin(Skyblock.class).getReforgeHandler().getRegisteredReforges(new ItemBase(event.getClickedInventory().getItem(13)).getReforge());

            ItemBase base = new ItemBase(event.getClickedInventory().getItem(13));

            ReforgeHandler handler = Skyblock.getPlugin(Skyblock.class).getReforgeHandler();

            List<Reforge> validReforges =
                    reforges.stream()
                    .filter(reforge -> handler.getReforge(reforge).getApplicable().equals(base.getItem()))
                    .collect(Collectors.toList());

            player.sendMessage(base.getItem().toString());

            if (validReforges.size() <= 0) {
                player.sendMessage(ChatColor.RED + "There are no availiable reforges that can be applied to this item!");
                return;
            }

            Color leatherColor = null;

            if (item.getItemMeta() instanceof LeatherArmorMeta) {
                leatherColor = ((LeatherArmorMeta) item.getItemMeta()).getColor();
            }

            Reforge reforge = null;
            try {
                Random random = Skyblock.getPlugin(Skyblock.class).getRandom();

                int size = validReforges.size();
                int index = random.nextInt(size);

                reforge = validReforges.get(index);
            } catch (ArrayIndexOutOfBoundsException ex) {
                player.sendMessage(ChatColor.RED + "Failed to get reforge: " + ex.getMessage());
            }
            int cost = Util.calculateReforgeCost(event.getClickedInventory().getItem(13));

            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

            double purse = skyblockPlayer.getCoins();

            if (purse < cost) {
                player.sendMessage(ChatColor.RED + "You do not have enough coins to reforge this item!");
                return;
            }

            skyblockPlayer.setValue("stats.purse", purse - cost);

            String oldName = event.getClickedInventory().getItem(13).getItemMeta().getDisplayName();

            ItemStack newItem = ItemBase.reforge(event.getClickedInventory().getItem(13), reforge);

            if (leatherColor != null) {
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

                meta.setColor(leatherColor);

                newItem.setItemMeta(meta);
            }

            event.getClickedInventory().setItem(13, newItem);

            player.updateInventory();

            player.sendMessage(ChatColor.GREEN + "You reforged your " + oldName + ChatColor.GREEN + " into a " + newItem.getItemMeta().getDisplayName() + ChatColor.GREEN + "!");
        }
    }

}
