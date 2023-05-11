package com.skyblock.skyblock.commands.economy;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb withdraw <amount|all|half|20%>")
@Description(description = "Withdraw money from your bank account")
public class WithdrawCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /sb withdraw <amount|all|half|20%>");
            return;
        }

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        double amount;

        if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("half") || args[0].equalsIgnoreCase("20%")) {
            amount = skyblockPlayer.getDouble("bank.balance");

            if (args[0].equalsIgnoreCase("half")) {
                amount /= 2;
            } else if (args[0].equalsIgnoreCase("20%")) {
                amount /= 5;
            }

            if (amount == 0) {
                player.sendMessage(ChatColor.RED + "You don't have any money in your bank account!");
                return;
            }
        } else {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Usage: /sb withdraw <amount|all|half|20%>");
                return;
            }
        }

        if (amount < 0) {
            player.sendMessage(ChatColor.RED + "You can't withdraw a negative amount of money!");
            return;
        }

        boolean success = skyblockPlayer.withdraw(amount);

        if (!success) {
            player.sendMessage(ChatColor.RED + "You don't have enough money in your bank account!");
        } else {
            player.sendMessage(ChatColor.GREEN + "Withdrew " + ChatColor.GOLD + Util.abbreviate(amount) + " coins" + ChatColor.GREEN + "! " +
                    "There's now " + ChatColor.GOLD + Util.abbreviate(skyblockPlayer.getDouble("bank.balance")) + " coins" + ChatColor.GREEN + " in the account!");

            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);

            if ((boolean) skyblockPlayer.getExtraData("personalBankUsed")) {
                skyblockPlayer.setExtraData("personalBankUsed", false);
                skyblockPlayer.setExtraData("personalBankLastUsed", System.currentTimeMillis());

                Util.delay(player::closeInventory, 2);
            }
        }
    }
}
