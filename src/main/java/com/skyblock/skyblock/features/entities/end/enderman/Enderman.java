package com.skyblock.skyblock.features.entities.end.enderman;

import com.skyblock.skyblock.features.entities.EntityDrop;
import com.skyblock.skyblock.features.entities.EntityDropRarity;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Enderman extends SkyblockEntity {

    private EndermanType type;
    public Enderman(String type) {
        super(EntityType.ENDERMAN);

        Equipment equipment = new Equipment();

        this.type = EndermanType.valueOf(type);

        switch (EndermanType.valueOf(type)){
            case FOUR_K:
                loadStats(4500, 500, false, false, true, equipment, "Enderman", 42, 40, "weak_enderman");
                break;
            case SIX_K:
                loadStats(6000, 600, false, false, true, equipment, "Enderman", 45, 42, "weak_enderman");
                break;
            case NINE_K:
                loadStats(9000, 700, false, false, true, equipment, "Enderman", 50, 44, "strong_enderman");
                break;
            case ZEALOT:
                loadStats(13000, 1250, false, false, true, equipment, "Zealot", 55, 45, "zealot");
                break;
            case SPECIAL_ZEALOT:
                equipment.hand = new ItemStack(Material.ENDER_PORTAL_FRAME);

                loadStats(2000, 1250, false, false, true, equipment, "Zealot", 55, 40, "zealot");
                break;
            case ZEALOT_ENDERCHEST:
                equipment.hand = new ItemStack(Material.ENDER_CHEST);

                loadStats(13000, 1250, false, false, true, equipment, "Zealot", 55, 45, "zealot");
                break;
            default:
                break;
        }
    }

    @Override
    public List<EntityDrop> getRareDrops() {
        List<EntityDrop> drops = new ArrayList<>(Arrays.asList(
                new EntityDrop("ENCHANTED_ENDER_PEARL", EntityDropRarity.RARE, 1, 1),
                new EntityDrop("END_HELMET", EntityDropRarity.RARE, 1, 1),
                new EntityDrop("END_CHESTPLATE", EntityDropRarity.RARE, 1, 1),
                new EntityDrop("END_LEGGINGS", EntityDropRarity.RARE, 1, 1),
                new EntityDrop("END_BOOTS", EntityDropRarity.RARE, 1, 1)));

        if (type.equals(EndermanType.SPECIAL_ZEALOT)) return Collections.singletonList(new EntityDrop("SUMMONING_EYE", EntityDropRarity.RARE, 1.0, 1));
        if (type.equals(EndermanType.ZEALOT_ENDERCHEST)) return Collections.singletonList(Arrays.asList(new EntityDrop("ENCHANTED_ENDER_PEARL", EntityDropRarity.RARE, 100, Util.random(1, 2)),
                new EntityDrop("CRYSTAL_FRAGMENT", EntityDropRarity.RARE, 100, 1),
                new EntityDrop("ENCHANTED_END_STONE", EntityDropRarity.RARE, 100, 1),
                new EntityDrop("ENCHANTED_OBSIDIAN", EntityDropRarity.RARE, 100, 1)).get(Util.random(0, 3)));
        return drops;
    }
}
