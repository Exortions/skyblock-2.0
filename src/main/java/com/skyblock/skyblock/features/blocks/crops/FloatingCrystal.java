package com.skyblock.skyblock.features.blocks.crops;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
@AllArgsConstructor
@SuppressWarnings("deprecation")
public class FloatingCrystal {

    private UUID id;
    private Material material;
    private short durability;
    private Location location;
    private int range;

    // First 2 runnables and particle code from https://github.com/superischroma/Spectaculation/blob/main/src/main/java/me/superischroma/spectaculation/entity/insentient/FloatingCrystal.java
    public void spawn() {
        String url = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTI2NWY5NmY1NGI3ODg4NWM0NmU3ZDJmODZiMWMxZGJmZTY0M2M2MDYwZmM3ZmNjOTgzNGMzZTNmZDU5NTEzNSJ9fX0=";

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARMOR_STAND, "", location);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        ArmorStandTrait trait = npc.getOrAddTrait(ArmorStandTrait.class);
        Equipment equipment = npc.getOrAddTrait(Equipment.class);

        equipment.set(Equipment.EquipmentSlot.HELMET, Util.idToSkull(new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()), url));
        trait.setVisible(false);
        trait.setGravity(false);

        ArmorStand stand = (ArmorStand) npc.getEntity();
        stand.setCustomNameVisible(false);

        List<Location> crops = getNearby(stand.getLocation());
        new BukkitRunnable() {
            public void run() {
                if (!stand.isDead()) {
                    Vector clone = stand.getVelocity().clone();
                    stand.setVelocity(new Vector(0, clone.getY() < 0 ? 0.1 : -0.1, 0));
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Skyblock.getPlugin(), 15, 15);

        new BukkitRunnable() {
            public void run() {
                if (!stand.isDead()) {
                    Location location = stand.getLocation();
                    location.setYaw(stand.getLocation().getYaw() + 15.0f);
                    stand.teleport(location);
                    stand.getWorld().spigot().playEffect(stand.getEyeLocation().clone().add(Util.random(-0.5, 0.5), 0.0, Util.random(-0.5, 0.5)), Effect.FIREWORKS_SPARK, 24, 1, 0, 0, 0, 1, 0, 64);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Skyblock.getPlugin(), 0, 1);

        new BukkitRunnable() {
            public void run() {
                if (stand.isDead()) {
                    cancel();
                    return;
                }

                List<Location> needsReplacing = new ArrayList<>();

                for (Location location : crops) {
                    Block block = location.getBlock();
                    if (block.getType().equals(Material.AIR)) needsReplacing.add(location);
                }

                if (needsReplacing.size() == 0) return;

                Random rand = new Random();
                Location loc = needsReplacing.get(rand.nextInt(needsReplacing.size()));
                Block replace = loc.getBlock();

                if (material.equals(Material.WHEAT)) replace.setTypeId(59);
                else replace.setType(material);

                BlockState blockState = replace.getState();
                blockState.setRawData(CropState.RIPE.getData());
                blockState.update();

                Vector vector = loc.clone().add(0.5, 0.0, 0.5).toVector().subtract(stand.getEyeLocation().clone().toVector());
                double count = 25.0;
                for (int i = 1; i <= (int) count; i++) {
                    stand.getWorld().spigot().playEffect(stand.getEyeLocation().clone().add(vector.clone().multiply((double) i / count)),
                            Effect.FIREWORKS_SPARK, 24, 1, 0, 0, 0, 1, 0, 64);
                }
            }
        }.runTaskTimer(Skyblock.getPlugin(), 20, 40);
    }

    private List<Location> getNearby(Location location) {
        List<Location> locations = new ArrayList<>();

        for (int x = location.getBlockX() - range; x <= location.getBlockX() + range; x++) {
            for (int y = location.getBlockY() - range; y <= location.getBlockY() + range; y++) {
                for (int z = location.getBlockZ() - range; z <= location.getBlockZ() + range; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType().equals(material)) locations.add(block.getLocation());
                }
            }
        }

        return locations;
    }

}
