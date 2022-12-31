package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb createspawner type subType x1 y1 z1 x2 y2 z2 amount limit delay")
public class CreateSpawnerCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        plugin.getEntitySpawnerHandler().addSpawner(args[0], args[1], new Location(player.getWorld(), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])), new Location(player.getWorld(), Integer.parseInt(args[5]), Integer.parseInt(args[6]), Integer.parseInt(args[7])), Material.valueOf(args[8]), Integer.parseInt(args[9]), Integer.parseInt(args[10]),  Integer.parseInt(args[11]));
    }
}
