package com.skyblock.skyblock.features.items.tools;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class SweetAxe extends SkyblockItem {
    public SweetAxe() {
        super(plugin.getItemHandler().getItem("SWEET_AXE.json"), "sweet_axe");
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.LOG) || event.getBlock().getType().equals(Material.LOG_2)) {
            if (Util.random(0, 5) == 0) {
                SkyblockPlayer.getPlayer(event.getPlayer()).dropItem(new ItemStack(Material.APPLE), event.getBlock().getLocation());
            }
        }
    }
}
