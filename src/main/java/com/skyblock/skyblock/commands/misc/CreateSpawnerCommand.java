package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb createspawner <type> <subType> <limit> <delay>")
public class CreateSpawnerCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        plugin.getEntitySpawnerHandler().addSpawner(args[0], args[1], player.getLocation(), 1, Integer.parseInt(args[2]), Integer.parseInt(args[3]), 1);
    }
}
