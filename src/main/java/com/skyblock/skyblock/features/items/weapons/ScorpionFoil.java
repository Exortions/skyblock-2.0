package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ScorpionFoil extends SkyblockItem {

    private final HashMap<UUID, Integer> tickers = new HashMap<>();
    private final List<UUID> empoweredAttacks = new ArrayList<>();

    public ScorpionFoil() {
        super(Skyblock.getPlugin().getItemHandler().getItem("SCORPION_FOIL.json"), "scorpion_foil");

        new BukkitRunnable() {
            @Override
            public void run() {
                tickers.forEach((uuid, ticker) -> {
                    if (!SkyblockPlayer.getPlayer(uuid).getBukkitPlayer().getItemInHand().hasItemMeta() || !SkyblockPlayer.getPlayer(uuid).getBukkitPlayer().getItemInHand().getItemMeta().getDisplayName().contains("Scorpion Foil")) {
                        tickers.remove(uuid);
                        return;
                    }

                    if (ticker < 4) tickers.put(uuid, 4);
                });
            }
        }.runTaskTimer(Skyblock.getPlugin(), 0, 100);
    }


    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (!tickers.containsKey(player.getBukkitPlayer().getUniqueId()))
            tickers.put(player.getBukkitPlayer().getUniqueId(), 4);

        if (tickers.get(player.getBukkitPlayer().getUniqueId()) < 1) {
            player.getBukkitPlayer().sendMessage(ChatColor.RED + "You are out of tickers!");
            return;
        }

        tickers.put(player.getBukkitPlayer().getUniqueId(), tickers.get(player.getBukkitPlayer().getUniqueId()) - 1);
        player.addStat(SkyblockStat.HEALTH, Math.min(60, player.getStat(SkyblockStat.MAX_HEALTH) - player.getStat(SkyblockStat.HEALTH)));

        if (tickers.get(player.getBukkitPlayer().getUniqueId()) < 1) empoweredAttacks.add(player.getBukkitPlayer().getUniqueId());
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (e.getEntity().getType().equals(EntityType.SPIDER)) damage *= 3.5;

        if (empoweredAttacks.contains(player.getBukkitPlayer().getUniqueId())) {
            empoweredAttacks.remove(player.getBukkitPlayer().getUniqueId());
            damage *= 3.5;
        }

        return damage;
    }
}