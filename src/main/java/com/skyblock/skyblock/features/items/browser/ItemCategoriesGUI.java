package com.skyblock.skyblock.features.items.browser;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import com.skyblock.skyblock.utilities.sign.SignGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static com.skyblock.skyblock.features.items.browser.BrowserCategory.*;

public class ItemCategoriesGUI extends Gui {
    public ItemCategoriesGUI(Player p) {
        super("Item Categories", 54, new HashMap<>());

        addItem(10, ALL);
        addItem(11, SWORD);
        addItem(12, BOW);
        addItem(13, HELMET);
        addItem(14, CHESTPLATE);
        addItem(15, LEGGING);
        addItem(16, BOOT);
        addItem(19, PICKAXE);
        addItem(20, AXE);
        addItem(21, SHOVEL);
        addItem(22, HOE);
        addItem(23, ACCESSORIE);
        addItem(24, MATERIAL);
        addItem(25, FISHING);
        addItem(28, WAND);
        addItem(29, ENCHANTED_BOOK);
        addItem(30, SACK);
        addItem(31, TRAVEL_SCROLL);
        addItem(32, MINION);
        addItem(33, PET);
        addItem(34, PET_ITEM);
        addItem(37, POTION);
        addItem(38, BLOCKS);
        addItem(39, MISC);

        addItem(48, Util.buildCloseButton());
        addItem(49, new ItemBuilder(ChatColor.GREEN + "Search Items", Material.SIGN).addLore("&7Search through all items.").toItemStack());

        getSpecificClickEvents().put(getItems().get(49), () -> {
            SignGui sign = new SignGui(Skyblock.getPlugin().getSignManager(), e -> {
                String[] lines = e.getLines();
                StringBuilder search = new StringBuilder();

                for (String line : lines) search.append(line);

                Util.delay(() -> new ItemSearchGUI(search.toString(), 1, p).show(p), 1);
            });

            sign.open(p);
        });

        for (BrowserCategory cat : BrowserCategory.values())
            getSpecificClickEvents().put(cat.getItem(), () -> new ItemCategoryGUI(cat, 1, p).show(p));
    }

    private void addItem(int slot, BrowserCategory cat) { addItem(slot, cat.getItem()); }

}
