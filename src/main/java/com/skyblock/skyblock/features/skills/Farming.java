package com.skyblock.skyblock.features.skills;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Farming extends Skill {

    public Farming() {
        super("Farming", "Farmhand", "Harvest crops and shear sheep to\nearn Farming XP!", new ItemStack(Material.GOLD_HOE), new ItemStack(Material.HAY_BLOCK));
    }

    public double getHealth(int level)
    {
        int health = level * 2;
        if (level >= 15)
            health += (level - 14);
        if (level >= 20)
            health += (level - 19);
        if (level >= 26)
            health += (level - 25);
        return health;
    }
    @Override
    public List<String> getRewards(int level, int lastLevel) {
        String dropChance = ChatColor.DARK_GRAY + "" + lastLevel * 4 + "➜" + ChatColor.GREEN + level * 4;
        int healthPlus = 2;
        if (level >= 15) healthPlus = 3;
        if (level >= 20) healthPlus = 4;
        if (level >= 26) healthPlus = 5;
        return Arrays.asList(ChatColor.WHITE + " Grants " + dropChance + "%" + ChatColor.WHITE + " chance",
                ChatColor.WHITE + " to drop 2x crops.", ChatColor.DARK_GRAY + "+" +
                        ChatColor.GREEN + healthPlus + " " + ChatColor.RED + "❤ Health");
    }
    @Override
    public void update(SkyblockPlayer player, int prev) {
        super.update(player, prev);

        player.addStat(SkyblockStat.MAX_HEALTH, (int) getHealth(getLevel(getXP(player))));
        player.addStat(SkyblockStat.HEALTH, (int) getHealth(getLevel(getXP(player))));
    }

}
