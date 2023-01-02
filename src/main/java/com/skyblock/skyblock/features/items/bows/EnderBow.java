package com.skyblock.skyblock.features.items.bows;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EnderBow extends ListeningItem {

    private final List<UUID> active = new ArrayList<>();

    public EnderBow() {
        super(plugin.getItemHandler().getItem("ENDER_BOW.json"), "ender_bow");
    }

    @Override
    public void onLeftClick(PlayerInteractEvent event) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (!player.checkMana(50)) return;

        if (player.isOnCooldown(getInternalName())) {
            player.getBukkitPlayer().sendMessage(ChatColor.RED + "This ability is on cooldown.");

            return;
        }

        if (!active.contains(player.getBukkitPlayer().getUniqueId())) active.add(player.getBukkitPlayer().getUniqueId());

        EnderPearl enderPearl = player.getBukkitPlayer().launchProjectile(EnderPearl.class);
        enderPearl.setShooter(player.getBukkitPlayer());

        if (player.getCooldown(getInternalName())) player.setCooldown(getInternalName(), 45);

        Util.sendAbility(player, "Ender Warp", 50);
    }

    @EventHandler
    public void onPearlLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getEntity().getShooter());

        if (!active.contains(player.getBukkitPlayer().getUniqueId())) return;

        active.remove(player.getBukkitPlayer().getUniqueId());

        Location landed = event.getEntity().getLocation();

        player.getBukkitPlayer().teleport(landed);

        List<SkyblockEntity> entities = player.getBukkitPlayer().getNearbyEntities(8, 8, 8)
                .stream()
                .filter(entity -> Skyblock.getPlugin().getEntityHandler().getEntity(entity) != null)
                .map(entity -> Skyblock.getPlugin().getEntityHandler().getEntity(entity))
                .collect(java.util.stream.Collectors.toList());

        for (SkyblockEntity entity : entities) {
            double maxHealth = entity.getEntityData().maximumHealth;
            long damage = (long) (maxHealth / 10);

            entity.damage(damage, player, false);
        }
    }

}
