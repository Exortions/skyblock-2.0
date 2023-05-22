package com.skyblock.skyblock.commands.menu.npc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiresPlayer
@Usage(usage = "/sb banker")
@SuppressWarnings("unchecked")
@Description(description = "Opens the banker menu")
public class BankerCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        String menu;

        if (args.length == 0) menu = "banker";
        else menu = args[0];

        Inventory inventory;

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        double balance = (double) skyblockPlayer.getValue("bank.balance");
        double purse = (double) skyblockPlayer.getValue("stats.purse");
        int interestRate = (int) skyblockPlayer.getValue("bank.interest");
        int nextInterestHours = 31; // TODO: Calculate this

        List<String> original = (List<String>) skyblockPlayer.getValue("bank.recent_transactions");

        List<String> recentTransactions = new ArrayList<>();
        for (int i = original.size() - 1; i >= 0; i--) {
            recentTransactions.add(original.get(i));
        }

        if (menu.equalsIgnoreCase("banker")) {
            inventory = Bukkit.createInventory(null, 36, "Personal Bank Account");

            Util.fillEmpty(inventory);

            inventory.setItem(11, new ItemBuilder(ChatColor.GREEN + "Deposit Coins", Material.CHEST).setLore(Util.buildLore(
                    "&7Current balance: &6" + Util.formatDouble(balance) + "\n\n&7Store coins in the bank to keep\n&7them safe while you go on\n&7adventures!\n\n" +
                            "&7You will earn &b" + interestRate + "% &7interest every\n&7season for your first &610 million\n&7banked coins.\n\n&7Until interest: &b" + nextInterestHours + "h\n\n&eClick to make a deposit!"
            )).toItemStack());

            inventory.setItem(13, new ItemBuilder(ChatColor.GREEN + "Withdraw Coins", Material.DROPPER).setLore(Util.buildLore(
                    "&7Current balance: &6" + Util.formatDouble(balance) + "\n\n&7Take your coins out of the bank\n&7in order to spend them.\n\n&eClick to withdraw coins!"
            )).toItemStack());

            if (recentTransactions.size() == 0) {
                inventory.setItem(15, new ItemBuilder(ChatColor.GREEN + "Recent Transactions", Material.PAPER).setLore(Util.buildLore(
                        "&7There are no recent\n&7transactions!"
                )).toItemStack());
            } else {
                List<String> transactions = new ArrayList<>();

                for (String transaction : recentTransactions) {
                    double amount = Double.parseDouble(transaction.split(";")[0]);
                    long time = Long.parseLong(transaction.split(";")[1]);
                    String by = transaction.split(";")[2];

                    Date date = new Date(time);

                    String timeString = Util.calculateTimeAgoWithPeriodAndDuration(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), ZoneId.systemDefault());
                    String plusOrMinus = amount > 0 ? "&a+" : "&c-";

                    transactions.add(plusOrMinus + " &6" + Util.formatInt(Util.abs((int) amount)) + "&7, &e" + timeString + " &7by " + by);
                }

                inventory.setItem(15, new ItemBuilder(ChatColor.GREEN + "Recent Transactions", Material.PAPER).setLore(
                        Util.buildLore("\n&7" + String.join("\n&7", transactions))
                ).toItemStack());
            }

            inventory.setItem(32, new ItemBuilder(ChatColor.GREEN + "Information", Material.REDSTONE_TORCH_ON).setLore(Util.buildLore(
                    "&7Keep your coins safe in the bank!\n&7You lose half the coins in your\n&7purse when dying in combat.\n\n&7Balance limit: &650 Million\n\n" +
                            "&7The banker rewards you every 31\n&7hours with &binterest&7 for the\n&7coins in your bank balance.\n\n&7Interest in: &b" + nextInterestHours + "h"
            )).toItemStack());

            inventory.setItem(31, Util.buildCloseButton());
        } else if (menu.equalsIgnoreCase("deposit")) {
            inventory = Bukkit.createInventory(null, 36, "Bank Deposit");

            Util.fillEmpty(inventory);

            inventory.setItem(11, new ItemBuilder(
                    ChatColor.GREEN + "Your whole purse",
                    Material.CHEST, 64
            ).addLore(Util.buildLore(
                    "&8Bank deposit\n\n&7Current balance: &6" + Util.formatDouble(balance) + "\n&7Amount to deposit: &6" + Util.formatDouble(purse) + "\n\n&eClick to deposit coins!"
            )).toItemStack());

            inventory.setItem(13, new ItemBuilder(
                    ChatColor.GREEN + "Half your purse",
                    Material.CHEST, 32
            ).addLore(Util.buildLore(
                    "&8Bank deposit\n\n&7Current balance: &6" + Util.formatDouble(balance) + "\n&7Amount to deposit: &6" + Util.formatDouble(purse / 2) + "\n\n&eClick to deposit coins!"
            )).toItemStack());

            inventory.setItem(15, new ItemBuilder(
                    ChatColor.GREEN + "Specific amount",
                    Material.SIGN
            ).addLore(Util.buildLore(
                    "&7Current balance: &6" + Util.formatDouble(balance) + "\n\n&eClick to deposit coins!"
            )).toItemStack());

            inventory.setItem(31, Util.buildBackButton("&7To Personal Bank Account"));
        } else if (menu.equalsIgnoreCase("withdraw")) {
            inventory = Bukkit.createInventory(null, 36, "Bank Withdrawal");

            Util.fillEmpty(inventory);

            inventory.setItem(10, new ItemBuilder(
                    ChatColor.GREEN + "Everything in the account",
                    Material.DROPPER, 64
            ).addLore(Util.buildLore(
                    "&8Bank withdrawal\n\n&7Current balance: &6" + Util.formatDouble(balance) + "\n&7Amount to withdraw: &6" + Util.formatDouble(balance) + "\n\n&eClick to withdraw coins!"
            )).toItemStack());

            inventory.setItem(12, new ItemBuilder(
                    ChatColor.GREEN + "Half the account",
                    Material.DROPPER, 32
            ).addLore(Util.buildLore(
                    "&8Bank withdrawal\n\n&7Current balance: &6" + Util.formatDouble(balance) + "\n&7Amount to withdraw: &6" + Util.formatDouble(balance / 2) + "\n\n&eClick to withdraw coins!"
            )).toItemStack());

            inventory.setItem(14, new ItemBuilder(
                    ChatColor.GREEN + "Withdraw 20%",
                    Material.DROPPER
            ).addLore(Util.buildLore(
                    "&8Bank withdrawal\n\n&7Current balance: &6" + Util.formatDouble(balance) + "\n&7Amount to withdraw: &6" + Util.formatDouble((balance * 20) / 100) + "\n\n&eClick to withdraw coins!"
            )).toItemStack());

            inventory.setItem(16, new ItemBuilder(
                    ChatColor.GREEN + "Specific amount",
                    Material.SIGN
            ).addLore(Util.buildLore(
                    "&7Current balance: &6" + Util.formatDouble(balance) + "\n\n&eClick to withdraw coins!"
            )).toItemStack());

            inventory.setItem(31, Util.buildBackButton("&7To Personal Bank Account"));
        } else {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid menu: " + ChatColor.DARK_GRAY + menu);
            return;
        }

        player.openInventory(inventory);
    }

}
