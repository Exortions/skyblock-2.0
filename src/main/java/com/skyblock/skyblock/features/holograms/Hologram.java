package com.skyblock.skyblock.features.holograms;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Hologram {

    private final Location location;
    private final List<String> lines;

    private final List<ArmorStand> stands = new ArrayList<>();

    public Hologram(String location, String lines) {
        this.location = new Location(Skyblock.getSkyblockWorld(), Integer.parseInt(location.split(",")[0]), Integer.parseInt(location.split(",")[1]), Integer.parseInt(location.split(",")[2]));
        this.lines = Arrays.asList(Util.buildLore(lines));
    }

    public void spawn() {
        Location loc = location.clone();

        for (String line : lines) {
            loc.add(0, 0.25, 0);

            ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
            stand.setGravity(false);
            stand.setCustomName(line);
            stand.setCustomNameVisible(true);
            stand.setVisible(false);
            stand.setMarker(true);

            stands.add(stand);
        }
    }

    public void despawn() {
        stands.forEach(ArmorStand::remove);
    }

}
