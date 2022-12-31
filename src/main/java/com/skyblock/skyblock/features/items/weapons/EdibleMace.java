package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EdibleMace extends SkyblockItem {

    private final List<UUID> active = new ArrayList<>();

    public EdibleMace() {
        super(plugin.getItemHandler().getItem("EDIBLE_MACE.json"), "edible_mace");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (active.contains(player.getBukkitPlayer().getUniqueId())) {
            active.remove(player.getBukkitPlayer().getUniqueId());

            return damage * 2;
        }

        return damage;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (!player.checkMana(100)) return;

        active.remove(event.getPlayer().getUniqueId());
        active.add(event.getPlayer().getUniqueId());

        Util.sendAbility(player, "ME SMASH HEAD", 100);
    }
}
