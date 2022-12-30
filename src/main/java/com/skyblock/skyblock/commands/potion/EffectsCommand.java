package com.skyblock.skyblock.commands.potion;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.potions.EffectsGui;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.TrueAlias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb effects")
@Description(description = "Shows your currently active effects")
public class EffectsCommand implements Command, TrueAlias<EffectsCommand> {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        new EffectsGui(player).show(player);
    }
}
