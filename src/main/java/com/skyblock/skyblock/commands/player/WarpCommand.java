package com.skyblock.skyblock.commands.player;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.TrueAlias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

@RequiresPlayer
@Usage(usage = "/sb warp <name>")
@Description(description = "Sends you to a location")
public class WarpCommand implements Command, TrueAlias<WarpCommand> {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length == 0) {
            // fast travel menu

            return;
        }

        String warpName = args[0];

        HashMap<String, Location> warps = new HashMap<>();

        warps.put("hub", new Location(Skyblock.getSkyblockWorld(), -2 , 70,  -84,  -180, 0));
        warps.put("home", new Location(IslandManager.getIsland(player), 0, 100, 0));

        if (warps.containsKey(warpName)) {
            player.teleport(warps.get(warpName));

            return;
        }

        // Operator Warps
        if (player.isOp()) {
            player.teleport(Util.getSpawnLocation(warpName));
        }
    }
}
