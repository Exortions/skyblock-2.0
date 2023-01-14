package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.ArmorSet;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class UnstableDragonArmor extends ArmorSet {

    HashMap<String, Long> lastStrike = new HashMap<>(); 

    public UnstableDragonArmor() {
        super(Skyblock.getPlugin().getItemHandler().getItem("UNSTABLE_DRAGON_HELMET.json"),
                Skyblock.getPlugin().getItemHandler().getItem("UNSTABLE_DRAGON_CHESTPLATE.json"),
                Skyblock.getPlugin().getItemHandler().getItem("UNSTABLE_DRAGON_LEGGINGS.json"),
                Skyblock.getPlugin().getItemHandler().getItem("UNSTABLE_DRAGON_BOOTS.json"),
                "unstable_dragon_armor"
        );
    }

    @Override
    public void tick(Player player) {
        SkyblockPlayer sbp = SkyblockPlayer.getPlayer(player);
        lastStrike.putIfAbsent(player.getName(), 0L);

        if (lastStrike.get(player.getName()) + 30000 < System.currentTimeMillis()) {
            List<Entity> nearbys = new ArrayList<>(player.getWorld().getNearbyEntities(player.getLocation(), 8, 8, 8));
            nearbys = nearbys.stream().filter(entity -> {
                return entity instanceof Monster || entity instanceof Animals;
            }).collect(Collectors.toList());
            if (nearbys.size() == 0) return;
            lastStrike.put(player.getName(), System.currentTimeMillis());
            Collections.shuffle(nearbys);
            Entity toStrike = nearbys.get(0);
            player.getWorld().strikeLightningEffect(toStrike.getLocation());
            Skyblock.getPlugin().getEntityHandler().getEntity(toStrike).damage(3000, SkyblockPlayer.getPlayer(player), false);
        }
    }
}
