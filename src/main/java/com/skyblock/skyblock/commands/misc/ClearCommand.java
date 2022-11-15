package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Permission;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Usage(usage = "/sb clear [player]")
@Permission(permission = "skyblock.clear")
@Description(description = "Clears the chat.")
public class ClearCommand implements Command {

    @Override
    public void execute(CommandSender sender, String[] args, Skyblock plugin) {
        if (args.length == 0) {
            Bukkit.broadcastMessage(StringUtils.repeat(" \n", 100));
        } else if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Could not find player '" + args[0] + "'.");
                return;
            }

            player.sendMessage(StringUtils.repeat(" \n", 100));
        }
    }
}
