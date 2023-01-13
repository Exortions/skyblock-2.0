package com.skyblock.skyblock.commands.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.items.browser.ItemCategoriesGUI;
import com.skyblock.skyblock.features.items.browser.ItemSearchGUI;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.TrueAlias;
import com.skyblock.skyblock.utilities.command.annotations.Alias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Pattern;

@RequiresPlayer
@Alias(aliases = { "ib" })
@Usage(usage = "/sb itembrowser <page> <query>")
@Description(description = "Shows a list of all items in the game")
public class ItemBrowserCommand implements Command, TrueAlias<ItemBrowserCommand> {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length == 0) {
            new ItemCategoriesGUI(player).show(player);
            return;
        }

        new ItemSearchGUI(args[0], 1, player).show(player);
    }
}
