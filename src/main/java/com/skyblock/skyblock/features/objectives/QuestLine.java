package com.skyblock.skyblock.features.objectives;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class QuestLine {

    protected final List<Objective> line;
    private final String name;
    private final String display;

    public QuestLine(String name, String display, Objective... objectives) {
        this.line = Arrays.asList(objectives);
        this.display = display;
        this.name = name;
    }

    public Objective getObjective(SkyblockPlayer skyblockPlayer) {
        List<String> completed = (ArrayList<String>) skyblockPlayer.getValue("quests.completedObjectives");

        for (Objective obj : line) {
            if (completed.contains(obj.getId())) continue;

            return obj;
        }

        return new Objective("", "");
    }

    public Objective getNext(Objective obj) {
        try {
            return line.get(line.indexOf(obj) + 1);
        } catch (ArrayIndexOutOfBoundsException igored) { }

        return null;
    }

    public void complete(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        List<String> completedQuests = (List<String>) skyblockPlayer.getValue("quests.completedQuests");
        completedQuests.add(getName());
        skyblockPlayer.setValue("quests.completedQuests", completedQuests);

        if (!hasCompletionMessage()) return;

        String message = " \n " + ChatColor.GOLD + ChatColor.BOLD + " QUEST COMPLETE" + "\n" +
                ChatColor.WHITE + "  " + display + "\n";

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 0);
        player.sendMessage(message);
        player.sendMessage(" ");

        for (Objective objective : line) {
            player.sendMessage(ChatColor.GREEN + "   ✓ " + ChatColor.WHITE + objective.getDisplay());
        }

        if (getRewards().size() > 0) {
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GREEN + "  " + ChatColor.BOLD + "REWARD");

            for (String reward : getRewards()) {
                player.sendMessage("   " + ChatColor.translateAlternateColorCodes('&', reward));
            }
        }

        player.sendMessage("  ");

        reward(SkyblockPlayer.getPlayer(player));
    }

    protected List<String> getRewards() {
        return Collections.emptyList();
    }

    protected void reward(SkyblockPlayer player) { }

    protected boolean hasCompletionMessage() {
        return false;
    }

    public void onDisable() {}
    public void onEnable() {}
}
