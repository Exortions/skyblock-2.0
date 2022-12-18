package com.skyblock.skyblock.features.objectives.impl.starting;

import com.skyblock.skyblock.event.SkyblockCraftEvent;
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
    public void onCraft(SkyblockCraftEvent e) {
        if (!isThisObjective(e.getPlayer())) return;

        if (e.getRecipe().getResult().getType().equals(Material.WORKBENCH)) complete(e.getPlayer());
    }
}
