package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FrozenScythe extends SkyblockItem {

    public FrozenScythe() {
        super(plugin.getItemHandler().getItem("FROZEN_SCYTHE.json"), "frozen_scythe");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (!player.checkMana(50)) return;

        List<SkyblockEntity> entities = player.getBukkitPlayer().getNearbyEntities(100, 100, 100).stream()
                        .filter(entity -> plugin.getEntityHandler().getEntity(entity) != null)
                        .map(entity -> plugin.getEntityHandler().getEntity(entity))
                        .collect(Collectors.toList());

        Location origin;
        Location loc = player.getBukkitPlayer().getLocation();

        player.getBukkitPlayer().playSound(loc, Sound.FIREWORK_LAUNCH, 1.0f, 10.0f);

        loc.add(0, 1, 0);

        origin = loc.clone();

        Vector to1 = loc.getDirection().normalize().multiply(0.3);
        Vector to = loc.getDirection().normalize().multiply(1.0);

        EulerAngle angle = new EulerAngle(0, 0, 12.0);

        loc.add(to1);

        ArmorStand firstStand = origin.getWorld().spawn(loc, ArmorStand.class);
        firstStand.setVisible(false);
        firstStand.setArms(true);
        firstStand.setItemInHand(new ItemStack(Material.ICE));
        firstStand.setSmall(true);
        firstStand.setMarker(true);
        firstStand.setRightArmPose(angle);

        loc.add(to1);

        ArmorStand secondStand = origin.getWorld().spawn(loc, ArmorStand.class);
        secondStand.setVisible(false);
        secondStand.setArms(true);
        secondStand.setItemInHand(new ItemStack(Material.PACKED_ICE));
        secondStand.setSmall(true);
        secondStand.setMarker(true);
        secondStand.setRightArmPose(angle);

        loc.add(to1);

        ArmorStand thirdStand = origin.getWorld().spawn(loc, ArmorStand.class);
        thirdStand.setVisible(false);
        thirdStand.setArms(true);
        thirdStand.setItemInHand(new ItemStack(Material.ICE));
        thirdStand.setSmall(true);
        thirdStand.setMarker(true);
        thirdStand.setRightArmPose(angle);

        loc.add(to1);

        ArmorStand fourthStand = origin.getWorld().spawn(loc, ArmorStand.class);
        fourthStand.setVisible(false);
        fourthStand.setArms(true);
        fourthStand.setItemInHand(new ItemStack(Material.PACKED_ICE));
        fourthStand.setSmall(true);
        fourthStand.setMarker(true);
        fourthStand.setRightArmPose(angle);

        final boolean[] didHitBlock = {false};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (firstStand.getLocation().getBlock().getType() != Material.AIR) {
                    didHitBlock[0] = true;

                    firstStand.setGravity(false);
                    secondStand.setGravity(false);
                    thirdStand.setGravity(false);
                    fourthStand.setGravity(false);

                    Util.delay(() -> {
                        firstStand.remove();
                        secondStand.remove();
                        thirdStand.remove();
                        fourthStand.remove();
                    }, 100);

                    this.cancel();
                }

                ParticleEffect.CLOUD.display(firstStand.getLocation(), 0, 0, 0, 0, 1, null);

                firstStand.setVelocity(to);
                secondStand.setVelocity(to);
                thirdStand.setVelocity(to);
                fourthStand.setVelocity(to);

                long damage = Util.calculateAbilityDamage(1000, player.getStat(SkyblockStat.MANA), 0.1, 0);

                for (Entity entity : firstStand.getNearbyEntities(1, 1, 1)) {
                    SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(entity);

                    if (sentity == null) continue;

                    sentity.damage(damage, player, false);
                    EntityLiving nmsEntity = (EntityLiving) ((CraftEntity) entity).getHandle();
                    AttributeInstance speed = nmsEntity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
                    // for 5 seconds, cut the speed in half
                    speed.setValue(speed.getValue() / 2);
                    Util.delay(() -> speed.setValue(speed.getValue() * 2), 100);
                }

                if (firstStand.getLocation().distance(origin) > 50) {
                    firstStand.remove();
                    secondStand.remove();
                    thirdStand.remove();
                    fourthStand.remove();
                    this.cancel();
                }
            }
        }.runTaskTimer(Skyblock.getPlugin(), 0, 1);
    }
}
