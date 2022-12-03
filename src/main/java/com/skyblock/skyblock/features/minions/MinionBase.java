package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.crafting.SkyblockCraftingRecipe;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
public abstract class MinionBase {

    protected final String name;
    protected int level;

    protected final Function<Integer, SkyblockCraftingRecipe> recipe;

    protected final Function<Integer, ItemStack> hand;
    protected final Function<Integer, String> head;
    protected final Color leatherArmorColor;

    protected final Function<Integer, Integer> getTimeBetweenActions;
    protected final Function<Integer, Integer> getMaximumStorage;

    protected ArmorStand minion;
    protected ArmorStand text;

    protected double timeBetweenActions;
    protected int resourcesGenerated;
    protected int maxStorage;

    protected List<ItemStack> inventory;

    public MinionBase(String name, Function<Integer, SkyblockCraftingRecipe> recipe, Function<Integer, ItemStack> hand, Function<Integer, String> head, Color leatherArmorcolor, Function<Integer, Integer> getTimeBetweenActions, Function<Integer, Integer> getMaximumStorage) {
        this.name = name;
        this.recipe = recipe;
        this.hand = hand;
        this.head = head;
        this.leatherArmorColor = leatherArmorcolor;
        this.getTimeBetweenActions = getTimeBetweenActions;
        this.getMaximumStorage = getMaximumStorage;

        this.minion = null;

        this.level = 0;

        this.timeBetweenActions = 0;
        this.resourcesGenerated = 0;
        this.maxStorage = 0;

        this.inventory = new ArrayList<>();
    }

    public abstract void load(SkyblockPlayer player, int index);

    public abstract void spawn(SkyblockPlayer player, Location location, int level);
    public abstract void pickup(SkyblockPlayer player, Location location);

    public abstract void showInventory(SkyblockPlayer player);

    protected abstract void tick(SkyblockPlayer player, Location location);

}
