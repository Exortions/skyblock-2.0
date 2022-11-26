package com.skyblock.skyblock.features.slayer.miniboss.tarantula;

import com.skyblock.skyblock.features.slayer.miniboss.SlayerMiniboss;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;

public class TarantulaVermin extends SlayerMiniboss {
    public TarantulaVermin(Player spawner) {
        super(EntityType.SPIDER, spawner);
        loadStats(54000, 260, false, true, true,
                new Equipment(), "Tarantula Vermin", 110, 150);
    }

    @Override
    protected void tick() {
        Spider spider = (Spider) getVanilla();
        spider.setTarget(spawner);
    }
}
