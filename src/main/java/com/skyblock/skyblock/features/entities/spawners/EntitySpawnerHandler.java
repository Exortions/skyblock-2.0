package com.skyblock.skyblock.features.entities.spawners;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntitySpawnerHandler implements Listener {

    private static final String PATH = Skyblock.getPlugin().getDataFolder() + File.separator + "spawners.yml";

    private final List<EntitySpawner> spawners = new ArrayList<>();
    private final File file;


    public EntitySpawnerHandler() {
        file = new File(PATH);

        init();

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;

        for (EntitySpawner spawner : spawners) {
            updateHasPlayers(spawner);
        }
    }

    public void addSpawner(String type, String subType, SkyblockLocation location, Material material, int amount, int limit, long delay) {
        addSpawner(type, subType, location.getPosition1(), location.getPosition2(), material, amount, limit, delay);
    }

    public void addSpawner(String type, String subType, Location pos1, Location pos2, Material material, int amount, int limit, long delay) {
        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            String id = type + "_" + subType;

            if (!config.contains(id)) {
                config.createSection(id);
            }

            config.set(id + ".type", type);
            config.set(id + ".subType", subType);
            config.set(id + ".delay", delay);
            config.set(id + ".limit", limit);
            config.set(id + ".amount", amount);
            config.set(id + ".pos1", pos1);
            config.set(id + ".pos2", pos2);
            config.set(id + ".block", material.name());

            List<String> ids = config.getStringList("ids");
            if (!ids.contains(id)) ids.add(id);

            config.set("ids", ids);

            config.save(file);

            EntitySpawner spawner = loadSpawner(id);

            spawners.add(spawner);

            spawner.start();
        } catch (IOException e) { }
    }

    public EntitySpawner loadSpawner(String id) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        return new EntitySpawner(config.getConfigurationSection(id));
    }

    public void init() {
        if (!file.exists()) {
            try {
                file.createNewFile();

                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.set("ids", new ArrayList<>());
                config.save(file);
            } catch (IOException e) { }
        } else {
            List<String> ids = YamlConfiguration.loadConfiguration(file).getStringList("ids");

            for (String id : ids) {
                EntitySpawner spawner = loadSpawner(id);

                spawners.add(spawner);

                spawner.start();
            }
        }
    }

    private void updateHasPlayers(EntitySpawner spawner) {
        if (Bukkit.getOnlinePlayers().size() == 0) return;

        boolean players = false;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Util.inCuboid(p.getLocation(), spawner.getPos1(), spawner.getPos2())) {
                players = true;
                break;
            }
        }

        if (!players) {
            spawner.setHasPlayers(false);
            return;
        }

        if (!spawner.isHasPlayers()) {
            spawner.setHasPlayers(true);;
            spawner.spawn();
        }
    }
}
