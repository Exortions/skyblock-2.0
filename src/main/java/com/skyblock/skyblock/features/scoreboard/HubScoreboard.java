package com.skyblock.skyblock.features.scoreboard;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class HubScoreboard extends Scoreboard {

    public HubScoreboard(Player player) {
        super(player);

        runTask();
    }

    private int tick = 0;

    @Override
    void display() {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        DecimalFormat formatter = new DecimalFormat("#,###");
        formatter.setGroupingUsed(true);

        String dateString = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

        SkyblockLocation currentLocation = skyblockPlayer.getCurrentLocation();

        String loc = currentLocation == null ? ChatColor.GRAY + "None" : currentLocation.getColor() + currentLocation.getName();

        if (player.getWorld().getName().startsWith(IslandManager.ISLAND_PREFIX)) loc = ChatColor.GREEN + "Private Island";

        this.animateTitle();

        addLine(9, ChatColor.GRAY + "" + dateString + ChatColor.DARK_GRAY + " Skyblock");
        addLine(8, ChatColor.GRAY + "   ");
        addLine(7, ChatColor.WHITE + " " + Skyblock.getPlugin(Skyblock.class).getTimeManager().getDate());
        addLine(6, ChatColor.GRAY + " " + Skyblock.getPlugin(Skyblock.class).getTimeManager().getTime() + " " + Skyblock.getPlugin(Skyblock.class).getTimeManager().getIcon());
        addLine(5, ChatColor.WHITE + " ⏣ " + loc);
        addLine(4, ChatColor.WHITE + " ");
        addLine(3, ChatColor.WHITE + "Purse: " + ChatColor.GOLD +  formatter.format((int) skyblockPlayer.getValue("stats.purse")));
        addLine(2, ChatColor.WHITE + "  ");
        addLine(1, ChatColor.YELLOW + "www.hypixel.net");

        tick++;
    }

    public void animateTitle() {
        switch (tick) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 21:
            case 22:
                objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "SKYBLOCK");
                break;
            case 11:
                objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "S" + ChatColor.YELLOW + "" + ChatColor.BOLD + "KYBLOCK");
                break;
            case 12:
                objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "S" + ChatColor.GOLD + "" + ChatColor.BOLD + "K" + ChatColor.YELLOW + "" + ChatColor.BOLD + "YBLOCK");
                break;
            case 13:
                objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "SK" + ChatColor.GOLD + "" + ChatColor.BOLD + "Y" + ChatColor.YELLOW + "" + ChatColor.BOLD + "BLOCK");
                break;
            case 14:
                objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "SKY" + ChatColor.GOLD + "" + ChatColor.BOLD + "B" + ChatColor.YELLOW + "" + ChatColor.BOLD + "LOCK");
                break;
            case 15:
                objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "SKYB" + ChatColor.GOLD + "" + ChatColor.BOLD + "L" + ChatColor.YELLOW + "" + ChatColor.BOLD + "OCK");
                break;
            case 16:
                objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "SKYBL" + ChatColor.GOLD + "" + ChatColor.BOLD + "O" + ChatColor.YELLOW + "" + ChatColor.BOLD + "CK");
                break;
            case 17:
                objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "SKYBLO" + ChatColor.GOLD + "" + ChatColor.BOLD + "C" + ChatColor.YELLOW + "" + ChatColor.BOLD + "K");
                break;
            case 18:
                objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "SKYBLOC" + ChatColor.GOLD + "" + ChatColor.BOLD + "K");
                break;
            case 19:
            case 20:
            case 23:
            case 24:
                objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "SKYBLOCK");
                break;
            default:
                tick = 0;
                break;
        }
    }

}
