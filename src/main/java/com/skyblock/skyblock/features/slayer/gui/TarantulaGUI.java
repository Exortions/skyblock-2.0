package com.skyblock.skyblock.features.slayer.gui;

import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TarantulaGUI extends Gui {
    public TarantulaGUI(Player opener) {
        super("Tarantula Broodfather", 54, new HashMap<String, Runnable>());

        Util.fillEmpty(this);

        addItem(49, Util.buildBackButton(""));

        addItem(11, SlayerGUI.getStartItem(SlayerType.TARANTULA, 1, 750, 35, 5, 100, 1));
        addItem(12, SlayerGUI.getStartItem(SlayerType.TARANTULA, 2, 30000, 45, 25, 2000, 2));
        addItem(13, SlayerGUI.getStartItem(SlayerType.TARANTULA, 3, 900000, 210, 100, 10000, 2));
        addItem(14, SlayerGUI.getStartItem(SlayerType.TARANTULA, 4, 2400000, 530, 500, 50000, 2));

        addItem(15, new ItemBuilder(ChatColor.RED + "Not released yet!", Material.COAL_BLOCK).addLore(ChatColor.GRAY + "This boss is still in", ChatColor.GRAY + "development!").toItemStack());
    }
}
