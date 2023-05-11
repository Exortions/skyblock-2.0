package com.skyblock.skyblock.features.entities.spawners;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntitySpawnerHandler {

    private static final String PATH = Skyblock.getPlugin().getDataFolder() + File.separator + "spawners.yml";
    private final File file;
    private final FileConfiguration config;

    public EntitySpawnerHandler() {
        file = new File(PATH);
        config = YamlConfiguration.loadConfiguration(file);
        init();
    }

    public void addSpawner(String type, String subType, SkyblockLocation location, Material material, int amount, int limit, long delay) {
        addSpawner(type, subType, location.getPosition1(), location.getPosition2(), material, amount, limit, delay);
    }

    public void addSpawner(String type, String subType, Location pos1, Location pos2, Material material, int amount, int limit, long delay) {
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
        if (!ids.contains(id)) {
            ids.add(id);
        }

        config.set("ids", ids);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadSpawner(id).start();
    }

    public EntitySpawner loadSpawner(String id) {
        return new EntitySpawner(config.getConfigurationSection(id));
    }

    public void init() {
        if (!file.exists()) {
            try {
                file.createNewFile();
                config.set("ids", new ArrayList<>());
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            List<String> ids = config.getStringList("ids");
            for (String id : ids) {
                loadSpawner(id).start();
            }
        }
    }
}