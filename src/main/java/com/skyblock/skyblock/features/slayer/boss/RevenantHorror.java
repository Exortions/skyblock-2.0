package com.skyblock.skyblock.features.slayer.boss;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.slayer.SlayerBoss;
import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

@Getter
public class RevenantHorror extends SlayerBoss {

    private static final List<Double> SPEEDS = Arrays.asList(0.35, 0.4, 0.45, 0.55);

    private final Player spawner;
    private final double speed;

    private boolean enraged;

    public RevenantHorror(Player spawner, Integer level) {
        super(EntityType.ZOMBIE, SlayerType.REVENANT, spawner, level, 0.2);

        this.spawner = spawner;
        this.enraged = false;

        Equipment equipment = new Equipment();

        ItemStack zombieHelmet = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        zombieHelmet = Util.idToSkull(zombieHelmet, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhiZWUyM2I1YzcyNmFlOGUzZDAyMWU4YjRmNzUyNTYxOWFiMTAyYTRlMDRiZTk4M2I2MTQxNDM0OWFhYWM2NyJ9fX0=");

        equipment.helmet = zombieHelmet;
        equipment.chest = new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack();
        equipment.legs = new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchantmentGlint().toItemStack();
        equipment.boots = new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantmentGlint().toItemStack();
        equipment.hand = new ItemBuilder(Material.DIAMOND_HOE).toItemStack();

        switch (level) {
            case 1:
                loadStats(500, 15, true, false, false, equipment, "Revenant Horror", 10, 50);
                break;
            case 2:
                loadStats(20000, 50, true, false, false, equipment, "Revenant Horror", 70, 100);
                break;
            case 3:
                loadStats(400000, 300, true, false, false, equipment, "Revenant Horror", 310, 200);
                break;
            case 4:
                loadStats(1500000, 1000, true, false, false, equipment, "Revenant Horror", 610, 500);
                break;
        }

        speed = SPEEDS.get(level - 1);
    }
    @Override
    protected void tick() {
        super.tick();

        Zombie zombie = (Zombie) getVanilla();
        zombie.setBaby(false);
        zombie.setVillager(false);

        zombie.setTarget(spawner);

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(spawner);

        EntityZombie nms = ((CraftZombie) zombie).getHandle();

        nms.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(getMovementSpeed());

        if (tick % 80 == 0) {
            skyblockPlayer.damage(getEntityData().damage / 2f, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getVanilla());
        }

        if (tick % 20 == 0 && getLevel() >= 2) {
            for (Entity entity : getVanilla().getNearbyEntities(8.5, 5, 8.5)) {
                if (entity instanceof Player) {
                    SkyblockPlayer p = SkyblockPlayer.getPlayer((Player) entity);
                    p.damage(getEntityData().damage / 4f, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getVanilla());
                }
            }
        }

        if (tick % (20 * 40) == 0 && getLevel() >= 3 && tick != 0) {
            enraged = true;
            spawner.playSound(spawner.getLocation(), Sound.ZOMBIE_WOODBREAK, 1f, 1f);
            spawner.setVelocity(new Vector(Util.random(-1.0, 1.0), Util.random(0.0, 0.5), Util.random(-1.0, 1.0)));
            zombie.getEquipment().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).addEnchantmentGlint().dyeColor(Color.RED).toItemStack());
            Util.delay(() -> {
                enraged = false;
                zombie.getEquipment().setChestplate(getEntityData().chestplate);
                nms.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(getMovementSpeed());
            }, 20 * 12);
        }
    }

    private double getMovementSpeed() {
        return SPEEDS.get(getLevel() - 1) * (enraged ? 1.05 : 1.0);
    }
}
