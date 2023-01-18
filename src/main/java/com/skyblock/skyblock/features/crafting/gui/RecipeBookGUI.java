package com.skyblock.skyblock.features.crafting.gui;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.commands.menu.SkyblockMenuCommand;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class RecipeBookGUI extends Gui {
    private Player player;
    public RecipeBookGUI(Player player) {
        super("Recipe Book", 54, new HashMap<>());

        this.player = player;

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        Util.fillEmpty(this);
        addItem(4, SkyblockMenuCommand.createRecipeBookItem(skyblockPlayer));

        addItem(49, Util.buildCloseButton());
        addItem(48, Util.buildBackButton());

        addItem(20, createCollectionsRecipesItem("Farming", Material.GOLD_HOE));
        addItem(21, createCollectionsRecipesItem("Mining", Material.STONE_PICKAXE));
        addItem(22, createCollectionsRecipesItem("Combat", Material.STONE_SWORD));
        addItem(23, createCollectionsRecipesItem("Fishing", Material.FISHING_ROD));
        addItem(24, createCollectionsRecipesItem("Foraging", Material.SAPLING, (short) 3));
    }

    private ItemStack createCollectionsRecipesItem(String collection, Material material) {
        return createCollectionsRecipesItem(collection, material, (short) 0);
    }
    private ItemStack createCollectionsRecipesItem(String collection, Material material, short data) {
        ItemBuilder item = new ItemBuilder(ChatColor.GREEN + collection + " Recipes", material, data).addLore("&7View all of the " + collection + " Recipes", "&7that you have unlocked!");

        getClickEvents().put(item.toItemStack().getItemMeta().getDisplayName(), () -> {
            new CollectionsRecipeGUI(collection, 1, item.toItemStack().clone(), player).show(player);
        });

        item.addLore(" ", ChatColor.YELLOW + "Click to view!");

        item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        return item.toItemStack().clone();
    }
}
