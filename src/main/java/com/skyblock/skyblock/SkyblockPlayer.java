package com.skyblock.skyblock;

import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.scoreboard.HubScoreboard;
import com.skyblock.skyblock.features.scoreboard.Scoreboard;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

@Data
public class SkyblockPlayer {

    private static int EVERY_SECOND = 20;
    public static HashMap<UUID, SkyblockPlayer> playerRegistry = new HashMap<>();

    public static SkyblockPlayer getPlayer(Player player) {
        return playerRegistry.get(player.getUniqueId());
    }

    public static void registerPlayer(UUID uuid) {
        playerRegistry.put(uuid, new SkyblockPlayer(uuid));
    }

    private Player bukkitPlayer;
    private HashMap<SkyblockStat, Integer> stats;
    private HashMap<String, Boolean> cooldowns;
    private FileConfiguration config;
    private File configFile;
    private Scoreboard board;
    private int tick;

    public SkyblockPlayer(UUID uuid) {
        bukkitPlayer = Bukkit.getPlayer(uuid);
        cooldowns = new HashMap<>();
        stats = new HashMap<>();
        tick = 0;

        initConfig();
    }

    public void tick() {
        if (board == null) board = new HubScoreboard(getBukkitPlayer());

        if (tick % EVERY_SECOND == 0) {
            board.updateScoreboard();
        }

        tick++;
    }

    public int getStat(SkyblockStat stat) { return stats.get(stat); }

    public void setStat(SkyblockStat stat, int val) {
        stats.put(stat, val);

        setValue(stat.name(), val);
    }

    public boolean getCooldown(String id) {
        if (!cooldowns.containsKey(id)) {
            cooldowns.put(id, true);
        }

        return cooldowns.get(id);
    }

    public void setCooldown(String id, int secondsDelay) {
        cooldowns.put(id, false);

        new BukkitRunnable() {
            @Override
            public void run() {
                cooldowns.put(id, true);
            }
        }.runTaskLater(Skyblock.getPlugin(Skyblock.class), 40);
    }

    public void addStat(SkyblockStat stat, int val) {
        setStat(stat, getStat(stat) + val);
    }

    public void subtractStat(SkyblockStat stat, int val) {
        setStat(stat, getStat(stat) - val);
    }

    public Object getValue(String path) {
        return config.get(path);
    }

    public void setValue(String path, Object item) {
        try {
            config.set(path, item);
            config.save(configFile);
            config = YamlConfiguration.loadConfiguration(configFile);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void forEachStat(Consumer<SkyblockStat> cons) {
        for (SkyblockStat stat : SkyblockStat.values()) {
            cons.accept(stat);
        }
    }

    private void initConfig() {
        File folder = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + "Players");
        if (!folder.exists())  folder.mkdirs();
        configFile = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + "Players" + File.separator + getBukkitPlayer().getUniqueId() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                forEachStat((s) -> {
                    config.set("stats." + s.name().toLowerCase(), 0);
                });

                config.set("stats." + SkyblockStat.MAX_HEALTH.name().toLowerCase(), 100);
                config.set("stats." + SkyblockStat.HEALTH.name().toLowerCase(), 100);
                config.set("stats." + SkyblockStat.MAX_MANA.name().toLowerCase(), 100);
                config.set("stats." + SkyblockStat.MANA.name().toLowerCase(), 100);
                config.set("stats." + SkyblockStat.SPEED.name().toLowerCase(), 100);
                config.set("stats." + SkyblockStat.CRIT_CHANCE.name().toLowerCase(), 30);
                config.set("stats." + SkyblockStat.CRIT_DAMAGE.name().toLowerCase(), 50);

                config.set("stats.purse", 0);

                for (Collection collection : Collection.getCollections()) {
                    config.set("collection." + collection.getName().toLowerCase() + ".level", 0);
                    config.set("collection." + collection.getName().toLowerCase() + ".exp", 0);
                    config.set("collection." + collection.getName().toLowerCase() + ".unlocked", false);
                }

                config.save(configFile);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
