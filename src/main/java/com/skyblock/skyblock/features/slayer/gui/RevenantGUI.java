package com.skyblock.skyblock.features.slayer.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.slayer.SlayerHandler;
import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class RevenantGUI extends Gui {

    private static final SlayerHandler slayerHandler = Skyblock.getPlugin(Skyblock.class).getSlayerHandler();
    public RevenantGUI(Player opener) {
        super("Revenant Horror", 54, new HashMap<String, Runnable>() {{
            for (int i = 1; i < 5; i++) {
                int finalI = i;
                put(SlayerGUI.COLORS.get(i - 1) + "Revenant Horror " + Util.toRoman(i), () -> {
                    SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(opener);

                    if (skyblockPlayer.checkCoins(SlayerGUI.COINS.get(finalI - 1))) {
                        slayerHandler.startQuest(opener, SlayerType.REVENANT, finalI);
                    }

                    opener.closeInventory();
                });
            }
        }});

        Util.fillEmpty(this);

        addItem(49, Util.buildBackButton(""));

        addItem(11, SlayerGUI.getStartItem(SlayerType.REVENANT, 1, 500, 15, 5, 100, 1));
        addItem(12, SlayerGUI.getStartItem(SlayerType.REVENANT, 2, 20000, 25, 25, 2000, 2));
        addItem(13, SlayerGUI.getStartItem(SlayerType.REVENANT, 3, 400000, 120, 100, 10000, 3));
        addItem(14, SlayerGUI.getStartItem(SlayerType.REVENANT, 4, 1500000, 400, 500, 50000, 3));

        addItem(15, new ItemBuilder(ChatColor.RED + "Not released yet!", Material.COAL_BLOCK).addLore(ChatColor.GRAY + "This boss is still in", ChatColor.GRAY + "development!").toItemStack());
    }
}
