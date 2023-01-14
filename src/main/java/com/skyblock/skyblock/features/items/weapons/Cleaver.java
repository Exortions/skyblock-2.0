package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.ListeningItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class Cleaver extends ListeningItem {
    public Cleaver() {
        super(plugin.getItemHandler().getItem("CLEAVER.json"), "cleaver");
    }

    @Override
    public void onEntityDamage(SkyblockPlayerDamageEntityEvent event) {
        Player player = event.getPlayer().getBukkitPlayer();
        List<Entity> entities = event.getEntity().getVanilla().getNearbyEntities(3, 3, 3);
        for (Entity entity : entities) {
            SkyblockEntity skyblockEntity = plugin.getEntityHandler().getEntity(entity);

            if (skyblockEntity == null) continue;

            skyblockEntity.damage((long) (event.getDamage() * 0.1f), SkyblockPlayer.getPlayer(player), false);
        }
    }
}
