package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Permission;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Permission(permission = "skyblock.admin")
@Usage(usage = "/launch <player> <x> <y>")
@Description(description = "Launches a player in the air")
public class LaunchCommand implements Command {

    @Override
    public void execute(CommandSender sender, String[] args, Skyblock plugin) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /launch <player> <x> <y>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        double x;
        double y;

        try {
            x = Double.parseDouble(args[1]);
            y = Double.parseDouble(args[2]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Invalid number!");
            return;
        }

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        target.setVelocity(target.getLocation().getDirection().multiply(x).setY(y));
    }
}
