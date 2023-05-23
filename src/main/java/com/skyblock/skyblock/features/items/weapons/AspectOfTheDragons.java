package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.HashMap;

public class AspectOfTheDragons extends SkyblockItem {

    public AspectOfTheDragons() {
        super(plugin.getItemHandler().getItem("ASPECT_OF_THE_DRAGON.json"), "aspect_of_the_dragon");
    }

    public Entity[] getEntitiesInFront(Player player, int range) {
        return player.getNearbyEntities(range, range, range).stream().filter(entity -> {
            double angle = (Math.atan2(entity.getLocation().getZ() - player.getLocation().getZ(), entity.getLocation().getX() - player.getLocation().getX()) - Math.toRadians(player.getLocation().getYaw() + 90)) % (Math.PI * 2);
            return angle < 1.5 && angle > -1.5;
        }).toArray(Entity[]::new);
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        Player player = event.getPlayer();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.isOnCooldown(getInternalName())) {
            skyblockPlayer.sendOnCooldown(getInternalName());

            return;
        }

        ItemBase item = new ItemBase(player.getItemInHand());

        int cost = item.getAbilityCost();

        if (!skyblockPlayer.checkMana(cost)) return;

        Entity[] entities = getEntitiesInFront(player, 7);

        player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 1);

        for (Entity entity : entities) {
            SkyblockEntity skyblockEntity = this.plugin.getEntityHandler().getEntity(entity);

            if (skyblockEntity == null) continue;

            skyblockEntity.getVanilla().setVelocity(player.getLocation().getDirection().multiply(5));

            int damage = 700;

            if (skyblockPlayer.hasFullSetBonus() && skyblockPlayer.getFullSetBonusType().equals("superior_dragon_armor")) {
                damage += (damage / 2);
            }

            skyblockEntity.damage(damage, skyblockPlayer, true);
        }

        if (skyblockPlayer.getCooldown(getInternalName())) {
            skyblockPlayer.setCooldown(getInternalName(), 5);
        }

        this.playEffect(player);

        Util.sendAbility(skyblockPlayer, "Dragon Rage", cost);
    }


    public void playEffect(Player player) {
        double circles = 5;
        double delay = 2.5;
        double distance = 0.75;

        double radius = 0.75;
        double amount = 8;

        Vector dir = player.getLocation().getDirection();
        Location location = player.getLocation().add(0, 0.75, 0);

        for (int c = 0; c < circles; c++) {
            int iteration = c;

            new BukkitRunnable() {
                @Override
                public void run() {
                    Vector direction = dir.clone();
                    Location loc = location.clone().add(direction.multiply(3 + (iteration * distance))).add(0, 0.75, 0);

                    Vector x1 = new Vector(-direction.clone().normalize().getZ(), 0d, direction.clone().normalize().getX()).normalize();
                    Vector x2 = direction.clone().normalize().crossProduct(x1).normalize();

                    x1 = Util.rotateAroundAxisZ(x1, iteration * (360 / circles * 2));
                    x2 = Util.rotateAroundAxisZ(x2, iteration * (360 / circles * 2));

                    for (int i = 0; i < 8; i++) {
                        Location position = loc.clone().add(x1.clone().multiply(radius * Math.sin((double) i / amount * Math.PI * 2d))).add(x2.clone().multiply(radius * Math.cos((double) i / amount * Math.PI * 2d)));

                        ParticleEffect.FLAME.display(position, 0, 0, 0, 0, 3, null);
                    }
                }
            }.runTaskLater(plugin, (long) (c * delay));
        }
    }
}
