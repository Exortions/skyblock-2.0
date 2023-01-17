package com.skyblock.skyblock.features.bags;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class BagManager {

    private final HashMap<String, Bag> bags;

    public BagManager() {
        this.bags = new HashMap<>();
    }

    public void show(String id, Player player) {
        Bag bag = this.bags.get(id);

        if (bag == null) return;

        Inventory inventory = bag.show(player);

        player.openInventory(inventory);
    }

    public void registerBag(Bag bag) {
        this.bags.put(bag.getId(), bag);
    }

    public HashMap<String, Bag> getBags() {
        return this.bags;
    }

    public List<ItemStack> getBagContents(String id, Player player) {
        Bag bag = this.bags.get(id);

        if (bag == null) return null;

        return bag.getContents(player);
    }

}
