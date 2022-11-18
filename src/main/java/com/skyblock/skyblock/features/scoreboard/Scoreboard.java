package com.skyblock.skyblock.features.scoreboard;

import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public abstract class Scoreboard {

    private org.bukkit.scoreboard.Scoreboard board;
    protected Objective objective;

    protected final Player player;

    public Scoreboard(Player player) {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("SkyblockBoard", "Display ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Skyblock");

        this.objective = objective;
        this.player = player;

        display();
        updateScoreboard();
    }

    abstract void display();

    public void updateScoreboard(){
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = board.registerNewObjective("Skyblock", "Display ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "SKYBLOCK");

        this.objective = objective;

        display();
        player.setScoreboard(board);
    }

    public void addLine(int line, String text){
        Score score = objective.getScore(colorize(text));
        score.setScore(line);
    }

    private String colorize(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
