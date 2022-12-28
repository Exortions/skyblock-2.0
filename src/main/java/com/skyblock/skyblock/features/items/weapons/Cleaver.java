package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.event.SkyblockEntityDamageByPlayerEvent;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Cleaver extends ListeningItem {
    public Cleaver() {
        super(plugin.getItemHandler().getItem("CLEAVER.json"), "cleaver");
    }

    @Override
    public void onEntityDamage(SkyblockEntityDamageByPlayerEvent event) {
        Player player = event.getPlayer().getBukkitPlayer();
        List<Entity> entities = event.getEntity().getVanilla().getNearbyEntities(3, 3, 3);
        for (Entity entity : entities) {
            SkyblockEntity skyblockEntity = plugin.getEntityHandler().getEntity(entity);

            if (skyblockEntity == null) continue;

            skyblockEntity.damage((long) (event.getDamage() * 0.1f), SkyblockPlayer.getPlayer(player), false);
        }
    }
}
