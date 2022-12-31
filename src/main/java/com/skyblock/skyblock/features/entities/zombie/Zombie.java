package com.skyblock.skyblock.features.entities.zombie;

import com.skyblock.skyblock.features.entities.EntityDrop;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Zombie extends SkyblockEntity {

    private final ZombieType type;

    public Zombie(String type) {
        super(EntityType.ZOMBIE);

        this.type = ZombieType.valueOf(type);

        Equipment equipment = new Equipment();

        switch (this.type) {
            case GRAVEYARD:
                loadStats(100, 20, true, false, true, equipment, "Zombie", 1, 6, "zombie");
                break;
            case SEA_WALKER:
                equipment.hand = Util.idToSkull(new ItemStack(Material.SKULL, SkullType.PLAYER.ordinal()), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzJlN2ZhMmY5YjhkNmQxZTczNGVkYTVlM2NlMDI2Njg4MTM0MjkyZmNhZmMzMjViMWVhZDQzZDg5Y2MxZTEifX19");
                equipment.chest = new ItemBuilder("", Material.LEATHER_CHESTPLATE).dyeColor(Color.BLUE).toItemStack();
                equipment.legs = new ItemBuilder("", Material.LEATHER_LEGGINGS).dyeColor(Color.BLUE).toItemStack();
                equipment.boots = new ItemBuilder("", Material.LEATHER_BOOTS).dyeColor(Color.BLUE).toItemStack();

                loadStats(1500, 60, true, false, true, equipment, "Sea Walker", 4, 68, "Fishing");
                break;
            case CRYPT_GHOUL:
                equipment.chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                equipment.legs = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                equipment.boots = new ItemStack(Material.CHAINMAIL_BOOTS);

                loadStats(2000, 350, true, false, true, equipment, "Crypt Ghoul", 30, 30, "crypt_ghoul");
                break;
            case GOLDEN_GHOUL:
                equipment.hand = new ItemStack(Material.GOLD_SWORD);
                equipment.chest = new ItemStack(Material.GOLD_CHESTPLATE);
                equipment.legs = new ItemStack(Material.GOLD_LEGGINGS);
                equipment.boots = new ItemStack(Material.GOLD_BOOTS);

                loadStats(45000, 800, true, false, true, equipment, "Golden Ghoul", 50, 45, "golden_ghoul");
                break;
            case LAPIS_ZOMBIE:
                equipment.helmet = new ItemStack(Material.SEA_LANTERN);
                equipment.chest = new ItemBuilder("", Material.LEATHER_CHESTPLATE).dyeColor(Color.BLUE).toItemStack();
                equipment.legs = new ItemBuilder("", Material.LEATHER_LEGGINGS).dyeColor(Color.BLUE).toItemStack();
                equipment.boots = new ItemBuilder("", Material.LEATHER_BOOTS).dyeColor(Color.BLUE).toItemStack();

                loadStats(200, 50, true, false, true, equipment, "Lapis Zombie", 7, 12, "lapis_zombie");
                break;
            case ZOMBIE_VILLAGER:
                equipment.helmet = new ItemStack(Material.LEATHER_HELMET);
                equipment.chest = new ItemStack(Material.LEATHER_CHESTPLATE);
                equipment.legs = new ItemStack(Material.LEATHER_LEGGINGS);
                equipment.boots = new ItemStack(Material.LEATHER_BOOTS);

                loadStats(120, 24, true, false, true, equipment, "Zombie Villager", 1, 7, "zombie_villager");
                break;
            case DIAMOND_RESERVE:
                equipment.helmet = new ItemStack(Material.DIAMOND_HELMET);
                equipment.chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
                equipment.legs = new ItemStack(Material.DIAMOND_LEGGINGS);
                equipment.boots = new ItemStack(Material.DIAMOND_BOOTS);
                equipment.hand = new ItemStack(Material.DIAMOND_SWORD);

                loadStats(250, 200, true, false, true, equipment, "Zombie", 15, 20, "diamond_zombie");
                break;
            case OBSIDIAN_SANCTUARY:
                equipment.helmet = new ItemBuilder(Material.DIAMOND_BLOCK).addEnchantmentGlint().toItemStack();
                equipment.chest = new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack();
                equipment.legs = new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchantmentGlint().toItemStack();
                equipment.boots = new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantmentGlint().toItemStack();
                equipment.hand = new ItemBuilder(Material.DIAMOND_SWORD).addEnchantmentGlint().toItemStack();
                
                loadStats(300, 275, true, false, true, equipment, "Zombie", 20, 20, "obsidian_zombie");
                break;
            default:
                break;
        }
    }

    @Override
    protected void tick() {
        if (tick == 0) {
            org.bukkit.entity.Zombie zombie = (org.bukkit.entity.Zombie) getVanilla();

            zombie.setVillager(false);
            zombie.setBaby(false);

            if (type.equals(ZombieType.ZOMBIE_VILLAGER)) zombie.setVillager(true);

            List<ZombieType> speedMod = Arrays.asList(ZombieType.ZOMBIE_VILLAGER, ZombieType.CRYPT_GHOUL, ZombieType.GOLDEN_GHOUL);

            if (speedMod.contains(type)) {
                AttributeInstance attributes = ((EntityInsentient)((CraftEntity) getVanilla()).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

                attributes.setValue(0.3);
            }
        }
    }

    @Override
    public List<EntityDrop> getRareDrops() {
        List<EntityDrop> drops = new ArrayList<>();

        if (type.equals(ZombieType.LAPIS_ZOMBIE)) drops.addAll(Arrays.asList(new EntityDrop("LAPIS_ARMOR_HELMET", 1, 1), new EntityDrop("LAPIS_ARMOR_CHESTPLATE", 1, 1), new EntityDrop("LAPIS_ARMOR_LEGGINGS", 1, 1), new EntityDrop("LAPIS_ARMOR_BOOTS", 1, 1)));
        if (type.equals(ZombieType.DIAMOND_RESERVE) || type.equals(ZombieType.OBSIDIAN_SANCTUARY)) drops.addAll(Arrays.asList(new EntityDrop("TANK_MINER_HELMET", 1, 1), new EntityDrop("TANK_MINER_CHESTPLATE", 1, 1), new EntityDrop("TANK_MINER_LEGGINGS", 1, 1), new EntityDrop("TANK_MINER_BOOTS", 1, 1)));

        return drops;
    }
}
