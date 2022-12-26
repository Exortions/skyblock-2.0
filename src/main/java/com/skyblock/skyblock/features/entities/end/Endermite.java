package com.skyblock.skyblock.features.entities.end;

import com.skyblock.skyblock.features.entities.SkyblockEntity;
import org.bukkit.entity.EntityType;

public class Endermite extends SkyblockEntity {

    public Endermite(int level) {
        this(String.valueOf(level));
    }

    public Endermite(String l) {
        super(EntityType.ENDERMITE);

        int level = Integer.parseInt(l);
        int health = level == 37 ? 2000 : 2300;
        int damage = level == 37 ? 400 : 475;
        int xp = level == 37 ? 25 : 28;
        loadStats(health, damage, false, false, true, new Equipment(), "Endermite", level, xp, "endermite");
    }
}
