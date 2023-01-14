package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.ArgumentAlias;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.TrueAlias;
import com.skyblock.skyblock.utilities.command.annotations.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;

@RequiresPlayer
@Alias(aliases = { "gm", "gmode" })
@Permission(permission = "skyblock.admin")
@Description(description = "Vanilla gamemode command too slow")
@Usage(usage = "/sb gamemode <0|s|survival|1|c|creative|2|a|adventure|3|sp|spectator>")
public class GameModeCommand implements Command, ArgumentAlias, TrueAlias<GameModeCommand> {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length != 1)  { sendUsage(player); return; }

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

    @Override
    public HashMap<String, String> getArgumentAliases() {
        return new HashMap<String, String>() {{
            put("gms", "s");
            put("gmc", "c");
            put("gma", "a");
            put("gmsp", "sp");
        }};
    }
}
