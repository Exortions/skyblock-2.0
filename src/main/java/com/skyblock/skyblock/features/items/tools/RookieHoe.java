package com.skyblock.skyblock.features.items.tools;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class RookieHoe extends SkyblockItem {
    public RookieHoe() {
        super(plugin.getItemHandler().getItem("ROOKIE_HOE.json"), "rookie_hoe");
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (!type.equals(Material.CARROT) &&
            !type.equals(Material.POTATO) &&
            !type.equals(Material.CROPS)) return;

        if (Util.random(0, 1) == 0) {
            SkyblockPlayer.getPlayer(event.getPlayer()).dropItem(new ItemStack(Material.SEEDS), event.getBlock().getLocation());
        }
    }
}
