package com.skyblock.skyblock.features.skills;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Foraging extends Skill {

    public Foraging() {
        super("Foraging", "Logger", "Cut trees and forage for other\nplants to earn Foraging XP!", new ItemBuilder("Foraging", Material.SAPLING, (short) 3).toItemStack(), new ItemStack(Material.LOG));
    }

    public double getStrength(int level) {
        return level > 14 ? 2 : 1;
    }

    @Override
    public List<String> getRewards(int level, int lastLevel) {
        String dropChance = ChatColor.DARK_GRAY + "" + lastLevel * 4 + "➜" + ChatColor.GREEN + level * 4;
        if (level > 25)
            dropChance = ChatColor.DARK_GRAY + "" + (lastLevel - 25) * 4 + "➜" + ChatColor.GREEN + (level - 25) * 4;
        return Arrays.asList(ChatColor.WHITE + " Grants " + dropChance + "%" + ChatColor.WHITE + " chance",
                ChatColor.WHITE + " to drop " + (level > 25 ? "3" : "2") + "x logs.", ChatColor.DARK_GRAY + "+" +
                        ChatColor.GREEN + (level >= 15 ? "2" : "1") + " " + ChatColor.RED + "❁ Strength");
    }

    @Override
    public void levelUp(SkyblockPlayer player, int prev) {
        player.addStat(SkyblockStat.STRENGTH, (int) getStrength(getLevel(getXP(player))));
    }
}
