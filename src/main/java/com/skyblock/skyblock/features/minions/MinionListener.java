package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.crafting.gui.RecipeGUI;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.minions.items.MinionFuel;
import com.skyblock.skyblock.features.minions.items.MinionItem;
import com.skyblock.skyblock.features.minions.items.MinionItemHandler;
import com.skyblock.skyblock.features.minions.items.MinionItemType;
import com.skyblock.skyblock.features.minions.items.storages.Storage;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public class MinionListener implements Listener {

    @EventHandler
    public void onRightClickMinion(PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer() == null || event.getRightClicked() == null) return;

        if (!event.getRightClicked().hasMetadata("minion")) return;

        event.setCancelled(true);

        UUID minionId = UUID.fromString(event.getRightClicked().getMetadata("minion_id").get(0).asString());

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (player == null || !player.getBukkitPlayer().getWorld().getName().equals(IslandManager.ISLAND_PREFIX + player.getBukkitPlayer().getUniqueId().toString())) return;

        MinionBase minion = Skyblock.getPlugin().getMinionHandler().getMinion(minionId);

        if (minion == null) return;

        minion.showInventory(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getWhoClicked() == null || event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;
        
        MinionItemHandler mih = Skyblock.getPlugin().getMinionItemHandler();
        ItemStack current = event.getCurrentItem();
        SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getWhoClicked());
        Block targetBlock = player.getBukkitPlayer().getTargetBlock((Set<Material>) null, 4);
        boolean isMinionStorage = targetBlock.hasMetadata("minion_id"); 
        boolean isMinionInv = event.getClickedInventory().getName().contains("Minion") && !isMinionStorage;
        if (!player.getBukkitPlayer().getOpenInventory().getTopInventory().getName().contains("Minion")) return;

        MinionBase minion = null;
        if (!isMinionStorage) {
            for (MinionHandler.MinionSerializable serializable :
                    Skyblock.getPlugin().getMinionHandler().getMinions().get(player.getBukkitPlayer().getUniqueId())) {
                if (serializable.getBase().getGui().equals(player.getBukkitPlayer().getOpenInventory().getTopInventory())) {
                    minion = serializable.getBase();
                    break;
                }
            }
        } else {
            for (MinionHandler.MinionSerializable serializable :
                    Skyblock.getPlugin().getMinionHandler().getMinions().get(player.getBukkitPlayer().getUniqueId())) {
                if (serializable.getBase().getUuid().toString().equals(targetBlock.getMetadata("minion_id").get(0).asString())) {
                    minion = serializable.getBase();
                    break;
                }
            }
        }

        if (minion == null) return;

        event.setCancelled(true);

        if (isMinionInv || isMinionStorage) {
            if (mih.isRegistered(current) && !isMinionStorage) { //take upgrades
                boolean remove = mih.getRegistered(current).onItemClick(player.getBukkitPlayer(), current);
                if (remove) {
                    for (int i = 0; i < minion.minionItems.length; ++i) {
                        if (minion.minionItems[i] != null && minion.minionItems[i].isThisItem(current)) {
                            if (!(minion.minionItems[i] instanceof MinionFuel && ((MinionFuel) minion.minionItems[i]).duration != -1))
                                player.getBukkitPlayer().getInventory().addItem(mih.getRegistered(current).getItem());
                            
                            minion.minionItems[i].onUnEquip(minion);
                            minion.minionItems[i] = null;
                            break;
                        }
                    }
                    minion.showInventory(player);
                }
            } else {
                if (current.getItemMeta().hasDisplayName()) {
                    if (current.getItemMeta().getDisplayName().contains("Collect All")) {
                        minion.collectAll(player);
                        return;
                    }

                    if (current.getItemMeta().getDisplayName().contains("Next Tier") && minion.getLevel() < 11) {
                        new RecipeGUI(Skyblock.getPlugin().getItemHandler().getItem(minion.getMaterial().name() + "_GENERATOR_" + (minion.getLevel() + 1) + ".json"), null, minion.getGui(), player.getBukkitPlayer()).show(player.getBukkitPlayer());
                    }

                    if (current.getItemMeta().getDisplayName().contains("Quick-Upgrade") && current.getItemMeta().hasLore() && current.getItemMeta().getLore().stream().anyMatch((s) -> s.contains("Click to upgrade!"))) {
                        NBTItem nbt = new NBTItem(current);
                        minion.upgrade(player, minion.level + 1, nbt.getString("item"), nbt.getInteger("amount"));
                    }
                }

                if (current.getType().equals(Material.BEDROCK)) minion.pickup(player, minion.getMinion().getLocation());

                if (new NBTItem(current).hasKey("slot")) { //withdraw chest items
                    if (player.getBukkitPlayer().getInventory().firstEmpty() == -1) {
                        player.getBukkitPlayer().sendMessage(ChatColor.RED + "Your inventory does not have enough free space to add all items!");
                        return;
                    }

                    minion.collect(player, new NBTItem(current).getInteger("slot"));

                    if (isMinionInv) {
                        minion.showInventory(player);
                    }
                    else {
                        ((Storage) minion.minionItems[minion.getItemSlots(MinionItemType.STORAGE).get(0)]).openInventory((Chest) targetBlock.getState(), player.getBukkitPlayer());
                    }
                }
            }
        } else if (mih.isRegistered(current)) { //add upgrades
            MinionItem item = mih.getRegistered(current);
            boolean takeAll = false;

            if (!item.stackable) {
                for (int i = 0; i < minion.minionItems.length; ++i) {
                    if (minion.minionItems[i] != null && minion.minionItems[i].isThisItem(current)) return;
                }
            }

            for (int i : minion.getItemSlots(item.getType()) ) {
                if (minion.minionItems[i] == null && item.guiEquippable) {
                    if (item instanceof MinionFuel) {
                        if (item.stackable && current.getAmount() > 1) {
                            minion.fuelAmount += current.getAmount();
                            takeAll = true;
                        }
                        else
                            minion.fuelAmount = 1;

                        minion.fuelAddedTime = System.currentTimeMillis() / 60000;
                    }
                    minion.minionItems[i] = item;
                    minion.minionItems[i].onEquip(minion);
                    minion.showInventory(player);
                    break;
                }
            }

            if (current.getAmount() > 1 && !takeAll) 
                current.setAmount(current.getAmount() - 1);
            else
                player.getBukkitPlayer().getInventory().clear(event.getSlot());
        }
    }

}
