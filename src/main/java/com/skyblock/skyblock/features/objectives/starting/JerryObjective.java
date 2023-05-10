package com.skyblock.skyblock.features.objectives.starting;

import com.skyblock.skyblock.features.objectives.Objective;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class JerryObjective extends Objective {

    public JerryObjective() {
        super("jerry", "Talk to Jerry");
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (!isThisObjective(e.getPlayer())) return;

        if (e.getRightClicked().getType().equals(EntityType.VILLAGER)) complete(e.getPlayer());
    }
}
