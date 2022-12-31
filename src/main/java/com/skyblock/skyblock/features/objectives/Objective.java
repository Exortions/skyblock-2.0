package com.skyblock.skyblock.features.objectives;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

@Getter
public class Objective implements Listener {

    private final String id;
    private final String display;

    public Objective(String id, String display) {
        this.id = id;
        this.display = display;

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin());
    }

    private QuestLine getQuest() {
        return Skyblock.getPlugin().getQuestLineHandler().getQuest(this);
    }

    public Objective getNext() {
        return getQuest().getNext(this);
    }

    public void complete(Player p) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(p);
        Objective next = getNext();

        List<String> completed = (List<String>) player.getValue("quests.completedObjectives");
        completed.add(this.getId());
        player.setValue("quests.completedObjectives", completed);

        if (next == null) {
            getQuest().complete(player.getBukkitPlayer());
            return;
        }

        String message = " \n " + ChatColor.GOLD + ChatColor.BOLD + " NEW OBJECTIVE" + "\n" +
                ChatColor.WHITE + "  " + next.getDisplay() + "\n";

        player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 10, 0);
        player.getBukkitPlayer().sendMessage(message);
        player.getBukkitPlayer().sendMessage(" ");
    }

    public String getSuffix(SkyblockPlayer player) {
        return "";
    }

    protected boolean isThisObjective(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.getQuestLine() == null) return false;
        if (skyblockPlayer.getQuestLine().getObjective(skyblockPlayer) == null) return false;

        return skyblockPlayer.getQuestLine().getObjective(skyblockPlayer).getId().equals(getId());
    }

    public boolean hasSuffix(SkyblockPlayer skyblockPlayer) {
        return !getSuffix(skyblockPlayer).equals("");
    }
}
