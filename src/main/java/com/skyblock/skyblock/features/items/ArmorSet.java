package com.skyblock.skyblock.features.items;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class ArmorSet {

    private ItemStack helmet;
    private ItemStack chest;
    private ItemStack legs;
    private ItemStack boots;
    private String id;

    public ArmorSet(ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots, String id) {
        this.helmet = helmet;
        this.chest = chest;
        this.legs = legs;
        this.boots = boots;
        this.id = id;
    }

    public void fullSetBonus(Player player) { }

    public void stopFullSetBonus(Player player) { }

    public List<ItemStack> toList() {
        return Arrays.asList(helmet, chest, legs, boots);
    }
}
