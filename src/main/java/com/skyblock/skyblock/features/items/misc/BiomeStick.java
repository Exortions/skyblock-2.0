package com.skyblock.skyblock.features.items.misc;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.DynamicLore;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class BiomeStick extends ListeningItem implements DynamicLore {

    private final Biome biome;

    public BiomeStick(String sbid, Biome bio) {
        super(plugin.getItemHandler().getItem(sbid + ".json"), sbid.toLowerCase());
        this.biome = bio;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (!isThisItem(event.getPlayer().getItemInHand()) || event.getClickedBlock() == null || !player.isOnIsland())
            return;

        NBTItem nbt = new NBTItem(event.getPlayer().getItemInHand());
        int radius = nbt.getInteger("biomestick_radius");
        Location clickBlock = event.getClickedBlock().getLocation();

        for (int x = clickBlock.getBlockX() - radius; x < clickBlock.getBlockX() + radius; x++) {
            for (int z = clickBlock.getBlockZ() - radius; z < clickBlock.getBlockZ() + radius; z++) {
                World blockWorld = event.getPlayer().getWorld();
                Location blockLoc = new Location(blockWorld, x, clickBlock.getBlockY(), z);
                if (blockLoc.distance(clickBlock) <= radius)
                    blockWorld.setBiome(x, z, biome);
            }
        }

        event.getPlayer().sendMessage(ChatColor.YELLOW + "Set biome to "
                + ChatColor.AQUA + biome.name().charAt(0) + biome.name().substring(1).toLowerCase()
                + ChatColor.YELLOW + " in a radius of " + ChatColor.AQUA + radius
                + ChatColor.YELLOW + " blocks!");
        event.getPlayer().playSound(event.getPlayer().getLocation(), org.bukkit.Sound.NOTE_PLING, 1f, 24f);
    }

    @Override
    public void onLeftClick(PlayerInteractEvent event) {
        if (!isThisItem(event.getPlayer().getItemInHand())) return;
        NBTItem nbt = new NBTItem(event.getPlayer().getItemInHand());

        int radius = nbt.getInteger("biomestick_radius");

        if (radius < 5) radius++;
        else radius = 1;

        nbt.setInteger("biomestick_radius", radius);
        ItemBase base = new ItemBase(nbt.getItem());
        replaceLore(base);
        event.getPlayer().setItemInHand(base.getStack());
        event.getPlayer().sendMessage(ChatColor.YELLOW + "Radius changed to: " + ChatColor.AQUA + radius + ChatColor.YELLOW + " blocks!");
    }


    @Override
    public String[] toReplace() {
        return new String[]{
                ChatColor.GRAY + "Selected Radius:"
        };
    }

    @Override
    public String[] replaceWith(NBTItem nbtItem) {
        return new String[]{
                ChatColor.GRAY + "Selected Radius: " + ChatColor.AQUA + nbtItem.getInteger("biomestick_radius")
        };
    }
}
