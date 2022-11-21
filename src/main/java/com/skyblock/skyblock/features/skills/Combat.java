package com.skyblock.skyblock.features.skills;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class Combat extends Skill {

    public Combat() {
        super("Combat", "Warrior", "Fight mobs and players to\nearn Combat XP!");
    }

    @Override
    public List<String> getRewards(int level, int lastLevel) {
        return Arrays.asList(ChatColor.WHITE + " Deal " + ChatColor.DARK_GRAY + "" + lastLevel * 4 + "➜" +
                        ChatColor.GREEN + level * 4 + "% " + ChatColor.WHITE +
                        "more damage to mobs.",
                ChatColor.DARK_GRAY + "+" + ChatColor.GREEN + "1% " + ChatColor.BLUE + "☣ Crit Chance");
    }

    @Override
    public void update(SkyblockPlayer player, int prev) {
        super.update(player, prev);

        player.addStat(SkyblockStat.CRIT_CHANCE, (getLevel(getXP(player))));
    }
}
