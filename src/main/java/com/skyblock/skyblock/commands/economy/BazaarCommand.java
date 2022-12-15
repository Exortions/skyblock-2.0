package com.skyblock.skyblock.commands.economy;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.bazaar.Bazaar;
import com.skyblock.skyblock.features.bazaar.gui.BazaarCategoryGui;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.TrueAlias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb bazaar")
@Description(description = "Shows the Skyblock bazaar")
public class BazaarCommand implements Command, TrueAlias<BazaarCommand> {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        Bazaar bazaar = plugin.getBazaar();

        if (args.length == 0) {
            // TODO: Store advanced mode in player.yml
            new BazaarCategoryGui(player, bazaar.getCategories().stream().filter(cat -> cat.getName().equals("Farming")).findFirst().get(), false).show(player);
        }
    }
}
