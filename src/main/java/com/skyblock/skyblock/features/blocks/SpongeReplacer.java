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
        SkyblockLocation location;

        try {
            location = Skyblock.getPlugin().getLocationManager().getLocation(region);
        } catch (NullPointerException ex) {
            Skyblock.getPlugin().sendMessage("&cCould not find region &8" + region + "&c, please make sure that your &8locations.yml &cis up to date");
            Bukkit.getPluginManager().disablePlugin(Skyblock.getPlugin());
            return;
        }

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
            Skyblock.getSkyblockWorld().getBlockAt(loc).setType(Material.SPONGE);
        }
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2)
    {
        List<Block> blocks = new ArrayList<Block>();

        return Util.getBlocks(loc1, loc2, blocks);
    }
}
