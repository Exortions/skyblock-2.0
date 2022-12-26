package com.skyblock.skyblock.features.entities.end;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.projectiles.ProjectileSource;

public class Watcher extends SkyblockEntity {
    public Watcher() {
        super(EntityType.SKELETON);

        loadStats(9500, 500, true, false, true, new Equipment(getPlugin().getItemHandler().getItem("SUMMONING_EYE.json"),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).dyeColor(Color.BLACK).toItemStack(), new ItemBuilder(Material.LEATHER_LEGGINGS).dyeColor(Color.BLACK).toItemStack(),
                new ItemBuilder(Material.LEATHER_BOOTS).dyeColor(Color.BLACK).toItemStack(), null), "Watcher", 55, 40, "watcher");
    }

    @Override
    protected void tick() {
        if (tick % (20 * 5) == 0) {
            Arrow arrow = ((ProjectileSource) getVanilla()).launchProjectile(Arrow.class, getVanilla().getLocation().getDirection());
            arrow.setCritical(true);

            Util.delay(arrow::remove, 20 * 3);
        }
    }
}
