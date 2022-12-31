package com.skyblock.skyblock.enums;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.crafting.SkyblockCraftingRecipe;
import com.skyblock.skyblock.features.crafting.SkyblockRecipe;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.function.Function;

@Getter
public enum MiningMinionType implements MinionType<MiningMinionType> {

    COBBLESTONE(
            "Cobblestone",
            (level) -> {
                if (level == 1) {
                    return new SkyblockRecipe(
                            new ShapedRecipe(Skyblock.getPlugin().getItemHandler().getItem("COBBLESTONE_GENERATOR_1"))
                                    .shape("abc", "def", "ghi").setIngredient('a', Material.COBBLESTONE)
                                    .setIngredient('b', Material.COBBLESTONE).setIngredient('c', Material.COBBLESTONE)
                                    .setIngredient('d', Material.COBBLESTONE).setIngredient('c', Material.COBBLESTONE));
                } else {
                    return null;
                }
            },
            (level) -> new ItemStack(Material.WOOD_PICKAXE, 1),
            (level) -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjljMzhmZTRmYzk4YTI0ODA3OWNkMDRjNjViNmJmZjliNDUwMTdmMTY0NjBkYWIzYzM0YzE3YmZjM2VlMWQyZiJ9fX0=",
            Color.GRAY,
            (level) -> 14,
            (level) -> {
                int stacks;

                switch (level) {
                    case 1:
                        stacks = 1;
                    case 2:
                    case 3:
                        stacks = 3;
                        break;
                    case 4:
                    case 5:
                        stacks = 6;
                        break;
                    case 6:
                    case 7:
                        stacks = 9;
                        break;
                    case 8:
                    case 9:
                        stacks = 12;
                        break;
                    default:
                        stacks = 15;
                        break;
                }

                return stacks * 64;
            },
            Material.COBBLESTONE,
            (level) -> new ItemStack[]{
                    new ItemStack(Material.COBBLESTONE, 1)
            },
            (slot) -> {
                switch (slot) {
                    case 0:
                        return 1;
                    case 1:
                    case 2:
                        return 2;
                    case 3:
                    case 4:
                    case 5:
                        return 4;
                    case 6:
                    case 7:
                    case 8:
                        return 6;
                    case 9:
                    case 10:
                    case 11:
                        return 8;
                    case 12:
                    case 13:
                    case 14:
                        return 10;
                    default:
                        return 0;
                }
            }),
    ;

    private final String name;
    private final Function<Integer, SkyblockRecipe> recipe;
    private final Function<Integer, ItemStack> hand;
    private final Function<Integer, String> head;
    private final Color leatherArmorColor;
    private final Function<Integer, Integer> timeBetweenActions;
    private final Function<Integer, Integer> getMaximumStorage;
    private final Material material;
    private final Function<Integer, ItemStack[]> calculateDrops;
    private final Function<Integer, Integer> levelRequirementForStorageSlot;

    MiningMinionType(String name, Function<Integer, SkyblockRecipe> recipe, Function<Integer, ItemStack> hand, Function<Integer, String> head, Color leatherArmorColor, Function<Integer, Integer> timeBetweenActions, Function<Integer, Integer> getMaximumStorage, Material material, Function<Integer, ItemStack[]> calculateDrops, Function<Integer, Integer> levelRequirementForStorageSlot) {
        this.name = name;
        this.recipe = recipe;
        this.hand = hand;
        this.head = head;
        this.leatherArmorColor = leatherArmorColor;
        this.timeBetweenActions = timeBetweenActions;
        this.getMaximumStorage = getMaximumStorage;
        this.material = material;
        this.calculateDrops = calculateDrops;
        this.levelRequirementForStorageSlot = levelRequirementForStorageSlot;
    }

    @Override
    public MiningMinionType getRaw() {
        return this;
    }

}
