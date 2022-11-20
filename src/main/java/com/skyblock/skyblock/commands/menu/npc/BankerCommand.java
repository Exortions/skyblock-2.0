package com.skyblock.skyblock.commands.menu.npc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@RequiresPlayer
@Usage(usage = "/sb banker")
@Description(description = "Opens the banker menu")
public class BankerCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        Inventory inventory = Bukkit.createInventory(null, 36, "Personal Bank Account");

        Util.fillEmpty(inventory);

        player.openInventory(inventory);
    }
}
