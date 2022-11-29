package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.RotationUtil;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.Collections;

public class AspectOfTheDragons extends SkyblockItem {

    private final Skyblock plugin;

    public AspectOfTheDragons(Skyblock plugin) {
        super(plugin.getItemHandler().getItem("ASPECT_OF_THE_DRAGON.json"), "aspect_of_the_dragon");

        this.plugin = plugin;
    }

    public Entity[] getEntitiesInFront(Player player, int range) {
        return player.getNearbyEntities(range, range, range).stream().filter(entity -> {
            double angle = (Math.atan2(entity.getLocation().getZ() - player.getLocation().getZ(), entity.getLocation().getX() - player.getLocation().getX()) - Math.toRadians(player.getLocation().getYaw() + 90)) % (Math.PI * 2);
            return angle < 1.5 && angle > -1.5;
        }).toArray(Entity[]::new);
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.isOnCooldown(getInternalName())) {
            player.sendMessage(ChatColor.RED + "This ability is on cooldown.");

            return;
        }

        ItemBase item = new ItemBase(player.getItemInHand());

        int cost = item.getAbilityCost();

        if (!skyblockPlayer.checkMana(cost)) return;

        Entity[] entities = getEntitiesInFront(player, 4);

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

        this.playEffect(player.getLocation().add(player.getLocation().getDirection().multiply(2)), player.getLocation().getDirection());

        Util.sendAbility(skyblockPlayer, "Dragon Rage", cost);
    }

    public void playEffect(Location location, Vector direction) {
        Location playersCursor = location.add(direction.multiply(2)).add(0, 1.75, 0);

        double offset = 0.75;
        int rotation = 0;

        // TODO: Rotate (kill me now)

        // top
        ParticleEffect.FLAME.display(playersCursor.add(0, offset, 0), 0, 0, 0, 0, 1, null);
        playersCursor.subtract(0, offset, 0);

        // bottom
        ParticleEffect.FLAME.display(playersCursor.add(0, -offset, 0));
        playersCursor.subtract(0, -offset, 0);

        // left
        ParticleEffect.FLAME.display(playersCursor.add(direction.getZ() * (offset / 2), 0, -direction.getX() * (offset / 2)));
        playersCursor.subtract(direction.getZ() * (offset / 2), 0, -direction.getX() * (offset / 3) + (offset / 4));

        // right
        ParticleEffect.FLAME.display(playersCursor.add(-direction.getZ() * (offset / 2), 0, direction.getX() * (offset / 2)));
        playersCursor.subtract(-direction.getZ() * (offset / 2), 0, direction.getX() * (offset / 2));

        // top left
        ParticleEffect.FLAME.display(playersCursor.add(direction.getZ() * offset / 3, offset - (offset / 3), -direction.getX() * offset / 3));
        playersCursor.subtract(direction.getZ() * offset / 3, offset - (offset / 3), -direction.getX() * offset / 3);

        // top right
        ParticleEffect.FLAME.display(playersCursor.add(-direction.getZ() * offset / 3, offset - (offset / 3), direction.getX() * offset / 3));
        playersCursor.subtract(-direction.getZ() * offset / 3, offset - (offset / 3), direction.getX() * offset / 3);

        // bottom left
        ParticleEffect.FLAME.display(playersCursor.add(direction.getZ() * offset / 3, -offset + (offset / 3), -direction.getX() * offset / 3));
        playersCursor.subtract(direction.getZ() * offset / 3, -offset + (offset / 3), -direction.getX() * offset / 3);

        // bottom right
        ParticleEffect.FLAME.display(playersCursor.add(-direction.getZ() * offset / 3, -offset + (offset / 3), direction.getX() * offset / 3));
    }
}
