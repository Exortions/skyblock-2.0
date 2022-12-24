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
@Usage(usage = "/sb regenerate")
@Description(description = "Regenerates the Skyblock item in your hand")
public class RegenerateCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
            ItemBase base = new ItemBase(player.getItemInHand());

            base.regenerate();

            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Successfully regenerated item!");

            player.setItemInHand(base.getStack());
    }
}
