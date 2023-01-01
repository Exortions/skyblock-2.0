package com.skyblock.skyblock.features.items.bows;

import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

public class RunaansBow extends SkyblockItem {

    public RunaansBow() {
        super(plugin.getItemHandler().getItem("RUNAANS_BOW.json"), "runaans_bow");
    }

    @Override
    public void onBowShoot(EntityShootBowEvent e) {
        Player shooter = (Player) e.getEntity();
        Location location = shooter.getEyeLocation().add(shooter.getEyeLocation().getDirection().toLocation(shooter.getWorld()));
        float speed = e.getForce() * 3.0f;

        for (int i = 0; i < 2; i++) {
            Location l = location.clone();
            l.setYaw(location.getYaw() - (i == 0 ? 15.0f : -15.0f));
            shooter.getWorld().spawnArrow(l, l.getDirection(), speed, 1).setShooter(shooter);
        }
    }
}
