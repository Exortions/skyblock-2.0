package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrnateZombieSword extends SkyblockItem {

    private static final HashMap<UUID, Integer> charges = new HashMap<>();

    public OrnateZombieSword() {
        super(Skyblock.getPlugin().getItemHandler().getItem("ORNATE_ZOMBIE_SWORD.json"), "ornate_zombie_sword");

        new BukkitRunnable() {
            @Override
            public void run() {
                charges.replaceAll((u, v) -> 5);
            }
        }.runTaskTimer(Skyblock.getPlugin(), 0, 20 * 15);
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        if (!charges.containsKey(event.getPlayer().getUniqueId())) charges.put(event.getPlayer().getUniqueId(), 5);

        if (charges.get(event.getPlayer().getUniqueId()) <= 0) {
            event.getPlayer().sendMessage(ChatColor.RED + "You have no charges left!");
            return;
        }

        charges.put(event.getPlayer().getUniqueId(), charges.get(event.getPlayer().getUniqueId()) - 1);

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (!player.checkMana(70)) return;

        int health = 144 + (int) (player.getStat(SkyblockStat.HEALTH) * 0.05);
        player.setStat(SkyblockStat.HEALTH, Math.min(player.getStat(SkyblockStat.HEALTH) + health, player.getStat(SkyblockStat.MAX_HEALTH)));

        List<Player> nearby = event.getPlayer().getNearbyEntities(7, 7, 7).stream().filter(e -> e instanceof Player).map(e -> (Player) e).collect(Collectors.toList());

        for (Player nearbyPlayer : nearby) {
            SkyblockPlayer nearbySkyblockPlayer = SkyblockPlayer.getPlayer(nearbyPlayer);

            nearbySkyblockPlayer.setStat(SkyblockStat.HEALTH, Math.min(nearbySkyblockPlayer.getStat(SkyblockStat.HEALTH) + 48, nearbySkyblockPlayer.getStat(SkyblockStat.MAX_HEALTH)));
        }

        Util.sendAbility(player, "Instant Heal", 70);
    }
}
