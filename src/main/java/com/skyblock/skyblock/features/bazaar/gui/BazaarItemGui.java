package com.skyblock.skyblock.features.bazaar.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.bazaar.BazaarCategory;
import com.skyblock.skyblock.features.bazaar.BazaarItem;
import com.skyblock.skyblock.features.bazaar.BazaarSubItem;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;

public class BazaarItemGui extends Gui {

    public BazaarItemGui(Player player, BazaarCategory category, BazaarItem item) {
        super(category.getName() + " ➜ " + item.getName(), item.getInventorySize(), new HashMap<String, Runnable>() {{
        }});

        Util.fillEmpty(this);

        for (BazaarSubItem subItem : item.getSubItems()) {
            this.addItem(subItem.getSlot(), new ItemBuilder(subItem.getIcon()).setDisplayName(category.getColor() + ChatColor.stripColor(subItem.getIcon().getItemMeta().getDisplayName() == null ? WordUtils.capitalize(subItem.getIcon().getType().name().toLowerCase().replace("_", " ")) : subItem.getIcon().getItemMeta().getDisplayName())).addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).setLore(Arrays.asList(Util.buildLore(
                    "&8" + subItem.getCommodity().toString() + " commodity\n\n" +
                            "&7Buy Price: &6" + Skyblock.getPlugin().getBazaar().getEscrow().getBuyPrice(subItem) +
                            "\n&7Sell Price: &6" + Skyblock.getPlugin().getBazaar().getEscrow().getSellPrice(subItem) + "\n\n&eClick to view details!"))).toItemStack());
        }
        ;
        this.addItem(31, Util.buildBackButton("&7To Bazaar"));
    }

}
