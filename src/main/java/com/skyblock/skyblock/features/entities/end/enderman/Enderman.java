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
import java.util.List;

public class Enderman extends SkyblockEntity {

    private EndermanType type;
    public Enderman(String type) {
        super(EntityType.ENDERMAN);

        Equipment equipment = new Equipment();

        this.type = EndermanType.valueOf(type);

        switch (EndermanType.valueOf(type)){
            case FOUR_K:
                loadStats(4500, 500, false, false, true, equipment, "Enderman", 42, 40, 12, 10, "");
                break;
            case SIX_K:
                loadStats(6000, 600, false, false, true, equipment, "Enderman", 45, 42, 12, 12, "");
                break;
            case NINE_K:
                loadStats(9000, 700, false, false, true, equipment, "Enderman", 50, 44, 12, 15, "");
                break;
            case ZEALOT:
                loadStats(13000, 1250, false, false, true, equipment, "Zealot", 55, 45, 10, 15, "");
                break;
            case SPECIAL_ZEALOT:
                equipment.hand = new ItemStack(Material.ENDER_PORTAL_FRAME);

                loadStats(2000, 1250, false, false, true, equipment, "Zealot", 55, 40, 10, 15, "");
                break;
            case ZEALOT_ENDERCHEST:
                equipment.hand = new ItemStack(Material.ENDER_CHEST);

                loadStats(13000, 1250, false, false, true, equipment, "Zealot", 55, 45, 10, 15, "");
                break;
            default:
                break;
        }
    }

    @Override
    public List<EntityDrop> getDrops() {
        List<EntityDrop> drops = new ArrayList<>(Arrays.asList(
                new EntityDrop(Util.toSkyblockItem(new ItemStack(Material.ENDER_PEARL)), 3, 5),
                new EntityDrop(handler.getItem("ENCHANTED_ENDER_PEARL.json"), EntityDropRarity.RARE, 0.01, 1),
                new EntityDrop(handler.getItem("END_HELMET.json"), EntityDropRarity.RARE, 0.01, 1),
                new EntityDrop(handler.getItem("END_CHESTPLATE.json"), EntityDropRarity.RARE, 0.01, 1),
                new EntityDrop(handler.getItem("END_LEGGINGS.json"), EntityDropRarity.RARE, 0.01, 1),
                new EntityDrop(handler.getItem("END_BOOTS.json"), EntityDropRarity.RARE, 0.01, 1)));

        if (type.equals(EndermanType.SPECIAL_ZEALOT)) drops.add(new EntityDrop(handler.getItem("SUMMONING_EYE.json"), EntityDropRarity.RARE, 1.0, 1));
        if (type.equals(EndermanType.ZEALOT_ENDERCHEST)) drops.add(Arrays.asList(new EntityDrop(handler.getItem("ENCHANTED_ENDER_PEARL.json"), EntityDropRarity.OCCASIONAL, 1.0, Util.random(1, 2)),
                new EntityDrop(handler.getItem("CRYSTAL_FRAGMENT"), EntityDropRarity.OCCASIONAL, 1.0, 1),
                new EntityDrop(handler.getItem("ENCHANTED_END_STONE"), EntityDropRarity.OCCASIONAL, 1.0, 1),
                new EntityDrop(handler.getItem("ENCHANTED_OBSIDIAN"), EntityDropRarity.OCCASIONAL, 1.0, 1)).get(Util.random(0, 3)));
        return drops;
    }
}
