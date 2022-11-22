package com.skyblock.skyblock.commands.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
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

@RequiresPlayer
@Alias(aliases = { "ib" })
@Usage(usage = "/sb itembrowser <page> <query>")
@Description(description = "Shows a list of all items in the game")
public class ItemBrowserCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        List<ItemStack> items = new ArrayList<>();
        String command;
        String backCommand;

        try {
            int page = Integer.parseInt(args[0]) - 1;

            if (args.length >= 2) {
                StringBuilder query = new StringBuilder();
                for (int i = 1; i < args.length; i++) query.append(args[i]).append(" ");

                player.sendMessage(ChatColor.GREEN + "Searching for " + ChatColor.GOLD + query.toString().trim() + ChatColor.GREEN + "...");

                for (Map.Entry<String, ItemStack> entry : plugin.getItemHandler().getItems().entrySet()) {
                    if (entry.getValue().getItemMeta().getDisplayName().toLowerCase().contains(query.toString().toLowerCase())) {
                        items.add(entry.getValue());
                    }
                }

                command = "sb itembrowser " + (page + 2) + query;
                backCommand = "sb itembrowser " + (page) + query;
            } else {
                for (Map.Entry<String, ItemStack> entry : plugin.getItemHandler().getItems().entrySet()) {
                    items.add(entry.getValue());
                }

                items.sort(Comparator.comparing(o -> ChatColor.stripColor(o.getItemMeta().getDisplayName())));

                command = "sb itembrowser " + (page + 2);
                backCommand = "sb itembrowser " + (page);
            }

            Gui itemBrowser = new Gui("ItemBrowser", 54, new HashMap<>());

            int start = page * 45;
            int end = page * 45 + 45;

            int setItemIndex = 0;

            Util.fillEmpty(itemBrowser);

            for (int i = start; i < end; i++) {
                try {
                    ItemStack item = items.get(i);
                    itemBrowser.addItem(setItemIndex, item);
                    itemBrowser.clickEvents.put(item.getItemMeta().getDisplayName(), () -> player.getInventory().addItem(item));
                    setItemIndex++;
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }


            itemBrowser.addItem(53, new ItemBuilder(ChatColor.GREEN + "Next Page", Material.ARROW).toItemStack());
            itemBrowser.clickEvents.put(ChatColor.GREEN + "Next Page", () -> {
                player.closeInventory();
                player.performCommand(command);
                itemBrowser.clickEvents.clear();
            });

            if (page != 0) {
                itemBrowser.addItem(45, new ItemBuilder(ChatColor.GREEN + "Previous Page", Material.ARROW).toItemStack());
                itemBrowser.clickEvents.put(ChatColor.GREEN + "Previous Page", () -> {
                    player.closeInventory();
                    player.performCommand(backCommand);
                    itemBrowser.clickEvents.clear();
                });
            }

            itemBrowser.show(player);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Not a number");
        }
    }
}
