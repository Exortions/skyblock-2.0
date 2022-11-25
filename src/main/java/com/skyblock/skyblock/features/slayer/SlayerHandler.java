package com.skyblock.skyblock.features.slayer;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.scoreboard.HubScoreboard;
import com.skyblock.skyblock.features.scoreboard.SlayerScoreboard;
import com.skyblock.skyblock.utilities.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SlayerHandler {

    @Getter
    @AllArgsConstructor
    public class SlayerData {

        private SlayerBoss boss;
        private SlayerQuest quest;

    }

    private final HashMap<Player, SlayerData> slayers = new HashMap<>();

    public void startQuest(Player player, SlayerType type, int level) {
        SlayerData data = registerNewSlayer(player, type, level);
        player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SLAYER QUEST STARTED");
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 2);

        String entityName = "";

        switch (type) {
            case REVENANT:
                entityName = "Zombies";
                break;
            case TARANTULA:
                entityName = "Spiders";
                break;
            case SVEN:
                entityName = "Wolves";
                break;
        }

        player.sendMessage(ChatColor.DARK_PURPLE + " ☠ " + ChatColor.GRAY + "Slay " + ChatColor.RED + Util.formatInt(data.quest.getNeededExp()) + " Combat XP " + ChatColor.GRAY + "worth of " + entityName + ".");
    }

    public void endQuest(Player player, boolean fail) {
        SlayerData data = getSlayer(player);
        if (data == null) return;

        SlayerBoss boss = data.getBoss();

        if (fail) {
            player.sendMessage("  " + ChatColor.RED + ChatColor.BOLD + "SLAYER QUEST FAILED!");
            player.sendMessage("   " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "→ " + ChatColor.GRAY + "You died! What a noob!");

            Skyblock.getPlugin(Skyblock.class).getEntityHandler().unregisterEntity(boss.getVanilla().getEntityId());
            boss.getVanilla().remove();
        }

        unregisterSlayer(player);
    }

    public void addExp(Player player, double exp) {
        SlayerData data = getSlayer(player);
        SlayerQuest quest = data.quest;

        quest.addExp((int) exp);

        if (quest.getExp() >= quest.getNeededExp() && quest.getState().equals(SlayerQuest.QuestState.SUMMONING)) {
            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
            Location spawnLoc = player.getLocation();

            if (skyblockPlayer.getExtraData("lastKilledLoc") != null)
                spawnLoc = (Location) skyblockPlayer.getExtraData("lastKilledLoc");

            quest.setState(SlayerQuest.QuestState.FIGHTING);

            Location finalSpawnLoc = spawnLoc;
            Util.delay(() -> {
                quest.getBoss().spawn(finalSpawnLoc);
                player.playEffect(finalSpawnLoc, Effect.EXPLOSION_HUGE, 20);
                player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 10, 2);
            }, 40);
        }
    }

    public SlayerData registerNewSlayer(Player player, SlayerType type, int level) {
        SlayerBoss boss = type.getNewInstance(player, level);
        SlayerQuest quest = new SlayerQuest(player, boss);
        SlayerData data = new SlayerData(boss, quest);

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        skyblockPlayer.setExtraData("slayerData", data);
        skyblockPlayer.setBoard(new SlayerScoreboard(player));

        slayers.put(player, data);

        return getSlayer(player);
    }

    public void unregisterSlayer(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        skyblockPlayer.setBoard(new HubScoreboard(player));

        slayers.remove(player);
    }

    public SlayerData getSlayer(Player player) {
        return slayers.get(player);
    }
}
