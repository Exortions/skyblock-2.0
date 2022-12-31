package com.skyblock.skyblock.features.items.tools;

import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PromisingTool extends SkyblockItem {
    public PromisingTool(ItemStack baseItem, String internalName) {
        super(baseItem, internalName);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent e) {
        ItemStack item = e.getPlayer().getItemInHand();

        if (item == null) return;

        NBTItem nbt = new NBTItem(item);

        if (nbt.getString("skyblockId").equals(getInternalName())) {
            nbt.setInteger("blocksBroken", nbt.getInteger("blocksBroken") + 1);
            e.getPlayer().setItemInHand(nbt.getItem());

            try {
                ItemBase base = new ItemBase(e.getPlayer().getItemInHand());
                List<String> lore = base.getDescription();

                int blocks = nbt.getInteger("blocksBroken");
                boolean noEnchantment = blocks < 50;

                for (String line : lore) {
                    String stripColor = ChatColor.stripColor(line);
                    if (stripColor.startsWith("Will gain") && 250 - blocks >= 0)
                        lore.set(lore.indexOf(line), ChatColor.GRAY + "Will gain " + ChatColor.BLUE + "Efficiency " + (noEnchantment ? "I" : "II") + ChatColor.GRAY + " after");

                    if (stripColor.startsWith("breaking") && 250 - blocks >= 0)
                        lore.set(lore.indexOf(line), ChatColor.GRAY + "breaking " + ChatColor.GREEN + (noEnchantment ? 50 - blocks : 250 - blocks) + ChatColor.GRAY + " more blocks.");
                }

                base.setDescription(lore);

                if (blocks == 250) {
                    base.setEnchantment("efficiency", 2);
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Your " + base.getName() + " upgraded to " + ChatColor.BLUE + "Efficiency II");
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 2);
                } else if (blocks == 50) {
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Your " + base.getName() + " upgraded to " + ChatColor.BLUE + "Efficiency I");
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 2);
                    base.setEnchantment("efficiency", 1);
                }

                base.setStack(null);
                e.getPlayer().setItemInHand(base.createStack());
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
