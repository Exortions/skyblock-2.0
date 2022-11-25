package com.skyblock.skyblock.features.slayer.boss;

import com.skyblock.skyblock.features.slayer.SlayerBoss;
import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

@Getter
public class RevenantHorror extends SlayerBoss {

    private Player spawner;

    public RevenantHorror(Player spawner, Integer level) {
        super(EntityType.ZOMBIE, SlayerType.REVENANT, spawner, level, 0.2);

        this.spawner = spawner;

        Equipment equipment = new Equipment();

        ItemStack zombieHelmet = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        zombieHelmet = Util.IDtoSkull(zombieHelmet, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhiZWUyM2I1YzcyNmFlOGUzZDAyMWU4YjRmNzUyNTYxOWFiMTAyYTRlMDRiZTk4M2I2MTQxNDM0OWFhYWM2NyJ9fX0=");

        equipment.helmet = zombieHelmet;
        equipment.chest = new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack();
        equipment.legs = new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchantmentGlint().toItemStack();
        equipment.boots = new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantmentGlint().toItemStack();
        equipment.hand = new ItemBuilder(Material.DIAMOND_HOE).toItemStack();

        switch (level) {
            case 1:
                loadStats(500, 15, true, false, true, equipment, "Revenant Horror", 10, 50);
                break;
            case 2:
                loadStats(20000, 25, true, false, true, equipment, "Revenant Horror", 70, 100);
                break;
            case 3:
                loadStats(400000, 90, true, false, true, equipment, "Revenant Horror", 310, 200);
                break;
            case 4:
                loadStats(1500000, 300, true, false, true, equipment, "Revenant Horror", 610, 500);
                break;
        }
    }

    @Override
    protected void tick() {
        super.tick();

        Zombie zombie = (Zombie) getVanilla();
        zombie.setBaby(false);
        zombie.setVillager(false);
    }
}
