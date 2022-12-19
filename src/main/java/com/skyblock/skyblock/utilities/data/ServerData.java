package com.skyblock.skyblock.utilities.data;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ServerData {

    private final File serverDataFile;
    private final Skyblock skyblock;

    private static final HashMap<String, Object> defaultFields = new HashMap<>();

    private YamlConfiguration serverData;

    private final HashMap<String, Object> fields = new HashMap<>();

    static {
        defaultFields.put("date.season", "spring");
        defaultFields.put("date.day", 1);
        defaultFields.put("date.minutes", 0);
    }

    public ServerData(Skyblock skyblock) {
        this.skyblock = skyblock;

        this.serverDataFile = new File(skyblock.getDataFolder(), "global.yml");

        this.init();

        this.loadDataContentsIntoRam();
    }

    public Object get(String path) {
        return fields.get(path);
    }

    public void set(String path, Object value) {
        fields.put(path, value);
    }

    public void init() {
        this.serverData = YamlConfiguration.loadConfiguration(serverDataFile);

        if (serverDataFile.exists()) return;

        try {
            serverDataFile.createNewFile();

            for (String key : defaultFields.keySet()) {
                this.serverData.set(key, defaultFields.get(key));
            }

            this.serverData.save(serverDataFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void disable() {
        this.saveDataContentsToDisk();
    }

    public void saveDataContentsToDisk() {
        for (String key : fields.keySet()) {
            this.serverData.set(key, fields.get(key));
        }

        try {
            this.serverData.save(this.serverDataFile);
        } catch (IOException ex) {
            Skyblock.getPlugin().sendMessage("&cCould not save server data: &8" + ex.getMessage());
        }
    }

    public void loadDataContentsIntoRam() {
        for (String key : defaultFields.keySet()) {
            fields.put(key, this.serverData.get(key));
        }
    }

}
