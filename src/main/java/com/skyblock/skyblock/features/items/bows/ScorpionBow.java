package com.skyblock.skyblock.features.items.bows;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScorpionBow extends SkyblockItem {

    private final List<UUID> active = new ArrayList<>();

    public ScorpionBow() {
        super(plugin.getItemHandler().getItem("SCORPION_BOW.json"), "scorpion_bow");
    }

    @Override
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getEntity());

        if (!player.getBukkitPlayer().isSneaking() || event.getForce() < 1) return;

        if (!player.checkMana(150)) return;

        if (!active.contains(player.getBukkitPlayer().getUniqueId())) active.add(player.getBukkitPlayer().getUniqueId());

        Util.sendAbility(player, "Stinger", 150);
    }

    @Override
    public void onEntityDamage(SkyblockPlayerDamageEntityEvent event) {
        if (!active.contains(event.getPlayer().getBukkitPlayer().getUniqueId())) return;

        active.remove(event.getPlayer().getBukkitPlayer().getUniqueId());

        new BukkitRunnable() {
            int ticks = 0;
            final int damage = event.getPlayer().getStat(SkyblockStat.STRENGTH) * 35;

            @Override
            public void run() {
                if (ticks >= 6) {
                    cancel();
                    return;
                }

                event.getEntity().damage(damage, event.getPlayer(), false);

                ticks++;
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
