package com.skyblock.skyblock.features.minions.items.shippings;

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

public class Hopper extends MinionItem /*implements DynamicLore*/ {

    private final float sellMultiplier;
    public Hopper(String type, float sellMultiplier) {
        super(plugin.getItemHandler().getItem(type + "_HOPPER.json"), type.toLowerCase() + "hopper",  MinionItemType.SHIPPING, false, true);
        this.sellMultiplier = sellMultiplier;
    }

    /*@Override
    public boolean onItemClick(Player p, ItemStack item) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(p);
        NBTItem nbt = new NBTItem(item);
        if (nbt.getInteger("storedCoins") > 0) {
            player.addCoins(nbt.getInteger("storedCoins"));
            return false;
        }
        else
            return true;
    }

    @Override
    public ItemStack[] onBlockCollect(MinionBase minion, ItemStack[] drops) {
        Inventory tester = minion.getInventory().getContents(); // requires manual looping and adding bc no bukkit inv
        HashMap<Integer, ItemStack> leftOvers = new HashMap<>();
        for (ItemStack stack : drops) {
            leftOvers.putAll(tester.addItem(stack));
        }

        leftOvers.forEach((k, v) -> {
            int i = Skyblock.getPlugin().getMerchantHandler().getPriceHandler().getPrice(v) * sellMultiplier;
            //somehow store the stuff in the item. Maybe add a gui hook or something like that
        });

        return drops;
    }
    */
}
