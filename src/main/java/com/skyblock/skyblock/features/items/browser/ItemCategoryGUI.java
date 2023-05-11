package com.skyblock.skyblock.features.items.browser;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import com.skyblock.skyblock.utilities.item.ItemHandler;
import com.skyblock.skyblock.utilities.sign.SignClickCompleteHandler;
import com.skyblock.skyblock.utilities.sign.SignCompleteEvent;
import com.skyblock.skyblock.utilities.sign.SignGui;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemCategoryGUI extends Gui {

    private static final Skyblock plugin = Skyblock.getPlugin();
    public ItemCategoryGUI(BrowserCategory cat, int page, Player p) {
        super("Item Category: " + WordUtils.capitalize(cat.name().toLowerCase().replace('_', ' ')), 54, new HashMap<>());

        ItemHandler handler = plugin.getItemHandler();
        List<ItemStack> items = new ArrayList<>();

        if (handler.getCategories().containsKey(cat)) {
            items.addAll(handler.getCategories().get(cat));
        } else {
            for (ItemStack item : plugin.getItemHandler().getItems().values()) {
                if (!cat.getValidate().test(item)) continue;

                items.add(item.clone());
            }

            items.sort(Util.compareItems());

            handler.getCategories().put(cat, items);
        }

        int start = (page - 1) * 45;
        int end = (page - 1) * 45 + 45;

        int setItemIndex = 0;

        for (int i = start; i < end; i++) {
            try {
                ItemStack item = items.get(i);

                addItem(setItemIndex, item);
                getSpecificClickEvents().put(item, () -> {
                    p.getInventory().addItem(plugin.getItemHandler().getItem(item));
                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, 2);
                });
                setItemIndex++;
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        int maxPage = ((int) Math.ceil(items.size() / 45F));

        if (page != 1) addItem(45, new ItemBuilder(ChatColor.GREEN + "Previous Page", Material.ARROW).toItemStack());
        if (page != maxPage) addItem(53, new ItemBuilder(ChatColor.GREEN + "Next Page", Material.ARROW).toItemStack());

        addItem(48, Util.buildBackButton("&7To item categories"));
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

        getClickEvents().put(ChatColor.GREEN + "Go Back", () -> new ItemCategoriesGUI(p).show(p));
        getClickEvents().put(ChatColor.GREEN + "Previous Page", () -> new ItemCategoryGUI(cat, page - 1, p).show(p));
        getClickEvents().put(ChatColor.GREEN + "Next Page", () -> new ItemCategoryGUI(cat, page + 1, p).show(p));
    }

    @Override
    protected boolean getSpecificClickSound() {
        return false;
    }
}
