package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

    private final Skyblock plugin;
    public PlayerListener(Skyblock skyblock) {
        plugin = skyblock;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SkyblockPlayer.registerPlayer(player.getUniqueId());

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()){
                    skyblockPlayer.tick();
                }else{
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 5L, 1);
    }
}
