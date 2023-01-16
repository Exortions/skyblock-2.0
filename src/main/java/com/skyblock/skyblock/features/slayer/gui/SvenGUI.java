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

import java.util.HashMap;

public class SvenGUI extends Gui {
    private static final SlayerHandler slayerHandler = Skyblock.getPlugin(Skyblock.class).getSlayerHandler();
    public SvenGUI(Player opener) {
        super("Sven Packmaster", 54, new HashMap<String, Runnable>(){{
            for (int i = 1; i < 5; i++) {
                int finalI = i;
                put(SlayerGUI.COLORS.get(i - 1) + "Sven Packmaster " + Util.toRoman(i), () -> {
                    SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(opener);

                    if (skyblockPlayer.checkCoins(SlayerGUI.COINS.get(finalI - 1))) {
                        slayerHandler.startQuest(opener, SlayerType.SVEN, finalI);
                    }

                    opener.closeInventory();
                });
            }

            put(ChatColor.RED + "Close", opener::closeInventory);
            put(ChatColor.GREEN + "Go Back", () -> new SlayerGUI(opener).show(opener));
        }});

        Util.fillEmpty(this);

        addItem(48, Util.buildBackButton("&7To Slayer"));
        addItem(49, Util.buildCloseButton());

        addItem(11, SlayerGUI.getStartItem(SlayerType.SVEN, 1, 2000, 60, 5, 100, 1));
        addItem(12, SlayerGUI.getStartItem(SlayerType.SVEN, 2, 40000, 200, 25, 2000, 2));
        addItem(13, SlayerGUI.getStartItem(SlayerType.SVEN, 3, 750000, 450, 100, 10000, 3));
        addItem(14, SlayerGUI.getStartItem(SlayerType.SVEN, 4, 2000000, 1100, 500, 50000, 3));

        addItem(15, new ItemBuilder(ChatColor.RED + "Not released yet!", Material.COAL_BLOCK).addLore(ChatColor.GRAY + "This boss is still in", ChatColor.GRAY + "development!").toItemStack());
    }
}
