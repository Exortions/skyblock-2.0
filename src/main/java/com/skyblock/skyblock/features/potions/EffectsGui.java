package com.skyblock.skyblock.features.potions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class EffectsGui extends Gui {

    public EffectsGui(Player opener) {
        super("Active Effects", 54, new HashMap<String, Runnable>() {{
            put(ChatColor.RED + "Close", opener::closeInventory);
            put(ChatColor.GREEN + "Go Back", () -> opener.performCommand("sb menu"));
        }});

        Util.fillBorder(this);

        this.addItem(48, Util.buildBackButton());
        this.addItem(49, Util.buildCloseButton());

        SkyblockPlayer player = SkyblockPlayer.getPlayer(opener);

        List<PotionEffect> effects = player.getActiveEffects();

        for (PotionEffect effect : effects) {
            this.addItem(Util.createPotion(effect.getName(), effect.getAmplifier(), (int) effect.getDuration()));
        }
    }

    public void showActive(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.getOpenInventory().getTopInventory().getName().equals("Active Effects")) {
                    cancel();
                    return;
                }

                new EffectsGui(player).show(player);
            }
        }.runTaskTimer(Skyblock.getPlugin(), 0, 20);
    }

}
