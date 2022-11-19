package com.skyblock.skyblock.commands.enchantment;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiresPlayer
@Usage(usage = "/sb addenchantment <enchantment> <level>")
public class AddEnchantmentCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length < 1 || args.length > 2) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "/sb addenchantment <enchantment> <level>");
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

        if (level > plugin.getEnchantmentHandler().getEnchantment(enchantmentName).getMaxLevel()) {
            level = plugin.getEnchantmentHandler().getEnchantment(enchantmentName).getMaxLevel();
        }

        ItemStack item = player.getInventory().getItemInHand();

        ItemBase base = new ItemBase(item);

        if (base.hasEnchantment(plugin.getEnchantmentHandler().getEnchantment(enchantmentName))) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Item already has enchantment");
            return;
        }

        base.addEnchantment(enchantmentName, level);

        player.setItemInHand(base.createStack());
    }
}
