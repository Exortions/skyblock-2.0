package com.skyblock.skyblock.commands.player;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Usage(usage = "/sb playerdata <player> <path> <value>")
@Description(description = "Edits the config value of a player")
public class PlayerDataCommand implements Command {

    @Override
    public void execute(CommandSender sender, String[] args, Skyblock plugin) {
        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target.isOnline()) {
                SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(target);
                skyblockPlayer.setValue(args[1], Integer.valueOf(args[2]));
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid args");
        }
    }
}
