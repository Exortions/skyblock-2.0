package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class InkWand extends SkyblockItem {
    public InkWand() {
        super(plugin.getItemHandler().getItem("INK_WAND.json"), "ink_wand");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        Player player = event.getPlayer();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.isOnCooldown(getInternalName())) {
            skyblockPlayer.sendOnCooldown(getInternalName());
            return;
        }

        if (skyblockPlayer.checkMana(60)) {
            Item item = player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.INK_SACK));

            item.setPickupDelay(Integer.MAX_VALUE);

            Vector vector = player.getLocation().getDirection().multiply(0.25);
            vector.setY(0.60);

            item.setVelocity(vector);

            skyblockPlayer.setCooldown("ink_wand", 30);

            Util.sendAbility(skyblockPlayer, "Ink Bomb", 60);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (item.isDead() || item.isOnGround()) {
                        item.getWorld().playEffect(item.getLocation(), Effect.LARGE_SMOKE, 10);

                        for (Entity entity : item.getNearbyEntities(3.5, 3.5, 3.5)) {
                            SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(entity);

                            if (sentity == null) continue;

                            sentity.damage(Util.calculateAbilityDamage(50000, skyblockPlayer.getStat(SkyblockStat.MAX_MANA), 1.5, skyblockPlayer.getStat(SkyblockStat.ABILITY_DAMAGE)), skyblockPlayer, false);
                        }

                        item.remove();
                        cancel();
                    }
                }
            }.runTaskTimer(Skyblock.getPlugin(), 1, 1);
        }
    }
}
