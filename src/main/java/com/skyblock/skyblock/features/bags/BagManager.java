package com.skyblock.skyblock.features.bags;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

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

    public Bag getBag(String id) {
        return this.bags.get(id);
    }

    public HashMap<String, Bag> getBags() {
        return this.bags;
    }

}
