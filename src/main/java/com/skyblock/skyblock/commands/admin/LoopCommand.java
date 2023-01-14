package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Permission;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

@RequiresPlayer
@Usage(usage = "/sb loop /<command>")
@Permission(permission = "skyblock.admin")
@Description(description = "Loops commands")
public class LoopCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length < 2) { sendUsage(player); return; }

        try {
            int amount = Integer.parseInt(args[0]);
            LinkedList<String> arguments = new LinkedList<>(Arrays.asList(args));
            arguments.removeFirst();

            String command = arguments.getFirst();
            arguments.removeFirst();

            command = command.replaceAll("/", "");

            StringBuilder argStr = new StringBuilder();

            for (String s : arguments) argStr.append(" ").append(s);

            Bukkit.broadcastMessage(command + argStr);

            for (int i = 0; i < amount; i++) player.performCommand(command + argStr);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount!");
        }
    }
}
