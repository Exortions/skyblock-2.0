package com.skyblock.skyblock.features.minions.items;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;

import org.bukkit.inventory.ItemStack;
import com.skyblock.skyblock.features.minions.items.MinionItem;
import com.skyblock.skyblock.features.minions.items.items.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MinionItemHandler {

    private final HashMap<String, MinionItem> items;

    public MinionItemHandler(Skyblock plugin){
        items = new HashMap<>();
        registerItem(new AutoSmelter());
        registerItem(new MinionExpander());
        registerItem(new Storage("SMALL", 3 * 64));
        registerItem(new Storage("MEDIUM", 9 * 64));
        registerItem(new Storage("LARGE", 15 * 64));
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
}
