package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Permission;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiresPlayer
@Permission(permission = "skyblock.admin")
@Usage(usage = "/sb coins set/add/remove value")
@Description(description = "Modify player coins")
public class CoinsCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length < 2) {
            player.performCommand("sb coins set " + args[0]);
            return;
        }

        SkyblockPlayer sb = SkyblockPlayer.getPlayer(player);
        switch (args[0]) {
            case "add":
                sb.addCoins(Long.parseLong(args[1]));
                break;
            case "remove":
                sb.subtractCoins(Long.parseLong(args[1]));
                break;
            case "set":
                sb.setValue("stats.purse", Long.parseLong(args[1]));
                break;
        }

        player.sendMessage(ChatColor.GRAY + "Successfully modified coins.");
    }
}
