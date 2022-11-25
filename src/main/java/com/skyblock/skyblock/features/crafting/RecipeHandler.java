package com.skyblock.skyblock.features.crafting;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.CraftingManager;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RecipeHandler {

    private final List<SkyblockRecipe> recipes;

    public RecipeHandler() {
        recipes = new ArrayList<>();
    }

    public void init() {
        recipes.addAll(getShapedRecipeStrings());
        recipes.addAll(getShapelessRecipeStrings());
    }

    private List<SkyblockRecipe> getShapelessRecipeStrings(){
        List<SkyblockRecipe> recipes = new ArrayList<>();

        CraftingManager.getInstance().getRecipes().forEach(iRecipe -> {
            Recipe recipe = iRecipe.toBukkitRecipe();

            if (recipe instanceof ShapelessRecipe) recipes.add(new SkyblockRecipe((ShapelessRecipe) recipe));
        });

        return recipes;
    }

    private List<SkyblockRecipe> getShapedRecipeStrings(){
        List<SkyblockRecipe> recipes = new ArrayList<>();

        CraftingManager.getInstance().getRecipes().forEach(iRecipe -> {
            Recipe recipe = iRecipe.toBukkitRecipe();

            if (recipe instanceof ShapedRecipe) recipes.add(new SkyblockRecipe((ShapedRecipe) recipe));
        });

        return recipes;
    }

}
