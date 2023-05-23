package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.sql.SQLConfiguration;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Permission;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

@Usage(usage = "/sb wipe <player>")
@Permission(permission = "skyblock.admin")
@Description(description = "Wipes a player's account")
public class WipeCommand implements Command {

    @Override
    public void execute(CommandSender sender, String[] args, Skyblock plugin) {
        if (args.length != 1) { sendUsage(sender); return; }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target.isOnline()) ((Player) target).kickPlayer(ChatColor.RED + "Your account has been wiped!");

        File playerData = new File(Skyblock.getPlugin().getDataFolder() + File.separator + "players" + File.separator + target.getUniqueId() + ".yml");

        boolean success = IslandManager.deleteWorld(target.getUniqueId()) && playerData.delete() && SQLConfiguration.delete(target.getUniqueId());

        sender.sendMessage(success ? ChatColor.GREEN + "Successfully wiped " + target.getName() : ChatColor.RED + "Failed to wipe " + target.getName());
    }
}
