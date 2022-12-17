package com.skyblock.skyblock.features.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class EntityDrop {

    private ItemStack item;
    private EntityDropRarity rarity;
    private double chance;
    private int amount;

    public EntityDrop(ItemStack item) {
        this(item, EntityDropRarity.GUARANTEED, 1.0, 1);
    }
    public EntityDrop(ItemStack item, int amount) {
        this(item, EntityDropRarity.GUARANTEED, 1.0, amount);
    }

}
