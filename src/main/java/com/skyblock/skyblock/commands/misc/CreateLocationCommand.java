package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb createlocation <color> <weight> <x1> <y1> <z1> <x2> <y2> <z2> <name>")
@Description(description = "Create a Skyblock location")
public class CreateLocationCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length < 9) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /sb createlocation <color> <weight> <x1> <y1> <z1> <x2> <y2> <z2> <name>");
            return;
        }

        ChatColor color = ChatColor.valueOf(args[0].toUpperCase());
        int weight = Integer.parseInt(args[1]);
        int x1 = Integer.parseInt(args[2]);
        int y1 = Integer.parseInt(args[3]);
        int z1 = Integer.parseInt(args[4]);
        int x2 = Integer.parseInt(args[5]);
        int y2 = Integer.parseInt(args[6]);
        int z2 = Integer.parseInt(args[7]);
        StringBuilder name = new StringBuilder();
        for (int i = 8; i < args.length; i++) name.append(args[i]).append(" ");

        Location pos1 = new Location(player.getWorld(), x1, y1, z1, 0, 0);
        Location pos2 = new Location(player.getWorld(), x2, y2, z2, 0, 0);

        plugin.getLocationManager().createLocation(pos1, pos2, name.toString().trim(), color, weight);

        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Successfully created location " + ChatColor.GOLD + name.toString().trim() + ChatColor.GREEN + "!");
    }

}
