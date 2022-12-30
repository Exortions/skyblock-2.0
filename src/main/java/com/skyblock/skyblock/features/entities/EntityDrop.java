package com.skyblock.skyblock.features.entities;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.Rarity;
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
        this(item, EntityDropRarity.COMMON, 100, 1, 1);
    }
    public EntityDrop(ItemStack item, double min, double max) {
        this(item, EntityDropRarity.COMMON, 100, min, max);
    }

    public EntityDrop(String id, EntityDropRarity rarity, double chance, int amount) {
        this(Skyblock.getPlugin().getItemHandler().getItem(id.toUpperCase() + ".json"), rarity, chance, amount);
    }

    public EntityDrop(String id, double chance, int amount) {
        this(Skyblock.getPlugin().getItemHandler().getItem(id.toUpperCase() + ".json"), chance, amount);
    }

    public EntityDrop(ItemStack item, double chance, int amount) {
        this(item, EntityDropRarity.COMMON, chance, amount, amount);

        EntityDropRarity rar = EntityDropRarity.COMMON;

        if (chance <= 0.01) rar = EntityDropRarity.RNGESUS;
        if (chance <= 0.1) rar = EntityDropRarity.LEGENDARY;
        if (chance <= 1) rar = EntityDropRarity.RARE;
        if (chance <= 30) rar = EntityDropRarity.UNCOMMON;

        this.rarity = rar;
    }

    public EntityDrop(ItemStack item, EntityDropRarity rarity, double chance, int amount) {
        this(item, rarity, chance, amount, amount);
    }

}
