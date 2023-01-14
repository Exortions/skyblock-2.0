package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Permission;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiresPlayer
@Permission(permission = "skyblock.admin")
@Usage(usage = "/sb heal <amount>")
@Description(description = "Heals player for a certain amount of hp")
public class HealCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        SkyblockPlayer sb = SkyblockPlayer.getPlayer(player);

        if (args.length < 1) { player.performCommand("sb heal " + sb.getStat(SkyblockStat.MAX_HEALTH)); return; }

        try {
            int health = Integer.parseInt(args[0]);
            sb.heal(health);

            player.sendMessage(ChatColor.GREEN + "You have been healed for " + health + " health.");
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid health!");
        }
    }
}
