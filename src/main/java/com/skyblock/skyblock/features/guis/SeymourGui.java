package com.skyblock.skyblock.features.guis;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SeymourGui extends Gui {

    public SeymourGui(Player opener) {
        super("Seymour's Fancy Suits", 36, new HashMap<String, Runnable>() {{
            put(ChatColor.RED + "Close", opener::closeInventory);

            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(opener);

            put(ChatColor.DARK_PURPLE + "Cheap Tuxedo Jacket", () -> purchase(skyblockPlayer, 3000000, "cheap"));

            put(ChatColor.GOLD + "Fancy Tuxedo Jacket",  () -> purchase(skyblockPlayer, 20000000, "fancy"));

            put(ChatColor.GOLD + "Elegant Tuxedo Jacket", () -> purchase(skyblockPlayer, 74999999, "elegant"));
        }});

        Util.fillEmpty(this);

        this.addItem(31, Util.buildCloseButton());

        SkyblockPlayer player = SkyblockPlayer.getPlayer(opener);

        this.addItem(11, generateTuxedo(player, "cheap_tuxedo_chestplate", 100, 100, 75, 50, 3000000));
        this.addItem(13, generateTuxedo(player, "fancy_tuxedo_chestplate", 150, 300, 150, 100, 20000000));
        this.addItem(15, generateTuxedo(player, "elegant_tuxedo_chestplate", 200, 500, 250, 150, 74999999));
    }

    public ItemStack generateTuxedo(SkyblockPlayer player, String original, int critDamage, int intelligence, int health, int damage, int cost) {
        return new ItemBuilder(Util.getItem("skyblock:" + original).clone()).setLore(
                Util.buildLoreList(
                        "\n&8Complete Suit\n&7Crit Damage: &c+" + critDamage + "%\n&7Intelligence: &a+" + intelligence + "\n\n&6Full Set Bonnus: Dashing\n" +
                                "&7Max health set to &c" + health + SkyblockStat.HEALTH.getIcon() + "&7.\n&7Deal &c+" + damage + "% &7damage!\n&8Very stylish.\n\n&7Cost: &6" + Util.formatInt(cost) + " Coins\n\n" +
                                (player.getCoins() >= cost ? "&eClick to purchase!" : "&cCan't afford this!")
                )
        ).toItemStack();
    }

    public static void purchase(SkyblockPlayer player, int cost, String item) {
        if (player.getCoins() < cost) {
            player.getBukkitPlayer().sendMessage(ChatColor.RED + "You can't afford this!");
            return;
        }

        player.subtractCoins(cost);
        player.getBukkitPlayer().getInventory().addItem(Util.getItem("skyblock:" + item + "_tuxedo_chestplate.json").clone());
        player.getBukkitPlayer().getInventory().addItem(Util.getItem("skyblock:" + item + "_tuxedo_leggings.json").clone());
        player.getBukkitPlayer().getInventory().addItem(Util.getItem("skyblock:" + item + "_tuxedo_boots.json").clone());
    }

}
