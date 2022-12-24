package com.skyblock.skyblock.commands.potion;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb createpotion <name> <amplifier> <duration>")
@Description(description = "Creates a potion item")
public class CreatePotionCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length != 3) {
            player.sendMessage(ChatColor.RED + "Usage: /sb createpotion <name> <amplifier> <duration>");
            return;
        }

        String name = args[0];
        int amplifier = Integer.parseInt(args[1]);
        double duration = Double.parseDouble(args[2]);

        player.getInventory().addItem(Util.createPotion(name, amplifier, (int) duration));
    }
}
