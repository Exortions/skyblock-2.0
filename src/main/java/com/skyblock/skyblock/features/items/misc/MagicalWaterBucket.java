package com.skyblock.skyblock.features.items.misc;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class MagicalWaterBucket extends ListeningItem {

    public MagicalWaterBucket() {
        super(plugin.getItemHandler().getItem("MAGICAL_WATER_BUCKET.json"), "magical_water_bucket");
    }

    @EventHandler
    public void PlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (!player.isOnIsland()) {
            event.setCancelled(true);
            return;
        }

        if (!Util.getSkyblockId(event.getItemStack()).equals("magical_water_bucket")) return;

        event.setItemStack(plugin.getItemHandler().getItem("MAGICAL_WATER_BUCKET.json"));
    }

}