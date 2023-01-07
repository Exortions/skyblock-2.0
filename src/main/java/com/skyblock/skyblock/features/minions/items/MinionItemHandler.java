package com.skyblock.skyblock.features.minions.items;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;

import org.bukkit.inventory.ItemStack;
import com.skyblock.skyblock.features.minions.items.MinionItem;
//import com.skyblock.skyblock.features.minions.items.skins.*;
import com.skyblock.skyblock.features.minions.items.storages.*;
import com.skyblock.skyblock.features.minions.items.upgrades.*;
import com.skyblock.skyblock.features.minions.items.fuels.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MinionItemHandler {

    private final HashMap<String, MinionItem> items;

    public MinionItemHandler(Skyblock plugin){
        items = new HashMap<>();
        // Upgrades
        registerItem(new AutoSmelter());
        registerItem(new MinionExpander());
        registerItem(new DiamondSpreading());

        // Storages
        registerItem(new Storage("SMALL", 3));
        registerItem(new Storage("MEDIUM", 9));
        registerItem(new Storage("LARGE", 15));

        // Fuels
        registerItem(new GenericFuel("ENCHANTED_LAVA_BUCKET.json", "enchanted_lava_bucket", false, -1, 1.25f));
        registerItem(new GenericFuel("COAL.json", "coal", true, 30, 1.05f));
        registerItem(new GenericFuel("COAL_BLOCK.json", "coal_block", true, 60*5, 1.05f));
        registerItem(new GenericFuel("ENCHANTED_COAL.json", "enchanted_coal", true, 60*24, 1.1f));
        registerItem(new GenericFuel("ENCHANTED_CHARCOAL.json", "enchanted_charcoal", true, 60*36, 1.2f));
        registerItem(new GenericFuel("ENCHANTED_BREAD.json", "enchanted_bread", true, 60*12, 1.05f));
        registerItem(new GenericFuel("FOUL_FLESH.json", "foul_flesh", false, 60*5, 1.9f));
        registerItem(new GenericFuel("CATALYST.json", "catalyst", false, 60*3, 3f));
        registerItem(new GenericFuel("HAMSTER_WHEEL.json", "hamster_wheel", false, 60*24, 1.5f));
        registerItem(new SolarPanel());
    }

    public ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (Map.Entry<String, MinionItem> entry : items.entrySet()) {
            list.add(entry.getValue().getItem());
        }
        return list;
    }

    private void registerItem(MinionItem item){
        items.put(item.getInternalName(), item);
    }

    public boolean isRegistered(ItemStack item){
        return getRegistered(item) != null;
    }

    public MinionItem getRegistered(ItemStack item){
        if (item == null) return null;
        if (item.getItemMeta() == null) return null;

        if (item.getItemMeta().hasDisplayName()) {
            NBTItem nbtItem = new NBTItem(item);

            return items.get(nbtItem.getString("skyblockId"));
        }

        return null;
    }

    private String getID(ItemStack stack) {
        if (!Util.notNull(stack)) return "";
        return new NBTItem(stack).getString("skyblockId");
    }

    public MinionItem getRegistered(String id) {
        if (items.containsKey(id))  return items.get(id);
        else return null;
    }
}
