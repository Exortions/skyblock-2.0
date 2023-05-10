package com.skyblock.skyblock.features.objectives.starting;

import com.skyblock.skyblock.features.objectives.Objective;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleporterObjective extends Objective {
    public TeleporterObjective() {
        super("teleporter", "Use the teleporter");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!isThisObjective(e.getPlayer())) return;

        if (e.getTo().getBlock().getType().equals(Material.PORTAL)) complete(e.getPlayer());
    }
}
