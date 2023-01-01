package com.skyblock.skyblock.features.items.bows;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.SkyblockItem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WitherBow extends SkyblockItem {

    public WitherBow() {
        super(plugin.getItemHandler().getItem("WITHER_BOW.json"), "wither_bow");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (e.getEntity() instanceof Wither || (e.getEntity() instanceof Skeleton && ((Skeleton) e.getEntity()).getSkeletonType() == SkeletonType.WITHER)) {
            
            if (isThisItem(player.getBukkitPlayer().getItemInHand())) {
                return damage * 2;
            }
        }
        return damage;
    }
}
