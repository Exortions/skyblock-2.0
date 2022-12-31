package com.skyblock.skyblock.features.collections.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.crafting.gui.RecipeGUI;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CollectionRewardGUI extends Gui {

    private static final HashMap<ItemStack, List<String>> REWARDS_CACHE = new HashMap<>();

    public CollectionRewardGUI(ItemStack pane, ItemStack collectionItem, Gui prev, Player player) {
        super(ChatColor.stripColor(pane.getItemMeta().getDisplayName()) + " Rewards", 54, new HashMap<>());

        Util.fillEmpty(this);

        addItem(4, collectionItem);
        addItem(49, Util.buildCloseButton());
        addItem(48, Util.buildBackButton("&7To " + prev.getName()));

        getClickEvents().put(ChatColor.GREEN + "Go Back", () -> {
            prev.show(player);
        });

        List<String> recipes = getRecipeRewards(pane);

        int start = 23 - recipes.size();

        for (String recipe : recipes) {
            if (recipe.contains("Mystery")) recipe = recipe.replace("Mystery ", "").replace(" Pet", "") + ";4";

            ItemStack neu = Skyblock.getPlugin().getItemHandler().getItem(recipe.toUpperCase().replace(" ", "_") + ".json");
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

            addItem(start, edit);

            start += 2;
        }
    }


    private List<String> getRecipeRewards(ItemStack pane) {
        if (REWARDS_CACHE.containsKey(pane)) return REWARDS_CACHE.get(pane);

        List<String> rewards = new ArrayList<>();

        for (String line : pane.getItemMeta().getLore()) {
            String stripColor = ChatColor.stripColor(line);
            if (line.endsWith("Recipe")) rewards.add(stripColor.substring(2, stripColor.length() - 7));
        }

        REWARDS_CACHE.put(pane, rewards);

        return rewards;
    }
}
