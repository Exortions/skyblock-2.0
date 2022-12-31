package com.skyblock.skyblock.features.items.tools;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class JungleAxe extends SkyblockItem {

    private final ArrayList<Location> blockList = new ArrayList<>();

    public JungleAxe() {
        super(plugin.getItemHandler().getItem("JUNGLE_AXE.json"), "jungle_axe");
    }

    public void addBlocks(World w, Location l, Material logMat) {
        for (int x = l.getBlockX() - 1; x < l.getBlockX() + 3; ++x) {
            for (int y = l.getBlockY() - 1; y < l.getBlockY() + 3; ++y) {
                for (int z = l.getBlockZ() - 1; z < l.getBlockZ() + 3; ++z) {
                    if (!(w.getBlockAt(x, y, z).getType().equals(logMat) && blockList.size() < 10 && !blockList.contains(new Location(w, x, y, z))))
                        return;
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

        for (Location loc : blockList) {
            BlockBreakEvent bev = new BlockBreakEvent(event.getPlayer().getWorld().getBlockAt(loc), event.getPlayer());
            Bukkit.getPluginManager().callEvent(bev);
            if (bev.isCancelled()) continue;
            event.getPlayer().getWorld().getBlockAt(loc).setType(Material.AIR);
        }

        blockList.clear();
    }
}
