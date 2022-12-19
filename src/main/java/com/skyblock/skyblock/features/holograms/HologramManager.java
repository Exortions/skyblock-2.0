package com.skyblock.skyblock.features.holograms;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class HologramManager {

    private final List<Hologram> holograms = new ArrayList<>();

    public HologramManager() {
        this.index();
    }

    public void index() {
        File file = new File(Skyblock.getPlugin().getDataFolder(), "holograms.json");

        if (!file.exists()) {
            Skyblock.getPlugin().sendMessage("&cCould not find holograms file &8" + file.getAbsolutePath() + "&c, not loading any holograms.");
            Bukkit.getPluginManager().disablePlugin(Skyblock.getPlugin());
            return;
        }

        try {
            JSONObject hologramsObject = (JSONObject) ((JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)))).get("holograms");

            for (Object key : hologramsObject.keySet()) {
                String location = (String) key;
                String text = (String) hologramsObject.get(key);

                this.holograms.add(new Hologram(location, text));
            }
        } catch (IOException | ParseException ex) {
            Skyblock.getPlugin().sendMessage("&cCould not load holograms file: &8" + ex.getMessage() + "&c!");
            Bukkit.getPluginManager().disablePlugin(Skyblock.getPlugin());

            return;
        }
    }

    public void spawnAll() {
        this.holograms.forEach(Hologram::spawn);
    }

    public void despawnAll() {
        this.holograms.forEach(Hologram::despawn);
    }

}
