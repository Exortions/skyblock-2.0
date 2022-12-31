package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.MiningMinionType;
import com.skyblock.skyblock.features.minions.MiningMinion;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(SkyblockPlayer.getPlayer(event.getPlayer()).isNotOnPrivateIsland());

        if (!SkyblockPlayer.getPlayer(event.getPlayer()).isNotOnPrivateIsland()) {
            ItemStack item = event.getItemInHand();

            if (item.getItemMeta().hasDisplayName()) {
                String display = event.getItemInHand().getItemMeta().getDisplayName();

                if (item.getItemMeta().getDisplayName().contains("Minion")) {
                    new MiningMinion(MiningMinionType.COBBLESTONE).spawn(SkyblockPlayer.getPlayer(event.getPlayer()), event.getBlock().getLocation().clone().add(0.5, 0, 0.5), Util.romanToDecimal(display.split(" ")[display.split(" ").length - 1]));
                    event.getPlayer().sendMessage(ChatColor.AQUA + "You placed a minion! (%s/%s)");
                    event.getPlayer().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                }
            }
        }
    }

}
