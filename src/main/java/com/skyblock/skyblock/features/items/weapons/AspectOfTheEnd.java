package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Set;

public class AspectOfTheEnd extends SkyblockItem {

    public AspectOfTheEnd() {
        super(plugin.getItemHandler().getItem("ASPECT_OF_THE_END.json"), "aspect_of_the_end");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        Player player = event.getPlayer();

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.checkMana(50)) {
            int range = 8;
            int time = 3;
            int strength = 0;

            if (data.containsKey("range_increase")) range += (int) data.get("range_increase");
            if (data.containsKey("time_increase")) time += (int) data.get("time_increase");
            if (data.containsKey("strength_increase")) strength += (int) data.get("strength_increase");

            Location location = player.getTargetBlock((Set<Material>) null, range).getLocation();
            location.setYaw(player.getLocation().getYaw());
            location.setPitch(player.getLocation().getPitch());
            player.teleport(location);

            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 3f, 1f);

            if (skyblockPlayer.getCooldown(getInternalName())) {
                skyblockPlayer.addStat(SkyblockStat.SPEED, 50);
                skyblockPlayer.addStat(SkyblockStat.STRENGTH, strength);
                skyblockPlayer.setCooldown(getInternalName(), 3);

                int finalStrength = strength;
                skyblockPlayer.delay(() -> {
                    skyblockPlayer.subtractStat(SkyblockStat.SPEED, 50);
                    skyblockPlayer.subtractStat(SkyblockStat.STRENGTH, finalStrength);
                }, time);
            }

            Util.sendAbility(skyblockPlayer, "Instant Transmission", 50);
        }
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (!player.hasFullSetBonus() || !player.getArmorSet().equals(Skyblock.getPlugin().getSkyblockItemHandler().getRegisteredSet("strong_dragon_armor"))) return damage;

        return damage + 70;
    }
}
