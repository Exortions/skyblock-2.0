package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class GolemSword extends SkyblockItem {

    public GolemSword() {
        super(plugin.getItemHandler().getItem("GOLEM_SWORD.json"), "golem_sword");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (player.isOnCooldown(getInternalName())) {
            player.sendOnCooldown(getInternalName());

            return;
        }

        ItemBase item = new ItemBase(player.getBukkitPlayer().getItemInHand());

        int cost = item.getAbilityCost();

        if (!player.checkMana(cost)) return;

        Location location = player.getBukkitPlayer().getLocation();
        Vector dir = location.getDirection().normalize().multiply(1);
        location.add(dir);

        Location l = player.getBukkitPlayer().getLocation().add(0, -1, 0);

        player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.ANVIL_LAND, 100, 2);

        for (Player witness : player.getBukkitPlayer().getWorld().getPlayers()) {
            witness.playEffect(location, Effect.STEP_SOUND, l.getBlock().getTypeId());
            witness.playEffect(player.getBukkitPlayer().getLocation(), Effect.STEP_SOUND, l.getBlock().getTypeId());

            new BukkitRunnable() {
                @Override
                public void run() {
                    Location loc = player.getBukkitPlayer().getLocation();

                    witness.playEffect(l, Effect.EXPLOSION_HUGE, 10);
                    witness.playEffect(l, Effect.EXPLOSION_HUGE, 10);
                    witness.playEffect(l, Effect.EXPLOSION_HUGE, 10);

                    witness.playSound(loc, Sound.EXPLODE, 10, 1);
                }
            }.runTaskLater(Skyblock.getPlugin(), 10);

            if (player.getCooldown(getInternalName())) player.setCooldown(getInternalName(), 1);

            int x = 3;
            int y = 3;
            int z = 3;

            long damage = (long) ((player.getStat(SkyblockStat.ABILITY_DAMAGE) + 250) * (1 + (player.getStat(SkyblockStat.MANA) / 100F)));

            for (Entity entity : player.getBukkitPlayer().getNearbyEntities(x, y, z)) {
                if (!(entity instanceof LivingEntity)) continue;

                LivingEntity living = (LivingEntity) entity;

                if (entity instanceof Player) continue;

                SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(living);
                if (sentity == null) continue;

                sentity.damage(damage, player, false);
            }

            Util.sendMagicAbility(player, "Iron Punch", cost, (int) player.getBukkitPlayer().getNearbyEntities(x, y, z).stream().filter(e -> e.getType() != EntityType.ARMOR_STAND).count(), player.getBukkitPlayer().getNearbyEntities(x, y, z).stream().filter(e -> e.getType() != EntityType.ARMOR_STAND).count() * damage);
        }
    }
}
