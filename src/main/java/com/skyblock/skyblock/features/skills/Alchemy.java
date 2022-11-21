package com.skyblock.skyblock.features.skills;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class Alchemy extends Skill {

    public Alchemy() {
        super("Alchemy", "Brewer", "Brew potions to earn Alchemy XP!");
    }

    public double getIntelligence(int level) {
        return level > 14 ? 2 : 1;
    }
    @Override
    public List<String> getRewards(int level, int lastLevel) {
        String durationBoost = ChatColor.DARK_GRAY + "" + lastLevel + "➜" + ChatColor.GREEN + level;
        return Arrays.asList(ChatColor.WHITE + " Potions that you brew have a ", durationBoost + "%" + ChatColor.WHITE + " longer duration."
                        , ChatColor.DARK_GRAY + "+" + ChatColor.GREEN + getIntelligence(level) + " " + ChatColor.AQUA + "✎ Intelligence");
    }

    @Override
    public void update(SkyblockPlayer player, int prev) {
        super.update(player, prev);

        player.addStat(SkyblockStat.MAX_MANA, (int) getIntelligence(getLevel(getXP(player))));
        player.addStat(SkyblockStat.MANA, (int) getIntelligence(getLevel(getXP(player))));
    }
}
