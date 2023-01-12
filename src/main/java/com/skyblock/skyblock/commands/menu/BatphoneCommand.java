package com.skyblock.skyblock.commands.menu;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.slayer.gui.SlayerGUI;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb batphone")
@Description(description = "Opens the maddox batphone menu")
public class BatphoneCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        new SlayerGUI(player).show(player);
    }
}
