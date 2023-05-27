package com.skyblock.skyblock.features.scoreboard;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class HubScoreboard extends Scoreboard {

    public HubScoreboard(Player player) {
        super(player);
    }

    @Override
    void display() {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        DecimalFormat formatter = new DecimalFormat("#,###");
        formatter.setGroupingUsed(true);

        String date = new SimpleDateFormat("MM/dd/yy").format(Skyblock.getPlugin(Skyblock.class).getDate());

        SkyblockLocation currentLocation = skyblockPlayer.getCurrentLocation();

        String loc = currentLocation == null ? ChatColor.GRAY + "None" : currentLocation.getColor() + currentLocation.getName();

        if (currentLocation == null) loc = ChatColor.GRAY + "None";

        if (player.getWorld().getName().startsWith(IslandManager.ISLAND_PREFIX)) loc = ChatColor.GREEN + "Private Island";

        QuestLine line = skyblockPlayer.getQuestLine();

        int offset = 0;


        if (line != null) {
            offset += 4;

            if (line.getObjective(skyblockPlayer).hasSuffix(skyblockPlayer)) offset += 1;
        }

        addLine(9 + offset, ChatColor.GRAY + "" + date + ChatColor.DARK_GRAY + " Skyblock");
        addLine(8 + offset, ChatColor.GRAY + "   ");
        addLine(7 + offset, ChatColor.WHITE + " " + Skyblock.getPlugin(Skyblock.class).getTimeManager().getDate());
        addLine(6 + offset, ChatColor.GRAY + " " + Skyblock.getPlugin(Skyblock.class).getTimeManager().getTime() + " " + Skyblock.getPlugin(Skyblock.class).getTimeManager().getIcon());
        if (!loc.contains("Private Island")) {
            if (currentLocation != null) addLine(5 + offset, ChatColor.GRAY + " ⏣ " + currentLocation.getColor() + loc);
            else addLine(5 + offset,  ChatColor.GRAY + " ⏣ " + ChatColor.WHITE+ loc);
        } else {
            addLine(5 + offset, ChatColor.GRAY + " ⏣ " + ChatColor.GREEN + loc);
        }
        addLine(4 + offset, ChatColor.WHITE + " ");

        StringBuilder purse = new StringBuilder(ChatColor.WHITE + "Purse: " + ChatColor.GOLD +  Util.formatDouble(skyblockPlayer.getDouble("stats.purse")));

        if (skyblockPlayer.hasExtraData("lastpicked_coins")) {
            purse.append(ChatColor.YELLOW + " (+" + Util.formatInt((int) skyblockPlayer.getExtraData("lastpicked_coins")) + ")");
        }

        addLine(3 + offset, purse.toString());

        if (line != null) {
            int off = line.getObjective(skyblockPlayer).hasSuffix(skyblockPlayer) ? 1 : 0;

            addLine(5 + off, "     ");
            addLine(4 + off, ChatColor.WHITE + "Objective");
            addLine(3 + off, ChatColor.YELLOW + line.getObjective(skyblockPlayer).getDisplay());

            if (line.getObjective(skyblockPlayer).hasSuffix(skyblockPlayer)) addLine(3, " " + line.getObjective(skyblockPlayer).getSuffix(skyblockPlayer));
        }

        addLine(2, ChatColor.WHITE + "  ");
        addLine(1, ChatColor.YELLOW + "www.hypixel.net");
    }
}
