package com.skyblock.skyblock.features.minions.items.upgrades;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import de.tr7zw.nbtapi.NBTItem;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.DynamicLore;
import com.skyblock.skyblock.features.minions.MinionBase;
import com.skyblock.skyblock.features.minions.items.MinionItem;
import com.skyblock.skyblock.features.minions.items.MinionItemType;

public class DiamondSpreading extends MinionItem {

    public DiamondSpreading() {
        super(plugin.getItemHandler().getItem("DIAMOND_SPREADING.json"), "diamond_spreading",  MinionItemType.UPGRADE, false, true);
    }

    @Override
    public ItemStack[] onBlockCollect(MinionBase minion, ItemStack[] drops) {
        int items = 0;
        for (ItemStack drop : drops) {
           items += drop.getAmount();
        }
        ItemStack out = new ItemStack(Material.DIAMOND);
        out.setAmount((int) Math.floor(items/10));
        drops[drops.length] = out;
        return drops;
    }
}
