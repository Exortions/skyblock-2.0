package com.skyblock.skyblock.features.slayer.miniboss.tarantula;

import com.skyblock.skyblock.features.slayer.miniboss.SlayerMiniboss;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;

public class TarantulaBeast extends SlayerMiniboss {
    public TarantulaBeast(Player spawner) {
        super(EntityType.SPIDER, spawner);
        loadStats(144000, 1000, false, true, true,
                new Equipment(), "Tarantula Beast", 180, 250);
    }

    @Override
    protected void tick() {
        Spider spider = (Spider) getVanilla();
        spider.setTarget(spawner);
    }
}
