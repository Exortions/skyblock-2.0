package com.skyblock.skyblock.features.blocks.crops;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FloatingCrystalHandler {

    private static final String PATH = Skyblock.getPlugin().getDataFolder() + File.separator + "crystals.yml";
    private final File file;
    public FloatingCrystalHandler() {
        file = new File(PATH);

        init();
        loadCrystals();
    }

    public void saveCrystal(FloatingCrystal crystal) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String id = crystal.getId().toString();

        config.createSection(id);
        config.set(id + ".range",  crystal.getRange());
        config.set(id + ".durability",  crystal.getDurability());
        config.set(id + ".material",  crystal.getMaterial().toString());
        config.set(id + ".location",  crystal.getLocation());

        try {
            config.save(file);
        } catch (IOException e) {
            Skyblock.getPlugin().sendMessage("Failed to save crystal: " + crystal.getId());
        }
    }

    public void loadCrystals() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String id : config.getKeys(false)) {
            UUID uuid = UUID.fromString(id);
            int range = config.getInt(id + ".range");
            int durability = config.getInt(id + ".durability");
            Material material = Material.valueOf(config.getString(id + ".material"));
            Location location = (Location) config.get(id + ".location");

            new FloatingCrystal(uuid, material, (short) durability, location, range).spawn();
        }
    }

    private void init() {
        if (file.exists()) return;

        try {
            file.createNewFile();
        } catch (IOException e) { }
    }
}
