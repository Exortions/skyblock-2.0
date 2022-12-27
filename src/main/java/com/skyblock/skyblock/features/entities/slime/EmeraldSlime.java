package com.skyblock.skyblock.features.entities.slime;

import com.skyblock.skyblock.features.entities.SkyblockEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

public class EmeraldSlime extends SkyblockEntity {

    private enum SlimeLevel {
        LARGE_SLIME,
        MEDIUM_SLIME,
        SMALL_SLIME
    }

    private final SlimeLevel level;

    public EmeraldSlime(String type) {
        super(EntityType.SLIME);

        this.level = SlimeLevel.valueOf(type);

        switch (this.level) {
            case LARGE_SLIME:
                loadStats(250, 150, false, false, true, new Equipment(), "Slime", 15, 20, "slime");
                break;
            case MEDIUM_SLIME:
                loadStats(150, 100, false, false, true, new Equipment(), "Slime", 10, 15, "slime");
                break;
            case SMALL_SLIME:
                loadStats(80, 70, false, false, true, new Equipment(), "Slime", 5, 12, "small_slime");
                break;
        }
    }

    @Override
    protected void tick() {
        if (tick != 0) return;

        Slime slime = (Slime) getVanilla();

        switch (level) {
            case LARGE_SLIME:
                slime.setSize(10);
                break;
            case MEDIUM_SLIME:
                slime.setSize(5);
                break;
            case SMALL_SLIME:
                slime.setSize(2);
                break;
        }
    }
}
