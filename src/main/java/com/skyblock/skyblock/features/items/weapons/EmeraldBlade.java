package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.event.SkyblockCoinsChangeEvent;
import com.skyblock.skyblock.features.items.DynamicLore;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class EmeraldBlade extends ListeningItem implements DynamicLore {
    public EmeraldBlade() {
        super(plugin.getItemHandler().getItem("EMERALD_BLADE.json"), "emerald_blade");
    }

    @EventHandler
    public void onCoin(SkyblockCoinsChangeEvent e) {
        for (ItemStack item : e.getPlayer().getBukkitPlayer().getInventory().getContents()) {
            if (!Util.notNull(item)) continue;
            if (!Util.getSkyblockId(item).equals(getInternalName())) continue;

            try {
                NBTItem nbt = new NBTItem(item);
                nbt.setInteger("emeraldblade_damage", (int) Math.floor(2.5 * Math.pow(e.getPlayer().getCoins(), 1.0 / 4.0)));

                ItemBase base = new ItemBase(nbt.getItem());
                base.setDamage(130 + base.getReforge().getReforgeData(base.getRarityEnum()).get(SkyblockStat.DAMAGE) + nbt.getInteger("emeraldblade_damage"));

                replaceLore(base);
                e.getPlayer().getBukkitPlayer().getInventory().remove(item);
                e.getPlayer().getBukkitPlayer().getInventory().addItem(base.getStack());
            } catch (IllegalArgumentException ignored) { }
        }
    }

    @Override
    public String[] toReplace() {
        return new String[] {
                ChatColor.GRAY + "Current Damage Bonus:"
        };
    }

    @Override
    public String[] replaceWith(NBTItem nbtItem) {
        return new String[] {
                ChatColor.GRAY + "Current Damage Bonus: " + ChatColor.GREEN + nbtItem.getInteger("emeraldblade_damage")
        };
    }
}
