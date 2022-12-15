package com.skyblock.skyblock.features.skills;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Mining extends Skill {

    public Mining() {
        super("Mining", "Spelunker", "Spelunk islands for ores and\nvaluable materials to earn\nMining XP!", new ItemStack(Material.STONE_PICKAXE), new ItemStack(Material.IRON_BLOCK));
    }

    public double getDefense(int level) {
        return level > 14 ? 2 : 1;
    }

    @Override
    public List<String> getRewards(int level, int lastLevel) {
        String dropChance = ChatColor.DARK_GRAY + "" + lastLevel * 4 + "➜" + ChatColor.GREEN + level * 4;
        if (level > 25) dropChance = ChatColor.DARK_GRAY + "" + (lastLevel - 25) * 4 + "➜" + ChatColor.GREEN + (level - 25) * 4;
        return Arrays.asList(ChatColor.WHITE + " Grants " + dropChance + "%" + ChatColor.WHITE + " chance",
                ChatColor.WHITE + " to drop " + (level > 25 ? "3" : "2") + "x ores.", ChatColor.DARK_GRAY + "+" +
                        ChatColor.GREEN + (level >= 15 ? "2" : "1") + " " + ChatColor.GREEN + "❈ Defense");
    }

    @Override
    public void levelUp(SkyblockPlayer player, int prev) {
        player.addStat(SkyblockStat.DEFENSE, (int) getDefense(getLevel(getXP(player))));
    }
}
