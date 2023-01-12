package com.skyblock.skyblock.commands.menu;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.crafting.gui.CraftingGUI;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb craft")
@Description(description = "Opens the crafting table")
public class CraftCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        player.openInventory(new CraftingGUI(player));
    }
}
