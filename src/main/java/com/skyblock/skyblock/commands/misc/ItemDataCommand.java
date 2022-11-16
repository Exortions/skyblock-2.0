package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb itemdata")
@Description(description = "Shows the skyblock data of the item in your hand.")
public class ItemDataCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (player.getItemInHand() == null) {
            player.sendMessage(plugin.getPrefix() + "You do not have an item in your hand.");
            return;
        }

        NBTItem nbt = new NBTItem(player.getItemInHand());

        if (!nbt.getBoolean("skyblockItem")) {
            player.sendMessage(plugin.getPrefix() + "This item is not a skyblock item.");
            return;
        }

        player.sendMessage(plugin.getPrefix() + "Rarity: " + nbt.getString("rarity"));
        player.sendMessage(plugin.getPrefix() + "Type: " + nbt.getString("type"));
    }
}
