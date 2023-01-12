package com.skyblock.skyblock.commands.player;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.TrueAlias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb island")
@Description(description = "Teleports you to your island")
public class IslandCommand implements Command, TrueAlias<IslandCommand> {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        player.performCommand("sb warp home");
    }

}
