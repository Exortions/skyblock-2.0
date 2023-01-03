package com.skyblock.skyblock.features.minions.items.upgrades;

import java.util.ArrayList;
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

    HashMap<String, Integer> itemAmounts = new HashMap<>();

    public DiamondSpreading() {
        super(plugin.getItemHandler().getItem("DIAMOND_SPREADING.json"), "diamond_spreading",  MinionItemType.UPGRADE, false, true);
    }

    @Override
    public ArrayList<ItemStack> onBlockCollect(MinionBase minion, ArrayList<ItemStack> drops) {
        String uuid = minion.getUuid().toString();
        itemAmounts.putIfAbsent(uuid, 0);

        int items = 0;
        for (ItemStack drop : drops) {
           items += drop.getAmount();
        }

        itemAmounts.put(uuid, itemAmounts.get(uuid) + items);
        int diamonds = (int) Math.floor((itemAmounts.get(uuid) + 1)/10);
        itemAmounts.put(uuid, itemAmounts.get(uuid) - diamonds * 10);

        if (diamonds == 0) return drops;
        
        ItemStack out = new ItemStack(Material.DIAMOND);
        out.setAmount(diamonds);
        drops.add(out);
        return drops;
    }
}
