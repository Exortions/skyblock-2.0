package com.skyblock.skyblock.features.crafting.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.crafting.SkyblockRecipe;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import net.citizensnpcs.npc.ai.speech.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecipeGUI extends Gui {

    private static final List<Integer> slots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30);

    public RecipeGUI(ItemStack result, Gui prev, Player player) {
        super(ChatColor.stripColor(result.getItemMeta().getDisplayName()) + " Recipe", 54, new HashMap<>());

        Util.fillEmpty(this);

        addItem(49, Util.buildCloseButton());

        if (prev == null) prev = new Gui("Skyblock Menu", 54, new HashMap<>());

        addItem(48, Util.buildBackButton(ChatColor.GRAY + "To " + prev.getName()));

        Gui finalPrev = prev;
        getClickEvents().put(ChatColor.GREEN + "Go Back", () -> {
            finalPrev.show(player);
        });

        SkyblockRecipe recipe = Skyblock.getPlugin().getRecipeHandler().getRecipe(result);

        int i = 0;
        for (int slot : slots) {
            addItem(slot, null);
            String item = recipe.getItems().get(i);

            if (item.equals("AIR")) {
                i++;
                continue;
            }

            String id = item.split(":")[0];
            int amount = Integer.parseInt(item.split(":")[1]);

            ItemStack neu = Skyblock.getPlugin().getItemHandler().getItem(id + ".json").clone();
            neu.setAmount(amount);

            addItem(slot, neu);

            i++;
        }

        addItem(23, new ItemBuilder(ChatColor.GREEN + "Crafting Table", Material.WORKBENCH).addLore("&7Craft this recipe by using a", "&7crafting table.").toItemStack());
        addItem(25, result);
    }
}
