package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.SkyblockEntityType;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Alias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiresPlayer
@Alias(aliases = { "spawn" })
@Usage(usage = "/sb summon <type> <name> [amount]")
@Description(description = "Summon a custom mob.")
public class SummonCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length >= 2) {
            int amount = args.length == 3 ? Integer.parseInt(args[2]) : 1;

            for (int i = 0; i < amount; i++) {
                try {
                    SkyblockEntityType.valueOf(args[0].toUpperCase()).getNewInstance(args[1]).spawn(player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Success!");
                } catch (NullPointerException ex) {
                    player.sendMessage(ChatColor.RED + "Could not find mob with identifier " + ChatColor.DARK_GRAY + "skyblock:" + args[0].toLowerCase() + "." + args[1].toLowerCase() + ChatColor.RED + "!");
                }
            }
        }
    }
}
