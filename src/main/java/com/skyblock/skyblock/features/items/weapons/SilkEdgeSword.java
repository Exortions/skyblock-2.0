package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class SilkEdgeSword extends ListeningItem {
    public SilkEdgeSword() {
        super(plugin.getItemHandler().getItem("SILK_EDGE_SWORD.json"), "silk_edge_sword");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.hasExtraData("silk_edge_sword")) {
            if (e.getTo().clone().add(0, -1, 0)
                    .getBlock().getType() != Material.AIR) {

                for (int i = 0; i < 5; i++) player.getLocation().getWorld().playEffect(e.getTo(), Effect.EXPLOSION_LARGE, 10);

                player.getLocation().getWorld().playEffect(e.getTo(), Effect.LARGE_SMOKE, 10);
                player.getLocation().getWorld().playEffect(e.getTo(), Effect.FLAME, 10);

                player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);

                for (Entity entity : e.getTo().getWorld().getNearbyEntities(e.getTo(), 2, 2, 2)) {
                    SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(entity);

                    if (sentity == null) continue;

                    sentity.damage(Util.calculateAbilityDamage(400, skyblockPlayer.getStat(SkyblockStat.MAX_MANA), 1, skyblockPlayer.getStat(SkyblockStat.ABILITY_DAMAGE)), skyblockPlayer, false);
                }

                skyblockPlayer.setExtraData("silk_edge_sword", null);
            }
        }
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        Player player = event.getPlayer();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.isOnCooldown(getInternalName())) {
            skyblockPlayer.sendOnCooldown(getInternalName());
            return;
        }

        if (skyblockPlayer.checkMana(50)) {
            Vector vector = player.getLocation().getDirection().multiply(1.5);
            vector.setY(1);

            player.setVelocity(vector);
            skyblockPlayer.setCooldown("silk_edge_sword", 1);

            Util.sendAbility(skyblockPlayer, "Leap", 50);

            Util.delay(() -> {
                skyblockPlayer.setExtraData("silk_edge_sword", true);
            }, 10);
        }
    }
}
