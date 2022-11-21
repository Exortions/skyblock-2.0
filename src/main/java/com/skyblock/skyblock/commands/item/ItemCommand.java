package com.skyblock.skyblock.commands.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiresPlayer
@Usage(usage = "/sb item <id> [amount]")
@Description(description = "Gives you an item with the given id.")
public class ItemCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length < 1) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /sb item <id> [amount]");
            return;
        }

        String id = args[0];

        ItemStack stack = plugin.getItemHandler().getItem(id);

        int amount = 1;

        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid amount.");
                return;
            }
        }

        if (stack == null) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "No item with the id " + id + " exists.");
            return;
        }

        player.getInventory().addItem(Util.stripMerchantLore(stack));
    }
}
