package com.skyblock.skyblock.features.crafting.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class CollectionsRecipeGUI extends Gui {
    public CollectionsRecipeGUI(String collection, int page, ItemStack display, Player player) {
        super(collection + " Recipes", 54, new HashMap<>());

        List<String> recipes = Collection.recipes.get(collection);

        int pages = (int) Math.ceil(recipes.size() / 28f);

        Util.fillBorder(this);

        addItem(4, display);
        addItem(49, Util.buildCloseButton());
        addItem(48, Util.buildBackButton("&7To Recipe Book"));

        if (page != pages) addItem(53, new ItemBuilder(ChatColor.GREEN + "Next Page", Material.ARROW).addLore(ChatColor.YELLOW + "Page " + (page + 1)).toItemStack());
        if (page != 1) addItem(45, new ItemBuilder(ChatColor.GREEN + "Previous Page", Material.ARROW).addLore(ChatColor.YELLOW + "Page " + (page - 1)).toItemStack());

        getClickEvents().put(ChatColor.GREEN + "Next Page", () -> {
           new CollectionsRecipeGUI(collection, page + 1, display, player).show(player);
        });

        getClickEvents().put(ChatColor.GREEN + "Previous Page", () -> {
            new CollectionsRecipeGUI(collection, page - 1, display, player).show(player);
        });

        List<String> unlocked = (List<String>) SkyblockPlayer.getPlayer(player).getValue("recipes.unlocked");

        int j = -1;
        for (int i = (page - 1) * 28; i < recipes.size(); i++) {
            String recipe = recipes.get(i);
            j++;

            if (getItems().containsKey(j)) {
                i--;
                continue;
            }

            if (j == getSlots() - 1) break;

            if (unlocked.contains(recipe)) {
                if (recipe.contains("Mystery")) recipe = recipe.replace("Mystery ", "").replace(" Pet", "") + ";4";

                ItemStack neu = Skyblock.getPlugin().getItemHandler().getItem(recipe.toUpperCase().replace(" ", "_") + ".json");
                if (neu == null) {
                    player.sendMessage(ChatColor.RED + "Failed recipe: " + recipe);
                    continue;
                }
                ItemStack edit = neu.clone();
                ItemMeta meta = edit.getItemMeta();
                List<String> lore = meta.getLore();

                lore.add(" ");
                lore.add(ChatColor.YELLOW + "Click to view recipe!");

                meta.setLore(lore);
                edit.setItemMeta(meta);

                getSpecificClickEvents().put(edit, () -> {
                    new RecipeGUI(neu, this, player).show(player);
                });

                addItem(j, edit);
            } else {
                addItem(j, new ItemBuilder(ChatColor.RED + "???", Material.INK_SACK, (short) 8).addLore("&7Not unlocked!").toItemStack());
            }
        }

        getClickEvents().put(ChatColor.RED + "???", () -> {
            player.sendMessage(ChatColor.RED + "You haven't unlocked this recipe yet!");
        });
    }
}
