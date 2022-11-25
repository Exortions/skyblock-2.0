package com.skyblock.skyblock.features.entities.enderman;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Enderman extends SkyblockEntity {

    public Enderman(Skyblock sb, String type) {
        super(sb, EntityType.ENDERMAN);

        Equipment equipment = new Equipment();

        switch (EndermanType.valueOf(type)){
            case FOUR_K:
                loadStats(4500, 500, false, false, true, equipment, "Enderman", 42, 40);
                break;
            case SIX_K:
                loadStats(6000, 600, false, false, true, equipment, "Enderman", 45, 42);
                break;
            case NINE_K:
                loadStats(9000, 700, false, false, true, equipment, "Enderman", 50, 44);
                break;
            case ZEALOT:
                loadStats(13000, 1250, false, false, true, equipment, "Zealot", 55, 45);
                break;
            case SPECIAL_ZEALOT:
                equipment.hand = new ItemStack(Material.ENDER_PORTAL_FRAME);

                loadStats(2000, 1250, false, false, true, equipment, "Zealot", 55, 40);
                break;
            case ZEALOT_ENDERCHEST:
                equipment.hand = new ItemStack(Material.ENDER_CHEST);

                loadStats(13000, 1250, false, false, true, equipment, "Zealot", 55, 45);
                break;
        }
    }

    @Override
    protected void tick() {

    }
}
