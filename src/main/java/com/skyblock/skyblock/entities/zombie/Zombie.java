package com.skyblock.skyblock.entities.zombie;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.entities.SkyblockEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Zombie extends SkyblockEntity {

    public Zombie(Skyblock sb) {
        super(sb, EntityType.ZOMBIE);

        loadStats(100, 20, true, false, true, new HashMap<>(), "Zombie", 1);
    }

    @Override
    protected void tick() {
        if (tick == 0) {
            org.bukkit.entity.Zombie zombie = (org.bukkit.entity.Zombie) getVanilla();
            zombie.setBaby(false);
            zombie.setVillager(false);
        }
    }
}
