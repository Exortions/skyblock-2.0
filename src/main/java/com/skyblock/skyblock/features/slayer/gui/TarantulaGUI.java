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

public class TarantulaGUI extends Gui {
    private static final SlayerHandler slayerHandler = Skyblock.getPlugin(Skyblock.class).getSlayerHandler();
    public TarantulaGUI(Player opener) {
        super("Tarantula Broodfather", 54, new HashMap<String, Runnable>(){{
            for (int i = 1; i < 5; i++) {
                int finalI = i;
                put(SlayerGUI.COLORS.get(i - 1) + "Tarantula Broodfather " + Util.toRoman(i), () -> {
                    SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(opener);

                    if (skyblockPlayer.checkCoins(SlayerGUI.COINS.get(finalI - 1))) {
                        slayerHandler.startQuest(opener, SlayerType.TARANTULA, finalI);
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

        addItem(11, SlayerGUI.getStartItem(SlayerType.TARANTULA, 1, 750, 35, 5, 100, 1));
        addItem(12, SlayerGUI.getStartItem(SlayerType.TARANTULA, 2, 30000, 110, 25, 2000, 2));
        addItem(13, SlayerGUI.getStartItem(SlayerType.TARANTULA, 3, 900000, 525, 100, 10000, 2));
        addItem(14, SlayerGUI.getStartItem(SlayerType.TARANTULA, 4, 2400000, 1325, 500, 50000, 2));

        addItem(15, new ItemBuilder(ChatColor.RED + "Not released yet!", Material.COAL_BLOCK).addLore(ChatColor.GRAY + "This boss is still in", ChatColor.GRAY + "development!").toItemStack());
    }
}
