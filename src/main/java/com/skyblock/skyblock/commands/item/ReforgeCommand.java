package com.skyblock.skyblock.commands.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.Reforge;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiresPlayer
@Usage(usage = "/sb reforge <reforge>")
@Description(description = "Reforge the item in your hand.")
public class ReforgeCommand implements Command {
    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                Skyblock.getPlugin(Skyblock.class).getReforgeHandler().index();

                player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Successfully reloaded all reforges.");

                return;
            }
        }

        if (args.length == 0) {
            Inventory inventory = Bukkit.createInventory(null, 45, "Reforge Item");

            Util.fillEmpty(inventory);
            Util.fillSides45Slots(inventory, Material.STAINED_GLASS_PANE, 14);

            inventory.setItem(13, new ItemStack(Material.AIR));

            inventory.setItem(40, Util.buildCloseButton());

            inventory.setItem(22, new ItemBuilder(ChatColor.YELLOW + "Reforge Item", Material.ANVIL).addLore(Util.buildLore("&7Place an item above to reforge\n&7it! Reforging items adds a\n&7random modifier to the item that\n&7grants stat boosts.")).addNBT("reforge", true).toItemStack());

            player.openInventory(inventory);

            return;
        }

        if (player.getItemInHand() == null) {
            player.sendMessage(plugin.getPrefix() + "You do not have an item in your hand.");
            return;
        } else {
            ItemStack item = player.getItemInHand();

            if (item == null || item.getType().equals(Material.AIR)) {
                player.sendMessage(plugin.getPrefix() + "You do not have an item in your hand.");
                return;
            }

            NBTItem nbt = new NBTItem(item);

            if (!nbt.getBoolean("skyblockItem")) {
                player.sendMessage(plugin.getPrefix() + "This item is not a skyblock item.");
                return;
            }
        }

        Reforge reforge = Reforge.getReforge(args[0]);

        if (reforge == null) {
            player.sendMessage(plugin.getPrefix() + "That is not a valid reforge.");
            return;
        }

        player.setItemInHand(ItemBase.reforge(player.getItemInHand(), reforge));

        player.sendMessage(plugin.getPrefix() + "Reforge set to " + reforge + ".");
    }
}
