package com.skyblock.skyblock.features.items.tools;

import com.sk89q.worldedit.blocks.BlockData;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import com.sun.org.apache.bcel.internal.generic.DUP;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class EfficientAxe extends SkyblockItem {

    private static final HashMap<Byte, Byte> log = new HashMap<Byte, Byte>() {{
        put((byte) 0, (byte) 0);
        put((byte) 1, (byte) 1);
        put((byte) 2, (byte) 2);
        put((byte) 3, (byte) 3);
    }};

    private static final HashMap<Byte, Byte> log_2 = new HashMap<Byte, Byte>() {{
        put((byte) 0, (byte) 4);
        put((byte) 1, (byte) 5);
    }};

    public EfficientAxe() {
        super(plugin.getItemHandler().getItem("EFFICIENT_AXE.json"), "efficient_axe");
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        byte durability = 0;
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(event.getPlayer());
        HashMap<String, ItemStack[]> dropOverrides = (HashMap<String, ItemStack[]>) skyblockPlayer.getExtraData("dropOverrides");
        byte data = event.getBlock().getData();

        if (event.getBlock().getType().equals(Material.LOG)) {
            durability = log.get((byte) (data - 4));
        } else if (event.getBlock().getType().equals(Material.LOG_2)) {
            durability = log_2.get((byte) (data - 2));
        }

        dropOverrides.put(event.getBlock().getType() + ":" + data, new ItemStack[]{ new ItemStack(Material.WOOD, 5, durability) });
        skyblockPlayer.setExtraData("dropOverrides", dropOverrides);

        Util.delay(() -> {
            dropOverrides.remove(event.getBlock().getType() + ":" + data);
            skyblockPlayer.setExtraData("dropOverrides", dropOverrides);
        }, 1);
    }
}
