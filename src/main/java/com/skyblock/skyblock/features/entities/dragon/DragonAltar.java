package com.skyblock.skyblock.features.entities.dragon;

import com.skyblock.skyblock.Skyblock;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.Arrays;
import java.util.List;

@Getter
public class DragonAltar {

    private final List<Location> frames;
    public DragonAltar(Location... frames) {
        this.frames = Arrays.asList(frames);
    }

    public boolean isFilled(Block block) {
        return block.getState().getRawData() == 4;
    }

    public int getFilled() {
        int i = 0;
        for (Location l : frames) if (l.getWorld().getBlockAt(l).getState().getRawData() == 4) i++;

        return i;
    }

    public void onDisable() {
        reset();
    }

    public static DragonAltar getMainAltar() {
        World w = Skyblock.getSkyblockWorld();
        return new DragonAltar(new Location(w, -671.5, 9, -277.5), new Location(w, -669.5, 9, -277.5),
                new Location(w, -668.5, 9, -276.5), new Location(w, -668.5, 9, -274.5),
                new Location(w, -669.5, 9, -273.5), new Location(w, -671.5, 9, -273.5),
                new Location(w, -672.5, 9, -274.5), new Location(w, -672.5, 9, -276.5));
    }

    public void reset() {
        for (Location l : frames) {
            BlockState state = l.getWorld().getBlockAt(l).getState();
            state.setRawData((byte) 0);
            state.update();
        }
    }
}
