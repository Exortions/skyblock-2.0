package com.skyblock.skyblock.features.items.misc;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PlumberSponge extends ListeningItem {
    public PlumberSponge() {
        super("plumber_sponge");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e) {
        if (!isThisItem(e.getItemInHand())) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());

        e.setCancelled(true);
        if (player.isNotOnPrivateIsland()) return;

        Util.delay(() -> {
            Block origin = player.getBukkitPlayer().getWorld().getBlockAt(e.getBlock().getLocation());
            removeWater(origin, player.getBukkitPlayer());
        }, 2);
    }

    private void removeWater(Block block, Player p) {
        if (block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.WATER)) {
            block.setType(Material.AIR);

            World w = block.getWorld();
            Location l = block.getLocation();

            p.playSound(l, "mob.guardian.flop", 1, 1);

            Util.delay(() -> {
                removeWater(w.getBlockAt(l.clone().add(1, 0, 0)), p);
                removeWater(w.getBlockAt(l.clone().add(-1, 0, 0)), p);
                removeWater(w.getBlockAt(l.clone().add(0, 0, 1)), p);
                removeWater(w.getBlockAt(l.clone().add(0, 0, -1)), p);
                removeWater(w.getBlockAt(l.clone().add(0, 1, 0)), p);
                removeWater(w.getBlockAt(l.clone().add(0, -1, 0)), p);
            }, 2);
        }
    }
}
