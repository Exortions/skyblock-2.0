package com.skyblock.skyblock.commands.merchant;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.merchants.Merchant;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb spawnmerchant <id>")
@Description(description = "Spawns a merchant with the given id.")
public class SpawnMerchantCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length != 1) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /sb spawnmerchant <id>");
            return;
        }

        String id = args[0];

        Merchant merchant = plugin.getMerchantHandler().getMerchant(id);

        if (merchant == null) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "No merchant with the id " + id + " exists.");
            return;
        }

        merchant.createNpc();
    }
}
