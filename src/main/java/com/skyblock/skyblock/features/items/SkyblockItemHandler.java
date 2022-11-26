package com.skyblock.skyblock.features.items;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.items.armor.SuperiorDragonArmor;
import com.skyblock.skyblock.features.items.misc.GrapplingHook;
import com.skyblock.skyblock.features.items.misc.MaddoxBatphone;
import com.skyblock.skyblock.features.items.weapons.AspectOfTheDragons;
import com.skyblock.skyblock.features.items.weapons.AspectOfTheEnd;
import com.skyblock.skyblock.features.items.weapons.EndStoneSword;
import com.skyblock.skyblock.features.items.weapons.RogueSword;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SkyblockItemHandler {

    private final HashMap<String, SkyblockItem> items;
    private final HashMap<String, ArmorSet> sets;

    public SkyblockItemHandler(Skyblock plugin){
        items = new HashMap<>();
        sets = new HashMap<>();

        // Misc
        registerItem(new GrapplingHook(plugin));
        registerItem(new MaddoxBatphone());

        // Weapons
        registerItem(new AspectOfTheDragons(plugin));
        registerItem(new AspectOfTheEnd(plugin));
        registerItem(new EndStoneSword(plugin));
        registerItem(new RogueSword(plugin));

        // Armor Sets
        registerItem(new SuperiorDragonArmor());
    }

    public ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (Map.Entry<String, SkyblockItem> entry : items.entrySet()) {
            list.add(entry.getValue().getItem());
        }
        return list;
    }

    private void registerItem(SkyblockItem item){
        items.put(item.getInternalName(), item);
    }

    public boolean isRegistered(ItemStack item){
        return getRegistered(item) != null;
    }

    public SkyblockItem getRegistered(ItemStack item){
        if (item == null)  return null;
        if (item.getItemMeta() == null) return null;

        if (item.getItemMeta().hasDisplayName()) {
            NBTItem nbtItem = new NBTItem(item);
            return items.get(nbtItem.getString("skyblockId"));
        }

        return null;
    }

    public SkyblockItem getRegistered(String s){
        return items.get(s);
    }

    private void registerItem(ArmorSet set){
        sets.put(set.getId(), set);
    }

    public boolean isRegistered(ItemStack[] set){
        return getRegistered(set) != null;
    }

    public ArmorSet getRegistered(ItemStack[] set){
        ItemStack helmet = set[3];
        ItemStack chest = set[2];
        ItemStack legs = set[1];
        ItemStack boots = set[0];

        for (Map.Entry<String, ArmorSet> entry : sets.entrySet()) {
            ArmorSet armorSet = entry.getValue();
            if (getID(helmet).equals(getID(armorSet.getHelmet())) &&
                getID(chest).equals(getID(armorSet.getChest())) &&
                getID(legs).equals(getID(armorSet.getLegs())) &&
                getID(boots).equals(getID(armorSet.getBoots()))) {
                return armorSet;
            }
        }

        return null;
    }

    private String getID(ItemStack stack) {
        if (!Util.notNull(stack)) return "";
        return new NBTItem(stack).getString("skyblockId");
    }
}
