package com.skyblock.skyblock.features.items;

import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;

public interface DynamicLore {

    default void replaceLore(ItemBase base) {
        List<String> lore = base.getDescription();
        List<String> ability = base.getAbilityDescription();

        if (lore == null) return;

        int i = 0;
        for (String s : lore) {
            for (String rep : toReplace()) {
                if (s.startsWith(rep)) {
                    lore.set(lore.indexOf(s), replaceWith(new NBTItem(base.getOrig()))[i]);
                    i++;
                }
            }
        }

        for (String s : ability) {
            for (String rep : toReplace()) {
                if (s.startsWith(rep)) {
                    ability.set(ability.indexOf(s), replaceWith(new NBTItem(base.getOrig()))[i]);
                    i++;
                }
            }
        }

        base.setDescription(lore);
        base.setAbilityDescription(ability);

        base.setStack(null);

        base.createStack();
    }

    // Note: Order matters on these
    String[] toReplace();
    String[] replaceWith(NBTItem nbtItem);
}