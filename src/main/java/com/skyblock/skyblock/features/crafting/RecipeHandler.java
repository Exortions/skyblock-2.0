package com.skyblock.skyblock.features.crafting;

import com.skyblock.skyblock.Skyblock;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.CraftingManager;
import net.minecraft.server.v1_8_R3.IRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class RecipeHandler {

    private List<SkyblockRecipe> recipes;

    public RecipeHandler() {
        recipes = new ArrayList<>();
    }

    public void init() {
        recipes.addAll(getShapedRecipeStrings());
        recipes.addAll(getShapelessRecipeStrings());
    }

    private List<SkyblockRecipe> getShapelessRecipeStrings(){
        List<SkyblockRecipe> recipes = new ArrayList<>();
        for (IRecipe iRecipe : CraftingManager.getInstance().getRecipes()){
            Recipe recipe = iRecipe.toBukkitRecipe();
            if (recipe instanceof ShapelessRecipe){
                ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
                recipes.add(new SkyblockRecipe(shapelessRecipe));
            }
        }
        return recipes;
    }

    private List<SkyblockRecipe> getShapedRecipeStrings(){
        List<SkyblockRecipe> recipes = new ArrayList<>();
        for (IRecipe iRecipe : CraftingManager.getInstance().getRecipes()){
            Recipe recipe = iRecipe.toBukkitRecipe();
            if (recipe instanceof ShapedRecipe){
                ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
                recipes.add(new SkyblockRecipe(shapedRecipe));
            }
        }
        return recipes;
    }
}
