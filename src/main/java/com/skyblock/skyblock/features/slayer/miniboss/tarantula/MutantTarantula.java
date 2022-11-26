package com.skyblock.skyblock.features.slayer.miniboss.tarantula;

import com.skyblock.skyblock.features.slayer.miniboss.SlayerMiniboss;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;

public class MutantTarantula extends SlayerMiniboss {
    public MutantTarantula(Player spawner) {
        super(EntityType.SPIDER, spawner);
        loadStats(576000, 2000, false, true, true,
                new Equipment(), "Mutant Tarantula", 370, 500);
    }

    @Override
    protected void tick() {
        Spider spider = (Spider) getVanilla();
        spider.setTarget(spawner);
    }
}
