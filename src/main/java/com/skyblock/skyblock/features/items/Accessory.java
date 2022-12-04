package com.skyblock.skyblock.features.items;

import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Accessory extends SkyblockItem {

    public Accessory(ItemStack baseItem, String internalName) {
        super(baseItem, internalName);
    }

    public abstract void onEquip(SkyblockPlayer player);

    public abstract void onUnEquip(SkyblockPlayer player);

    public static boolean isAccessory(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;

        for (String s : meta.getLore()) {
            String stripped = ChatColor.stripColor(s).toLowerCase();

            if (stripped.endsWith("accessory")) return true;
        }

        return false;
    }
}
