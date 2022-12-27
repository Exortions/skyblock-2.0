package com.skyblock.skyblock.features.entities;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SlimeSplitEvent;

public class EntityListener implements Listener {

    @EventHandler
    public void onSplit(SlimeSplitEvent e) {
        e.setCancelled(true);
    }

}
