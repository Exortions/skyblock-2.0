package com.skyblock.skyblock.features.items.bows;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MosquitoBow extends SkyblockItem {

    private final List<UUID> active = new ArrayList<>();

    public MosquitoBow() {
        super(plugin.getItemHandler().getItem("MOSQUITO_BOW.json"), "mosquito_bow");
    }

    @Override
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getEntity());

        if (!player.getBukkitPlayer().isSneaking()) return;
        if (event.getForce() < 1) return;

        int cost = player.getStat(SkyblockStat.MAX_MANA) * 11 / 100;

        if (!player.checkMana(cost)) return;

        player.addStat(SkyblockStat.HEALTH, cost * 2);

        if (!active.contains(player.getBukkitPlayer().getUniqueId())) active.add(player.getBukkitPlayer().getUniqueId());

        Util.sendAbility(player, "Nasty Bite", cost);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (!active.contains(player.getBukkitPlayer().getUniqueId())) return damage;

        active.remove(player.getBukkitPlayer().getUniqueId());

        return damage * 1.19;
    }
}
