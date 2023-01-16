package com.skyblock.skyblock.features.items;

import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;

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

        if (ability != null) {
            for (String s : ability) {
                for (String rep : toReplace()) {
                    if (s.startsWith(rep)) {
                        ability.set(ability.indexOf(s), replaceWith(new NBTItem(base.getOrig()))[i]);
                        i++;
                    }
                }
            }
        }

        base.setDescription(lore);
        if (ability != null) base.setAbilityDescription(ability);

        base.setStack(null);

        base.createStack();
    }

    // Note: Order matters on these
    String[] toReplace();
    String[] replaceWith(NBTItem nbtItem);
}