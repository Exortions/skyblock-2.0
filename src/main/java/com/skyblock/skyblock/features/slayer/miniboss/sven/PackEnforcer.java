package com.skyblock.skyblock.features.slayer.miniboss.sven;

import com.skyblock.skyblock.features.slayer.miniboss.SlayerMiniboss;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWolf;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class PackEnforcer extends SlayerMiniboss {

    public PackEnforcer(Player spawner) {
        super(EntityType.WOLF, spawner);
        loadStats(45000, 900, false, false, true,
                new Equipment(), "Pack Enforcer", 100, 150);
    }

    @Override
    protected void tick() {
        Wolf wolf = (Wolf) getVanilla();

        wolf.setTarget(spawner);
        wolf.setAngry(true);

        EntityWolf nms = ((CraftWolf) wolf).getHandle();

        nms.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.445);
    }
}
