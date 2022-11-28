package com.skyblock.skyblock.features.skills;

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

}
