package com.skyblock.skyblock.features.bazaar.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.bazaar.BazaarCategory;
import com.skyblock.skyblock.features.bazaar.BazaarItem;
import com.skyblock.skyblock.features.bazaar.BazaarSubItem;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;

public class BazaarItemGui extends Gui {

    public BazaarItemGui(Player player, BazaarCategory category, BazaarItem item) {
        super(category.getName() + " ➜ " + item.getName(), item.getInventorySize(), new HashMap<String, Runnable>() {{
            for (BazaarSubItem subItem : item.getSubItems()) {
                put(subItem.getNamedIcon().toItemStack().getItemMeta().getDisplayName(), () -> {
                    new BazaarSubItemGui(player, subItem).show(player);
                });
            }
        }});

        Util.fillEmpty(this);

        for (BazaarSubItem subItem : item.getSubItems()) {
            ItemStack stack = new ItemStack(subItem.getIcon().getType());
            ItemMeta meta = stack.getItemMeta();

            meta.setDisplayName(subItem.getIcon().getItemMeta().getDisplayName() == null ? subItem.getIcon().getType().name() : subItem.getIcon().getItemMeta().getDisplayName());
            meta.setLore(Arrays.asList(Util.buildLore(
                    "&8" + subItem.getCommodity().toString() + " commodity\n\n" +
                            "&7Buy Price: &6" + Skyblock.getPlugin().getBazaar().getEscrow().getBuyPrice(subItem) +
                            "\n&7Sell Price: &6" + Skyblock.getPlugin().getBazaar().getEscrow().getSellPrice(subItem) + "\n\n&eClick to view details!")));

            stack.setItemMeta(meta);

            this.addItem(subItem.getSlot(), stack);
        }

        this.addItem(31, Util.buildBackButton("&7To Bazaar"));
    }

}
