package com.skyblock.skyblock.features.slayer.boss;

import com.skyblock.skyblock.features.slayer.SlayerBoss;
import com.skyblock.skyblock.features.slayer.SlayerType;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@Getter
public class TarantulaBroodfather extends SlayerBoss {

    public TarantulaBroodfather(Player spawner, Integer level) {
        super(EntityType.SPIDER, SlayerType.TARANTULA, spawner, level, -0.75);

        switch (level) {
            case 1:
                loadStats(750, 35, false, true, true, new Equipment(), "Tarantula Broodfather", 40, 50);
                break;
            case 2:
                loadStats(30000, 45, false, true, true, new Equipment(), "Tarantula Broodfather", 90, 100);
                break;
            case 3:
                loadStats(900000, 155, false, true, true, new Equipment(), "Tarantula Broodfather", 180, 200);
                break;
            case 4:
                loadStats(2400000, 400, false, true, true, new Equipment(), "Tarantula Broodfather", 260, 500);
                break;
        }
    }
}
