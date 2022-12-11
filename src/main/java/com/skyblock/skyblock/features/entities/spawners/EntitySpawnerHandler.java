package com.skyblock.skyblock.features.entities.spawners;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Location;
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


    public EntitySpawnerHandler() {
        file = new File(PATH);

        init();
    }

    public void addSpawner(String type, String subType, Location location, int amount, int limit, long delay, int range) {
        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (!config.contains(subType)) {
                config.createSection(subType);
                config.set(subType + ".locations", new ArrayList<>());
            }

            config.set(subType + ".type", type);
            config.set(subType + ".subType", subType);
            config.set(subType + ".delay", delay);
            config.set(subType + ".limit", limit);
            config.set(subType + ".amount", amount);
            config.set(subType + ".range", range);

            List<Location> list = (List<Location>) config.getList(subType + ".locations");
            list.add(location);

            config.set(subType + ".locations", list);

            List<String> ids = config.getStringList("ids");
            if (!ids.contains(subType)) ids.add(subType);

            config.set(subType + ".locations", list);
            config.set("ids", ids);

            config.save(file);

            loadSpawner(subType).start();
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
                loadSpawner(id).start();
            }
        }
    }
}
