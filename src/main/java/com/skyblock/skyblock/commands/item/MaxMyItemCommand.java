package com.skyblock.skyblock.commands.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb maxmyitem")
@Description(description = "Maxes your item with enchantments and reforges.")
public class MaxMyItemCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        ItemBase base;

        try {
            base = new ItemBase(player.getItemInHand());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "You must be holding a valid SkyBlock item!");
            return;
        }

        base.maxItem();

        player.setItemInHand(base.getStack());
    }
}
