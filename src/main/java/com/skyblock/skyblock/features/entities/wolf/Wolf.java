package com.skyblock.skyblock.features.entities.wolf;

import com.skyblock.skyblock.features.entities.SkyblockEntity;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWolf;
import org.bukkit.entity.EntityType;

public class Wolf extends SkyblockEntity {

    private final WolfType type;

    public Wolf(String type) {
        super(EntityType.WOLF);

        this.type = WolfType.valueOf(type);

        switch (this.type){
            case RUINS:
                loadStats(250, 90, false, false, true, new Equipment(), "Wolf", 15, 15, "wolf");
                break;
            case OLD_WOLF:
                loadStats(15000, 800, false, false, true, new Equipment(), "Old Wolf", 55, 45, "old_wolf");
                break;
            case PACK_SPIRIT:
                loadStats(6000, 240, false, false, true, new Equipment(), "Pack Spirit", 30, 12, "pack_spirit");
                break;
            case HOWLING_SPIRIT:
                loadStats(7000, 400, false, false, true, new Equipment(), "Howling Spirit", 35, 22, "howling_spirit");
                break;
            case SOUL_OF_THE_ALPHA:
                loadStats(31150, 1140, false, false, true, new Equipment(), "Soul of the Alpha", 55, 50, "soul_of_the_alpha");
                break;
            default:
                break;
        }
    }

    @Override
    protected void tick() {
        org.bukkit.entity.Wolf wolf = (org.bukkit.entity.Wolf) getVanilla();

        EntityWolf nms = (((CraftWolf) wolf)).getHandle();

        nms.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4);

        wolf.setAngry(wolf.getTarget() != null);
    }
}
