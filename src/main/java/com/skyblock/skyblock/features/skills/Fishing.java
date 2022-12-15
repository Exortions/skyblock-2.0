package com.skyblock.skyblock.features.skills;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Fishing extends Skill {

    public Fishing() {
        super(
                "Fishing",
                "Treasure Hunter",
                "Visit your local pond to fish\nand earn Fishing XP!",
                new ItemStack(Material.FISHING_ROD),
                new ItemStack(Material.PRISMARINE)
        );
    }

    public double getHealth(int level) {
        int health = 2;

        if (level > 14) health += 1;
        if (level > 19) health += 1;
        if (level > 25) health += 1;

        return health;
    }

    @Override
    public List<String> getRewards(int level, int lastLevel) {
        double percent = 0.2 * level;
        int health = 2;
        int coins;

        if (level < 3) coins = 25 * level;
        else {
            coins = 100;

            for (int i = 4; i < level; i++) {
                coins += 100;
            }
        }

        return Arrays.asList(
                ChatColor.WHITE + " Increases the chance to find",
                ChatColor.WHITE + " treasure when fishing by",
                ChatColor.DARK_GRAY + " " + (percent - 0.2) + "➜" + ChatColor.GREEN + "" + percent + ChatColor.WHITE + "%.",
                ChatColor.DARK_GRAY + "+" + ChatColor.GREEN + health + ChatColor.RED + " ❤ Health",
                ChatColor.DARK_GRAY + "+" + ChatColor.GOLD + coins + ChatColor.GRAY + " Coins"
        );
    }

    @Override
    public void levelUp(SkyblockPlayer player, int prev) {
        player.addStat(SkyblockStat.MAX_HEALTH, (int) getHealth(getLevel(getXP(player))));
        player.addStat(SkyblockStat.HEALTH, (int) getHealth(getLevel(getXP(player))));
    }

}
