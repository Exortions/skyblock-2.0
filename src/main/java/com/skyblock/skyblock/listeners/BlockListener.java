package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(!SkyblockPlayer.getPlayer(event.getPlayer()).isOnPrivateIsland());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(!SkyblockPlayer.getPlayer(event.getPlayer()).isOnPrivateIsland());
    }

}
