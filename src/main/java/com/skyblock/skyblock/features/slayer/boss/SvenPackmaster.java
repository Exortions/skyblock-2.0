package com.skyblock.skyblock.features.slayer.boss;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.slayer.SlayerBoss;
import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.features.slayer.miniboss.sven.SvenPup;
import com.skyblock.skyblock.utilities.Util;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWolf;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.List;

public class SvenPackmaster extends SlayerBoss {

    private static final List<Integer> TRUE_DPS = Arrays.asList(0, 10, 50, 200);

    private final Player spawner;
    private final int trueDPS;

    private boolean hasCalledPups;

    public SvenPackmaster(Player spawner, Integer level) {
        super(EntityType.WOLF, SlayerType.SVEN, spawner, level, -1);

        this.spawner = spawner;
        this.hasCalledPups = false;

        switch (level){
            case 1:
                loadStats(2000, 60, false, false, true, new Equipment(), "Sven Packmaster", 20, 50);
                break;
            case 2:
                loadStats(40000, 200, false, false, true, new Equipment(), "Sven Packmaster", 100, 100);
                break;
            case 3:
                loadStats(750000, 450, false, false, true, new Equipment(), "Sven Packmaster", 430, 200);
                break;
            case 4:
                loadStats(2000000, 1100, false, false, true, new Equipment(), "Sven Packmaster", 700, 500);
                break;
        }

        this.trueDPS = TRUE_DPS.get(level - 1);
    }

    @Override
    protected void tick() {
        super.tick();

        Wolf wolf = (Wolf) getVanilla();
        wolf.setTarget(spawner);
        wolf.setAngry(true);

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(spawner);

        EntityWolf nms = ((CraftWolf) wolf).getHandle();

        nms.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.445);

        if (tick % 20 == 0 && getLevel() >= 2) skyblockPlayer.damage(trueDPS, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getVanilla(), true);

        if (getEntityData().maximumHealth / 2 >= getEntityData().health && !hasCalledPups) {
            hasCalledPups = true;

            for (int i = 0; i < 5; i++) {
                Util.delay(() -> new SvenPup(this).spawn(getVanilla().getLocation()), i * 20);
            }
        }
    }
}
