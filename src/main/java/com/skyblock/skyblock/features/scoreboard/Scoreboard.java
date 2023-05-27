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

    private int animation = 0;

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

    public void updateTitle() {
        animateTitle();

        animation++;
    }

    private String colorize(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void animateTitle() {
        switch (animation) {
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
                animation = 0;
                break;
        }
    }

}
