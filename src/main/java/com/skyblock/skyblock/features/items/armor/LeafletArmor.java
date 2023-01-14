package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockPlayerLocationChangeEvent;
import com.skyblock.skyblock.features.items.BlockHelmetSet;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class LeafletArmor extends BlockHelmetSet {

    private final HashMap<UUID, BukkitRunnable> tasks = new HashMap<>();

    public LeafletArmor() {
        super(
                "LEAFLET_HELMET.json", "LEAFLET_CHESTPLATE.json",
                "LEAFLET_LEGGINGS.json", "LEAFLET_BOOTS.json", "leaflet_armor"
        );
    }

    @EventHandler
    public void onPlayerLocationChange(SkyblockPlayerLocationChangeEvent event) {
        if (event.getTo().equals("Forest") || event.getTo().equals("The Park")) {
            if (!tasks.containsKey(event.getPlayer().getBukkitPlayer().getUniqueId())) {
                tasks.put(event.getPlayer().getBukkitPlayer().getUniqueId(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getPlayer().addStat(SkyblockStat.HEALTH, 5);
                    }
                });

                tasks.get(event.getPlayer().getBukkitPlayer().getUniqueId()).runTaskTimer(Skyblock.getPlugin(), 0, 20);
            }
        } else {
            if (tasks.containsKey(event.getPlayer().getBukkitPlayer().getUniqueId())) {
                tasks.get(event.getPlayer().getBukkitPlayer().getUniqueId()).cancel();
                tasks.remove(event.getPlayer().getBukkitPlayer().getUniqueId());
            }
        }
    }

}
