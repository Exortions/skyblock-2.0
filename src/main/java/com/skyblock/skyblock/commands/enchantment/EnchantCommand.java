package com.skyblock.skyblock.commands.enchantment;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.enchantment.EnchantingTableGUI;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiresPlayer
@Usage(usage = "/sb enchant <enchantment> [level]")
public class EnchantCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length == 0) {
            player.openInventory(new EnchantingTableGUI(player, player.getLocation()));
            return;
        }

        if (args.length > 2) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /sb enchant <enchantment> [level]");
            return;
        }

        String enchantmentName = args[0];

        int level = 1;

        if (args.length == 2) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Level must be a number");
                return;
            }
        }

        if (plugin.getEnchantmentHandler().getEnchantment(enchantmentName) == null) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Enchantment not found");
            return;
        }

        if (level < 1) level = 1;

        if (level > plugin.getEnchantmentHandler().getEnchantment(enchantmentName).getMaxLevel())
            level = plugin.getEnchantmentHandler().getEnchantment(enchantmentName).getMaxLevel();

        ItemStack item = player.getInventory().getItemInHand();

        ItemBase base = new ItemBase(item);

        base.setEnchantment(enchantmentName, level);

        player.setItemInHand(base.createStack());
    }
}
