package com.skyblock.skyblock.features.location;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkyblockLocationManager {

    public static final String LOCATION_FILE_NAME = "locations.yml";
    private static final List<SkyblockLocation> LOCATIONS_CACHE = new ArrayList<>();

    private final Skyblock skyblock;
    private final File file;

    public SkyblockLocationManager(Skyblock skyblock) {
        this.skyblock = skyblock;

        this.file = new File(skyblock.getDataFolder() + File.separator + LOCATION_FILE_NAME);

        this.init();
    }

    public SkyblockLocation getLocation(Location location) {
        SkyblockLocation temp = null;

        List<SkyblockLocation> found = new ArrayList<>();

        if (LOCATIONS_CACHE.isEmpty()) {
            for (String name : this.getLocations()) {
                Location pos1 = (Location) getField(name, "pos1");

                Location pos2 = (Location) getField(name, "pos2");

                temp = new SkyblockLocation(pos1, pos2, ChatColor.valueOf(((String) getField(name, "color")).toUpperCase()), (String) getField(name, "name"), (int) getField(name, "weight"));

                LOCATIONS_CACHE.add(temp);

                if (Util.inCuboid(location, temp.getPosition1(), temp.getPosition2()) && location.getWorld().equals(pos1.getWorld())) found.add(temp);
                else temp = null;
            }
        } else {
            for (SkyblockLocation skyblockLocation : LOCATIONS_CACHE) {
                if (Util.inCuboid(location, skyblockLocation.getPosition1(), skyblockLocation.getPosition2()) && location.getWorld().equals(skyblockLocation.getPosition1().getWorld())) found.add(skyblockLocation);
            }
        }

        if (found.size() < 1) return null;

        for (SkyblockLocation loc : found) {
            if (temp == null) temp = loc;
            else {
                if (loc.getWeight() > temp.getWeight()) temp = loc;
            }
        }

        if (temp.getName().equals("The Park")) {
            Biome biome = location.getBlock().getBiome();
            if (biome.equals(Biome.BIRCH_FOREST)) temp = new SkyblockLocation(temp.getPosition1(), temp.getPosition2(), ChatColor.GREEN, "Birch Park", temp.getWeight() + 1);
            if (biome.equals(Biome.ICE_PLAINS) || biome.equals(Biome.SWAMPLAND) || biome.equals(Biome.TAIGA)) temp = new SkyblockLocation(temp.getPosition1(), temp.getPosition2(), ChatColor.GREEN, "Spruce Woods", temp.getWeight() + 1);
            if (biome.equals(Biome.ROOFED_FOREST)) temp = new SkyblockLocation(temp.getPosition1(), temp.getPosition2(), ChatColor.GREEN, "Dark Thicket", temp.getWeight() + 1);
            if (biome.equals(Biome.SAVANNA)) temp = new SkyblockLocation(temp.getPosition1(), temp.getPosition2(), ChatColor.GREEN, "Savanna Woodland", temp.getWeight() + 1);
            if (biome.equals(Biome.JUNGLE)) temp = new SkyblockLocation(temp.getPosition1(), temp.getPosition2(), ChatColor.GREEN, "Jungle Island", temp.getWeight() + 1);
        }

        return temp;
    }

    public void init() {
        if (file.exists()) return;

        try {
            if (!file.createNewFile()) {
                throw new IOException("could not create " + file.getAbsolutePath() + ", File#createNewFile returns false");
            }

            YamlConfiguration.loadConfiguration(file).save(file);
        } catch (Exception ex) {
            this.skyblock.sendMessage("&cFailed to initialize &8locations.yml&c: " + ex.getMessage());
        }
    }

    public SkyblockLocation getLocation(String name) {
        Location pos1 = (Location) getField(name, "pos1");

        Location pos2 = (Location) getField(name, "pos2");

        return new SkyblockLocation(
                pos1,
                pos2,
                ChatColor.valueOf(((String) getField(name, "color")).toUpperCase()),
                (String) getField(name, "name"),
                (int) getField(name, "weight"));
    }

    public List<String> getLocations() {
        return new ArrayList<>(YamlConfiguration.loadConfiguration(file).getKeys(false));
    }

    public void createLocation(Location position1, Location position2, String name, ChatColor color, int weight) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set(name + ".pos1", position1);
        config.set(name + ".pos2", position2);
        config.set(name + ".name", name);
        config.set(name + ".color", color.name());
        config.set(name + ".weight", weight);

        try {
            config.save(file);
        } catch (Exception ex) {
            this.skyblock.sendMessage("&cFailed to save &7locations.yml&c: " + ex.getMessage());
        }

        LOCATIONS_CACHE.add(new SkyblockLocation(position1, position2, color, name, weight));
    }

    public Object getField(String name, String field) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.get(name + "." + field);
    }

}
