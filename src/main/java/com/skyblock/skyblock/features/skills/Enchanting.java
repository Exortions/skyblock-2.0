package com.skyblock.skyblock.features.skills;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Enchanting extends Skill {
    public Enchanting() {
        super("Enchanting", "Conjourer", "Enchant items to earn Enchanting\nXP!", new ItemStack(Material.ENCHANTMENT_TABLE), new ItemStack(Material.ENCHANTED_BOOK));
    }

    public double getIntelligence(int level) {
        return level > 14 ? 2 : 1;
    }

    @Override
    public List<String> getRewards(int level, int lastLevel) {
        String extraOrbs = ChatColor.DARK_GRAY + "" + lastLevel * 5 + "➜" + ChatColor.GREEN + level * 5;
        return Arrays.asList(ChatColor.WHITE + " Gain " + extraOrbs + "%" + ChatColor.WHITE + " more",
                "experience orbs from any source", ChatColor.DARK_GRAY + "+" +
                ChatColor.GREEN + getIntelligence(level) + " " + ChatColor.AQUA + "✎ Intelligence");
    }

    @Override
    public void levelUp(SkyblockPlayer player, int prev) {
        player.addStat(SkyblockStat.MAX_MANA, (int) getIntelligence(getLevel(getXP(player))));
        player.addStat(SkyblockStat.MANA, (int) getIntelligence(getLevel(getXP(player))));
    }
}
