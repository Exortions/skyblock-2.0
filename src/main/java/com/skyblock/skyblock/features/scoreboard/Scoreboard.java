package com.skyblock.skyblock.features.scoreboard;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public abstract class Scoreboard {

    private org.bukkit.scoreboard.Scoreboard board;
    protected Objective objective;

    protected final Player player;

    public Scoreboard(Player player) {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("SkyblockBoard", "Display ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Skyblock");

        Objective health = board.registerNewObjective("health", "dummy");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
        health.setDisplayName(ChatColor.RED + "❤");

        for (Player p : Bukkit.getOnlinePlayers()) {
            Score score = health.getScore(p);
            if (SkyblockPlayer.getPlayer(p) != null) score.setScore(SkyblockPlayer.getPlayer(p).getStat(SkyblockStat.HEALTH));
        }

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

        Objective health = board.registerNewObjective("health", "dummy");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
        health.setDisplayName(ChatColor.RED + "❤");

        for (Player p : Bukkit.getOnlinePlayers()) {
            Score score = health.getScore(p);
            if (SkyblockPlayer.getPlayer(p) != null) score.setScore(SkyblockPlayer.getPlayer(p).getStat(SkyblockStat.HEALTH));
        }

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
