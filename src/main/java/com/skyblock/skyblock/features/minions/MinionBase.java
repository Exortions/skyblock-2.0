package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.MinionType;
import com.skyblock.skyblock.features.crafting.SkyblockCraftingRecipe;
import com.skyblock.skyblock.features.minions.items.MinionItem;
import com.skyblock.skyblock.features.minions.items.MinionItemType;

import lombok.Data;
import lombok.Getter;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Data
@Getter
public abstract class MinionBase {

    protected final UUID uuid;

    protected final MinionType<?> type;

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
    protected int actionRadius;
    public int additionalActionRadius = 0;

    protected Inventory gui;

    public List<ItemStack> inventory;
    public Block additionalStorage = null;

    protected final ArrayList<MinionItemType> minionItemSlots;
    protected MinionItem[] minionItems; 

    public MinionBase(UUID uuid, MinionType<?> type, String name, Function<Integer, SkyblockCraftingRecipe> recipe, Function<Integer, ItemStack> hand, Function<Integer, String> head, Color leatherArmorcolor, Function<Integer, Integer> getTimeBetweenActions, Function<Integer, Integer> getMaximumStorage) {
        this.uuid = uuid;
        this.type = type;
        this.name = name;
        this.recipe = recipe;
        this.hand = hand;
        this.head = head;
        this.leatherArmorColor = leatherArmorcolor;
        this.getTimeBetweenActions = getTimeBetweenActions;
        this.getMaximumStorage = getMaximumStorage;
        this.actionRadius = 2;

        this.minion = null;

        this.level = 1;

        this.timeBetweenActions = 0;
        this.resourcesGenerated = 0;
        this.maxStorage = 0;

        this.inventory = new ArrayList<>();

        minionItemSlots = new ArrayList<>(Arrays.asList(MinionItemType.SKIN, MinionItemType.FUEL, MinionItemType.SHIPPING, MinionItemType.UPGRADE, MinionItemType.UPGRADE));
        minionItems = new MinionItem[minionItemSlots.size()];
    }

    public abstract void spawn(SkyblockPlayer player, Location location, int level);
    public abstract void pickup(SkyblockPlayer player, Location location);

    public abstract void upgrade(SkyblockPlayer player, int level, String item, int amount);
    public abstract void collect(SkyblockPlayer player, int slot);
    public abstract void collect(SkyblockPlayer player);
    public abstract Material getMaterial();
    public abstract int getNextMaxStorage();

    public void collectAll(SkyblockPlayer player) {
        for (int i = 0; i < gui.getSize(); i++) collect(player, i);
    }

    protected abstract void showInventory(SkyblockPlayer player);

    protected abstract void tick(SkyblockPlayer player, Location location);

    public void addItem(MinionItem item) {
        ArrayList<Integer> slots = getItemSlots(item.getType());
        if (slots.size() > 0) {
            for (int slot : slots) {
                if (minionItems[slot] == null) {
                    minionItems[slot] = item;
                    minionItems[slot].onEquip(this);
                }
            }
        }
    }

    public void removeItem(int slot) {
        minionItems[slot].onUnEquip(this);
        minionItems[slot] = null;
    }

    public ArrayList<Integer> getItemSlots(MinionItemType type) {
        ArrayList<Integer> slots = new ArrayList<>();
        for (int i = 0; i < minionItemSlots.size(); ++i) {
            if (minionItemSlots.get(i) == type) slots.add(i);
        }
        return slots;
    }
}
