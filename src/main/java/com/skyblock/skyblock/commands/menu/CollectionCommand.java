package com.skyblock.skyblock.commands.menu;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.collections.CollectionCategory;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@RequiresPlayer
@Usage(usage = "/sb collection")
@Description(description = "Open the collection menu")
public class CollectionCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        Inventory inventory;

        SkyblockPlayer skyblockPlayer = new SkyblockPlayer(player.getUniqueId());

        if (args.length == 0) {
            inventory = Bukkit.createInventory(null, 54, "Collection");

            fillEmpty(inventory);

            player.openInventory(inventory);

            return;
        }

        String inv = args[0];

        if (Collection.getCollectionCategories().stream().anyMatch(cat -> cat.getName().equalsIgnoreCase(inv))) {
            CollectionCategory category = Collection.getCollectionCategories().stream().filter(cat -> cat.getName().equalsIgnoreCase(inv)).findFirst().get();

            inventory = Bukkit.createInventory(null, 54, category.getName());

            fillEmpty(inventory);

            player.openInventory(inventory);
        } else if (Collection.getCollections().stream().anyMatch(col -> col.getName().equalsIgnoreCase(inv))) {
            Collection collection = Collection.getCollections().stream().filter(col -> col.getName().equalsIgnoreCase(inv)).findFirst().get();

            inventory = Bukkit.createInventory(null, 54, collection.getName());

            fillEmpty(inventory);

            player.openInventory(inventory);
        } else {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid collection category or collection");
        }

    }

    public void fillEmpty(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, 1, (short) 15).toItemStack());
    }
}
