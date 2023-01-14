package com.skyblock.skyblock.features.slayer;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.scoreboard.HubScoreboard;
import com.skyblock.skyblock.features.scoreboard.SlayerScoreboard;
import com.skyblock.skyblock.features.slayer.gui.SlayerGUI;
import com.skyblock.skyblock.features.slayer.miniboss.SlayerMiniboss;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.sound.SoundSequence;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class SlayerHandler {

    @Getter
    @AllArgsConstructor
    public static class SlayerData {

        private final SlayerQuest quest;
        private final SlayerBoss boss;

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

        SkyblockPlayer.getPlayer(player).addCoins(-1 * SlayerGUI.COINS.get(level - 1));
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

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        AtomicReference<Location> spawnLoc = new AtomicReference<>(player.getLocation());

        if (skyblockPlayer.getExtraData("lastKilledLoc") != null)
            spawnLoc.set((Location) skyblockPlayer.getExtraData("lastKilledLoc"));

        if (quest.getExp() >= quest.getNeededExp() && quest.getState().equals(SlayerQuest.QuestState.SUMMONING)) {
            quest.setState(SlayerQuest.QuestState.FIGHTING);

            SoundSequence.BOSS_SPAWN.play(player.getLocation());
            BukkitRunnable particles = new BukkitRunnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 50; i++) {
                        player.playEffect(spawnLoc.get(), Effect.SPELL, Effect.SPELL.getData());
                        player.playEffect(spawnLoc.get(), Effect.FLYING_GLYPH, Effect.FLYING_GLYPH.getData());
                        player.playEffect(spawnLoc.get(), Effect.WITCH_MAGIC, Effect.WITCH_MAGIC.getData());
                    }
                }
            };

            particles.runTaskTimer(Skyblock.getPlugin(), 0, 5);

            Util.delay(() -> {
                particles.cancel();
                player.playEffect(spawnLoc.get(), Effect.EXPLOSION_HUGE, Effect.EXPLOSION_HUGE.getData());
                quest.getBoss().spawn(spawnLoc.get());
            }, 28);
        } else if (quest.getState().equals(SlayerQuest.QuestState.SUMMONING)) {
            SlayerMiniboss miniboss = SlayerMiniboss.getMiniBoss(player, quest.getBoss().getLevel(), exp, quest.getType());
            if (miniboss != null) miniboss.spawn(spawnLoc.get());
        }
    }

    public SlayerData registerNewSlayer(Player player, SlayerType type, int level) {
        SlayerBoss boss = type.getNewInstance(player, level);

        if (boss == null) return null;

        SlayerQuest quest = new SlayerQuest(player, boss);
        SlayerData data = new SlayerData(quest, boss);

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
        if (!slayers.containsKey(player)) return new SlayerData(null, null);
        return slayers.get(player);
    }
}
