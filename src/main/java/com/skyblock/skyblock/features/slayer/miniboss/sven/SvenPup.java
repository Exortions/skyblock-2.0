package com.skyblock.skyblock.features.slayer.miniboss.sven;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.slayer.boss.SvenPackmaster;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWolf;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;

public class SvenPup extends SkyblockEntity {

    private SvenPackmaster owner;

    public SvenPup(SvenPackmaster owner) {
        super(EntityType.WOLF);

        this.owner = owner;

        loadStats((int) owner.getEntityData().maximumHealth / 10, 90, false, false, true, new Equipment(), "Sven Pup", 100, 0);
    }

    @Override
    protected void tick() {
        if (owner.getVanilla().isDead()) getEntityData().health = -1;

        setLifeSpan(owner.getLifeSpan());

        Wolf wolf = (Wolf) getVanilla();
        wolf.setBaby();

        wolf.setTarget(owner.getSpawner());
        wolf.setAngry(true);

        EntityWolf nms = ((CraftWolf) wolf).getHandle();

        nms.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.445);
    }
}
