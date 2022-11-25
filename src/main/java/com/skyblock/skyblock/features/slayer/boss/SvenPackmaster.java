package com.skyblock.skyblock.features.slayer.boss;

import com.skyblock.skyblock.features.slayer.SlayerBoss;
import com.skyblock.skyblock.features.slayer.SlayerType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SvenPackmaster extends SlayerBoss {

    private int trueDPS;
    private boolean hasCalledPups;

    private static final List<Integer> TRUE_DPS = Arrays.asList(0, 10, 50, 200);

    public SvenPackmaster(Player spawner, Integer level) {
        super(EntityType.WOLF, SlayerType.SVEN, spawner, level, -1);

        switch (level){
            case 1:
                loadStats(2000, 60, false, false, true, new Equipment(), "Sven Packmaster", 20, 50);
                break;
            case 2:
                loadStats(40000, 75, false, false, true, new Equipment(), "Sven Packmaster", 100, 100);
                break;
            case 3:
                loadStats(750000, 135, false, false, true, new Equipment(), "Sven Packmaster", 430, 200);
                break;
            case 4:
                loadStats(2000000, 330, false, false, true, new Equipment(), "Sven Packmaster", 700, 500);
                break;
        }

        this.trueDPS = TRUE_DPS.get(level - 1);
    }
}
