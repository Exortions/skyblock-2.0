package com.skyblock.skyblock.commands.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.ReforgeType;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiresPlayer
@Usage(usage = "/sb reforge <reforge>")
@Description(description = "Reforge the item in your hand.")
public class ReforgeCommand implements Command {
    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
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

        if (args.length == 0) {
            player.sendMessage(plugin.getPrefix() + "You must specify a reforge.");
            return;
        }

        ReforgeType reforge = ReforgeType.getReforge(args[0]);

        if (reforge == null) {
            player.sendMessage(plugin.getPrefix() + "That is not a valid reforge.");
            return;
        }

        ItemStack item = player.getItemInHand();

        ItemBase base = new ItemBase(item);

        base.setReforgeType(reforge);

        base.createStack();

        player.setItemInHand(base.getStack());

        player.sendMessage(plugin.getPrefix() + "Reforge set to " + reforge + ".");
    }
}
