package com.skyblock.skyblock.features.holograms;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import lombok.Data;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Hologram {

    private final Location location;
    private final List<String> lines;

    private final List<ArmorStand> stands = new ArrayList<>();

    public Hologram(String location, String lines) {
        this.location = new Location(Skyblock.getSkyblockWorld(), Double.parseDouble(location.split(",")[0]), Double.parseDouble(location.split(",")[1]), Double.parseDouble(location.split(",")[2]));

        if (location.contains("world")) {
            this.location.setWorld(Bukkit.getWorld(location.split("world:")[1]));
        }

        this.lines = Arrays.asList(Util.buildLore(lines));
    }

    public void spawn() {
        Location loc = location.clone();
        loc.add(0.5, 2, 0.5);
        
        for (int i = 0; i < lines.size(); ++i) {
            String line = lines.get(i);
            if (i > 0) loc.add(0, -0.25, 0);

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARMOR_STAND, line, loc);
            npc.spawn(loc);

            ArmorStandTrait trait = npc.getOrAddTrait(ArmorStandTrait.class);
            trait.setGravity(false);
            trait.setVisible(false);
            trait.setMarker(true);
        }
    }

    public void despawn() {
        stands.forEach(ArmorStand::remove);
    }

}
