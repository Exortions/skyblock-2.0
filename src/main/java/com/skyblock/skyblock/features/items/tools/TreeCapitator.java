package com.skyblock.skyblock.features.items.tools;

import com.sk89q.worldedit.blocks.BlockData;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import com.sun.org.apache.bcel.internal.generic.DUP;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.*;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.ArrayList;
import java.io.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.*;
import java.util.HashMap;

public class TreeCapitator extends SkyblockItem {
    ArrayList<Location> blockList = new ArrayList<>();

    public TreeCapitator() {
        super(plugin.getItemHandler().getItem("TREECAPITATOR.json"), "treecapitator_axe");
    }

    public void addBlocks(World w, Location l, Material logMat) {
	for (Integer x = l.getBlockX()-1; x < l.getBlockX() + 3; ++x) {
	    for (Integer y = l.getBlockY()-1; y < l.getBlockY() + 3; ++y) {
		for (Integer z = l.getBlockZ()-1; z < l.getBlockZ() + 3; ++z) {
        	    if (w.getBlockAt(x, y, z).getType().equals(logMat) && blockList.size() < 35 && !blockList.contains(new Location(w, x, y, z))) {
			blockList.add(new Location(w, x, y, z));
			addBlocks(w, new Location(w, x, y, z), logMat);
        	    } 
		}
	    }
	}
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
	SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(event.getPlayer());
	if (!skyblockPlayer.isOnCooldown(getInternalName()) && (event.getBlock().getType().equals(Material.LOG) || event.getBlock().getType().equals(Material.LOG_2))) {
		skyblockPlayer.setCooldown(getInternalName(), 2);
		if (!event.isCancelled()) {
		        addBlocks(event.getPlayer().getWorld(), event.getBlock().getLocation(), event.getBlock().getType());
			for (Location loc : blockList) {
			    BlockBreakEvent bev = new BlockBreakEvent(event.getPlayer().getWorld().getBlockAt(loc), event.getPlayer());
			    Bukkit.getPluginManager().callEvent(bev);
			    if (bev.isCancelled()) continue;
			    event.getPlayer().getWorld().getBlockAt(loc).setType(Material.AIR);
			};
			blockList.clear();
		}
	}
    }
}
