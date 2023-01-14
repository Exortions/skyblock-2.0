package com.skyblock.skyblock.features.items.tools;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerItemHeldChangeEvent;
import com.skyblock.skyblock.features.items.ListeningItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class EfficientAxe extends ListeningItem {

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

    @EventHandler
    public void onItemChange(SkyblockPlayerItemHeldChangeEvent e) {
        if (isThisItem(e.getNewItem())) {
            for (int i = 0; i < 4; i++) {
                changeDrop(e.getPlayer(), Material.LOG, (byte) i);
            }

            for (int i = 0; i < 2; i++) {
                changeDrop(e.getPlayer(), Material.LOG_2, (byte) i);
            }
        } else if (isThisItem(e.getOldItem())) {
            HashMap<String, ItemStack[]> dropOverrides = (HashMap<String, ItemStack[]>) e.getPlayer().getExtraData("dropOverrides");

            for (int i = 0; i < 40; i++) {
                dropOverrides.remove("LOG:" + i);
            }

            for (int i = 0; i < 40; i++) {
                dropOverrides.remove("LOG_2:" + i);
            }

            e.getPlayer().setExtraData("dropOverrides", dropOverrides);
        }
    }

    private void changeDrop(SkyblockPlayer player, Material mat, byte data) {
        short durability = 0;
        HashMap<String, ItemStack[]> dropOverrides = (HashMap<String, ItemStack[]>) player.getExtraData("dropOverrides");

        if (mat.equals(Material.LOG)) {
            durability = log.get(data);
        } else if (mat.equals(Material.LOG_2)) {
            durability = log_2.get(data);
        }

        int add = (mat.equals(Material.LOG) ? 4 : 2);
        for (int i = 0; i < 20; i++) {
            dropOverrides.put(mat.name() + ":" + data, new ItemStack[]{new ItemStack(Material.WOOD, 5, durability)});
            data += add;
        }
        player.setExtraData("dropOverrides", dropOverrides);
    }
}
