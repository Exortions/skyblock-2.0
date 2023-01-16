package com.skyblock.skyblock.features.items;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class ListeningItem extends SkyblockItem implements Listener {

    public ListeningItem(String internalName) {
        super(internalName);

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin(Skyblock.class));
    }

    public ListeningItem(ItemStack baseItem, String internalName) {
        super(baseItem, internalName);

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin(Skyblock.class));
    }
}
