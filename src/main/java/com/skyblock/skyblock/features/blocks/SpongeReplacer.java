package com.skyblock.skyblock.features.blocks;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class SpongeReplacer {

    private String region;
    private List<SpongeBlock> sponges;
    private List<Location> locations;

    public SpongeReplacer(String region, SpongeBlock... sponges) {
        this.region = region;
        this.sponges = new ArrayList<>();
        this.locations = new ArrayList<>();

        for (SpongeBlock block : sponges) {
            for (int i = 0; i < block.getRare(); i++) {
                this.sponges.add(block);
            }
        }
    }

    public void generate() {
        SkyblockLocation location = Skyblock.getPlugin().getLocationManager().getLocation(region);
        List<Block> blocks = blocksFromTwoPoints(location.getPosition1(), location.getPosition2());

        for (Block block : blocks) {
            if (!block.getType().equals(Material.SPONGE)) continue;
            SpongeBlock sponge = sponges.get(Util.random(0, sponges.size() - 1));
            block.setType(sponge.getMaterial());
            locations.add(block.getLocation());
        }
    }

    public void end() {
        for (Location loc : locations) {
            Bukkit.getWorld("world").getBlockAt(loc).setType(Material.SPONGE);
        }
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2)
    {
        List<Block> blocks = new ArrayList<Block>();

        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));

        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));

        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }
}
