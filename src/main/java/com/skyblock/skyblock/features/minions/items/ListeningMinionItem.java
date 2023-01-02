package com.skyblock.skyblock.features.minions.items;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class ListeningMinionItem extends MinionItem implements Listener {

    public ListeningMinionItem(ItemStack baseItem, String internalName, MinionItemType type, boolean stackable, boolean guiEquippable) {
        super(baseItem, internalName, type, stackable, guiEquippable);

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin(Skyblock.class));
    }
}
