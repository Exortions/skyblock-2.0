package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SkyblockPlayer.registerPlayer(player.getUniqueId());

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
    }
}
