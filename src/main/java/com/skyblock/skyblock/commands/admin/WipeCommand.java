package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Permission;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;

@RequiresPlayer
@Permission(permission = "skyblock.admin")
@Usage(usage = "/sb wipe player")
@Description(description = "Wipes a players account")
public class WipeCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length != 1) { sendUsage(player); return; }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target.isOnline()) ((Player) target).kickPlayer(ChatColor.RED + "Wipe occurring");

        File playerData = new File(Skyblock.getPlugin().getDataFolder() + File.separator + "players" + File.separator + target.getUniqueId() + ".yml");
        playerData.delete();

        player.sendMessage(ChatColor.GREEN + "Successfully wiped " + target.getName());
    }
}
