package com.skyblock.skyblock;

import com.skyblock.skyblock.enums.SkyblockStat;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

@Data
public class SkyblockPlayer {

    public static HashMap<UUID, SkyblockPlayer> playerRegistry = new HashMap<>();

    public static SkyblockPlayer getPlayer(Player player) {
        return playerRegistry.get(player.getUniqueId());
    }

    public static void registerPlayer(UUID uuid) {
        playerRegistry.put(uuid, new SkyblockPlayer(uuid));
    }

    private Player bukkitPlayer;
    private HashMap<SkyblockStat, Integer> stats;
    private FileConfiguration config;
    private File configFile;

    public SkyblockPlayer(UUID uuid) {
        bukkitPlayer = Bukkit.getPlayer(uuid);
        stats = new HashMap<>();

        initConfig();
    }

    public int getStat(SkyblockStat stat) { return stats.get(stat); }

    public void setStat(SkyblockStat stat, int val) {
        stats.put(stat, val);

        setValue(stat.name(), val);
    }

    public void addStat(SkyblockStat stat, int val) {
        setStat(stat, getStat(stat) + val);
    }

    public void subtractStat(SkyblockStat stat, int val) {
        setStat(stat, getStat(stat) - val);
    }

    public Object getValue(String path){
        return config.get(path);
    }

    public void setValue(String path, Object item){
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
        if (!folder.exists()) {
            folder.mkdirs();
        }
        configFile = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + "Players" + File.separator + getBukkitPlayer().getUniqueId() + ".yml");
        if (!configFile.exists()) {
            try{
                configFile.createNewFile();
                config = YamlConfiguration.loadConfiguration(configFile);

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

                config.save(configFile);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
