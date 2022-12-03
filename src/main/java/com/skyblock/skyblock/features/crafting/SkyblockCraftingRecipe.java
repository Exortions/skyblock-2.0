package com.skyblock.skyblock.features.crafting;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Data
public class SkyblockCraftingRecipe {

    private final HashMap<String, ItemStack> ingredients;

}
