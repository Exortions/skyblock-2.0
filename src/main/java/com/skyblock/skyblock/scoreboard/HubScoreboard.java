package com.skyblock.skyblock.scoreboard;

import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class HubScoreboard extends Scoreboard {

    public HubScoreboard(Player player) {
        super(player);
    }

    @Override
    void display() {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        DecimalFormat formatter = new DecimalFormat("#,###");
        formatter.setGroupingUsed(true);

        int hours=java.util.Calendar.getInstance().getTime().getHours();
        int minutes=java.util.Calendar.getInstance().getTime().getMinutes();
        LocalDate l_date = LocalDate.now();
        String dateString = l_date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

        objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "SKYBLOCK");

        addLine(9, ChatColor.GRAY + "" + dateString + " " + "Skyblock");
        addLine(8, ChatColor.GRAY + "   ");
        addLine(7, ChatColor.WHITE + " Spring 10th");
        addLine(6, ChatColor.GRAY + " " + hours + ":" + minutes + "pm " + ChatColor.YELLOW + "☀");
        addLine(5, ChatColor.WHITE + " ⏣ " + ChatColor.GRAY + "None");
        addLine(4, ChatColor.WHITE + " ");
        addLine(3, ChatColor.WHITE + "Purse: " + ChatColor.GOLD +  formatter.format((int) skyblockPlayer.getValue("stats.purse")));
        addLine(2, ChatColor.WHITE + "  ");
        addLine(1, ChatColor.YELLOW + "www.hypixel.net");
    }
}
