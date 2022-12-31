package com.skyblock.skyblock.features.items.tools;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class TreeCapitator extends SkyblockItem {

    private final ArrayList<Location> blockList = new ArrayList<>();

    public TreeCapitator() {
        super(plugin.getItemHandler().getItem("TREECAPITATOR.json"), "treecapitator");
    }

    public void addBlocks(World w, Location l, Material logMat) {
        for (int x = l.getBlockX() - 1; x < l.getBlockX() + 3; ++x) {
            for (int y = l.getBlockY() - 1; y < l.getBlockY() + 3; ++y) {
                for (int z = l.getBlockZ() - 1; z < l.getBlockZ() + 3; ++z) {
                    if (!(w.getBlockAt(x, y, z).getType().equals(logMat) && blockList.size() < 35 && !blockList.contains(new Location(w, x, y, z))))
                        continue;

                    blockList.add(new Location(w, x, y, z));
                    addBlocks(w, new Location(w, x, y, z), logMat);
                }
            }
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(event.getPlayer());
        if (!(!skyblockPlayer.isOnCooldown(getInternalName()) && (event.getBlock().getType().equals(Material.LOG) || event.getBlock().getType().equals(Material.LOG_2))))
            return;

        skyblockPlayer.setCooldown(getInternalName(), 2);

        if (event.isCancelled()) return;

        addBlocks(event.getPlayer().getWorld(), event.getBlock().getLocation(), event.getBlock().getType());

        int delay = 0;

        for (Location loc : blockList) {
            final boolean[] cancelled = {false};

            new BukkitRunnable() {
                @Override
                public void run() {
                    BlockBreakEvent bev = new BlockBreakEvent(event.getPlayer().getWorld().getBlockAt(loc), event.getPlayer());
                    Bukkit.getPluginManager().callEvent(bev);
                    if (bev.isCancelled()) {
                        cancelled[0] = true;
                        return;
                    }
                    event.getPlayer().getWorld().getBlockAt(loc).setType(Material.AIR);
                }
            }.runTaskLater(plugin, delay);

            if (!cancelled[0]) delay += 1;
        }

        blockList.clear();
    }
}
