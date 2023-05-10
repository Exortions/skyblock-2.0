package com.skyblock.skyblock.features.objectives.starting;

import com.skyblock.skyblock.events.SkyblockPlayerCraftEvent;
import com.skyblock.skyblock.features.objectives.Objective;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;

public class WorkbenchObjective extends Objective {

    public WorkbenchObjective() {
        super("craft_workbench", "Craft a workbench");
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (!isThisObjective((Player) e.getWhoClicked())) return;

        if (e.getRecipe().getResult().getType().equals(Material.WORKBENCH)) complete((Player) e.getWhoClicked());
    }
    @EventHandler
    public void onCraft(SkyblockPlayerCraftEvent e) {
        if (!isThisObjective(e.getPlayer())) return;

        if (e.getRecipe().getResult().getType().equals(Material.WORKBENCH)) complete(e.getPlayer());
    }
}
