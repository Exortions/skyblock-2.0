package com.skyblock.skyblock.enums;

import com.skyblock.skyblock.features.crafting.SkyblockCraftingRecipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Function;

@Getter
public enum MiningMinionType implements MinionType<MiningMinionType> {

    COBBLESTONE(
            "Cobblestone",
            (level) -> {
                if (level == 1) {
                    return new SkyblockCraftingRecipe(new HashMap<String, ItemStack>() {{
                        put("a", new ItemStack(Material.COBBLESTONE, 1));
                    }});
                } else {
                    return null;
                }
            },
            (level) -> new ItemStack(Material.WOOD_PICKAXE, 1),
            (level) -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjljMzhmZTRmYzk4YTI0ODA3OWNkMDRjNjViNmJmZjliNDUwMTdmMTY0NjBkYWIzYzM0YzE3YmZjM2VlMWQyZiJ9fX0=",
            Color.GRAY,
            (level) -> 6,
            (level) -> 64,
            Material.COBBLESTONE,
            (level) -> new ItemStack[]{
                    new ItemStack(Material.COBBLESTONE, 1)
            }),
    ;

    private final String name;
    private final Function<Integer, SkyblockCraftingRecipe> recipe;
    private final Function<Integer, ItemStack> hand;
    private final Function<Integer, String> head;
    private final Color leatherArmorColor;
    private final Function<Integer, Integer> timeBetweenActions;
    private final Function<Integer, Integer> getMaximumStorage;
    private final Material material;
    private final Function<Integer, ItemStack[]> calculateDrops;

    MiningMinionType(String name, Function<Integer, SkyblockCraftingRecipe> recipe, Function<Integer, ItemStack> hand, Function<Integer, String> head, Color leatherArmorColor, Function<Integer, Integer> timeBetweenActions, Function<Integer, Integer> getMaximumStorage, Material material, Function<Integer, ItemStack[]> calculateDrops) {
        this.name = name;
        this.recipe = recipe;
        this.hand = hand;
        this.head = head;
        this.leatherArmorColor = leatherArmorColor;
        this.timeBetweenActions = timeBetweenActions;
        this.getMaximumStorage = getMaximumStorage;
        this.material = material;
        this.calculateDrops = calculateDrops;
    }

    @Override
    public MiningMinionType getRaw() {
        return this;
    }

}
