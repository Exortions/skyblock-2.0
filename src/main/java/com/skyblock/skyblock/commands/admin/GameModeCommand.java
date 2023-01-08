package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.TrueAlias;
import com.skyblock.skyblock.utilities.command.annotations.*;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@RequiresPlayer
@Permission(permission = "skyblock.admin")
@Usage(usage = "Change gamemode")
@Alias(aliases = { "gm", "gmode" })
@Description(description = "Vanilla gamemode command too slow")
public class GameModeCommand implements Command, TrueAlias<GameModeCommand> {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length != 1) return;

        switch (args[0]) {
            case "0":
            case "s":
            case "survival":
                player.setGameMode(GameMode.SURVIVAL);
                break;
            case "1":
            case "c":
            case "creative":
                player.setGameMode(GameMode.CREATIVE);
                break;
            case "2":
            case "a":
            case "adventure":
                player.setGameMode(GameMode.ADVENTURE);
                break;
            case "3":
            case "sp":
            case "spectator":
                player.setGameMode(GameMode.SPECTATOR);
                break;
        }

        player.sendMessage(String.format(ChatColor.GREEN + "Your gamemode has been updated to %s", ChatColor.YELLOW + player.getGameMode().name().toLowerCase()));
    }
}
