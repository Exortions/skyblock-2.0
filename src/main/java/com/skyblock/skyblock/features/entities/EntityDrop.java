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
    private double min;
    private double max;

    public EntityDrop(ItemStack item) {
        this(item, EntityDropRarity.GUARANTEED, 1.0, 1, 1);
    }
    public EntityDrop(ItemStack item, double min, double max) {
        this(item, EntityDropRarity.GUARANTEED, 1.0, min, max);
    }
    public EntityDrop(ItemStack item, EntityDropRarity rarity, double chance, int amount) {
        this(item, rarity, chance, amount, amount);
    }

}
