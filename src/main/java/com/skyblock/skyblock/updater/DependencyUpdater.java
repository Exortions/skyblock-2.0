package com.skyblock.skyblock.updater;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyUpdater {

    public static final String FILE_PREFIX = "https://raw.githubusercontent.com/Exortions/skyblock-2.0/master/dependencies/skyblock/";
    public static final String CACHED_CHANGES_FILE = ".cache.yml";

    public static final List<String> updateableFiles = new ArrayList<String>() {{
        add("bazaar" + File.separator + "items.json");
        add("collections" + File.separator + "combat.json");
        add("collections" + File.separator + "farming.json");
        add("collections" + File.separator + "mining.json");
        add("collections" + File.separator + "foraging.json");
        add("collections" + File.separator + "fishing.json");
        add("fairy_souls.json");
        add("holograms.json");
        add("prices.json");
        add("crystals.yml");
        add("launchpads.yml");
        add("locations.yml");
        add("merchants.json");
        add("reforges.json");
        add("spawners.yml");
        add("trades.json");
        add("enchants.json");
        add("harpSongs.json");
    }};

    private final Skyblock skyblock;

    public DependencyUpdater(Skyblock skyblock) {
        this.skyblock = skyblock;
    }

    public void update() {
        this.skyblock.sendMessage("Checking for dependency updates...");

        File cachedChangesFile = new File(skyblock.getDataFolder(), CACHED_CHANGES_FILE);

        if (!cachedChangesFile.exists()) {
            this.skyblock.sendMessage("No update cache found, creating now...");

            try {
                cachedChangesFile.createNewFile();

                FileConfiguration cache = YamlConfiguration.loadConfiguration(cachedChangesFile);

                cache.set("updated", new ArrayList<String>());
                cache.save(cachedChangesFile);
            } catch (IOException ex) {
                this.skyblock.sendMessage("&cFailed to create caches file &8" + cachedChangesFile.getName() + "&c: &8" + ex.getMessage());
                return;
            }
        }

        FileConfiguration cache = YamlConfiguration.loadConfiguration(cachedChangesFile);

        if (cache.getStringList("updated").isEmpty()) {
            this.skyblock.sendMessage("No cached changes found, pulling all updates...");
            this.pullAllChanges();

            return;
        }

        String[] recentlyUpdatedFiles = Arrays.stream(cache.getStringList("updated").toArray(new String[0])).map(s -> s.replace("/", File.separator)).toArray(String[]::new);

        this.skyblock.sendMessage("Found &8" + recentlyUpdatedFiles.length + " &fupdated dependencies, pulling non-updated files...");

        this.pullChanges(recentlyUpdatedFiles);
    }

    public void pullAllChanges() {
        this.pullChanges();
    }

    public void pullChanges(String... exclusions) {
        List<String> updateable = DependencyUpdater.updateableFiles.stream().filter(file -> {
            for (String exclusion : exclusions) {
                if (file.equals(exclusion)) {
                    return false;
                }
            }

            return true;
        }).collect(Collectors.toList());

        List<String> updated = new ArrayList<>();

        for (String file : updateable) {
            this.pullFile(file);

            updated.add(file);
        }

        this.skyblock.sendMessage("Finished pulling &8" + updated.size() + " &fupdated dependencies!");

        File cachedChangesFile = new File(skyblock.getDataFolder(), CACHED_CHANGES_FILE);
        FileConfiguration cache = YamlConfiguration.loadConfiguration(cachedChangesFile);
        cache.set("updated", new ArrayList<String>() {{
            addAll(Arrays.asList(exclusions));
        }});
    }

    private void pullFile(String file) {
        this.skyblock.sendMessage("Pulling &8" + file + "&f...");

        try {
            URL url = new URL(FILE_PREFIX + file.replace(File.separator, "/"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();

            if (status != 200) {
                this.skyblock.sendMessage("&cFailed to pull &8" + file + "&c: &8" + status);
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine + "\n");
            }

            in.close();

            conn.disconnect();

            File fileToWrite = new File(skyblock.getDataFolder(), file);
            fileToWrite.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(fileToWrite);
            writer.write(content.toString());
            writer.close();
        } catch (IOException ex) {
            this.skyblock.sendMessage("&cFailed to pull &8" + file + "&c: &8" + ex.getMessage());
            return;
        }

        this.skyblock.sendMessage("Successfully pulled &8" + file + "&f!");
    }

}
