package com.skyblock.skyblock.features.pets;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PetListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        ItemStack item = e.getItem();

        if (!Util.notNull(item)) return;

        NBTItem nbtItem = new NBTItem(item);

        if (nbtItem.getBoolean("isPet")) {
            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(e.getPlayer());
            skyblockPlayer.getBukkitPlayer().playSound(skyblockPlayer.getBukkitPlayer().getLocation(), Sound.ORB_PICKUP, 10, 1);
            skyblockPlayer.addPet(item);

            skyblockPlayer.getBukkitPlayer().setItemInHand(null);

            skyblockPlayer.getBukkitPlayer().sendMessage(ChatColor.GREEN + "Successfully addded " + item.getItemMeta().getDisplayName() + ChatColor.GREEN + " to your pet menu!");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!Util.notNull(e.getItemInHand())) return;
        NBTItem item = new NBTItem(e.getItemInHand());

        if (e.getBlock().getWorld().getName().startsWith(IslandManager.ISLAND_PREFIX)) e.setCancelled(item.getBoolean("isPet"));
    }
}
