package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockEntityDeathEvent;
import com.skyblock.skyblock.features.items.DynamicLore;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class RaiderAxe extends ListeningItem implements DynamicLore {
    public RaiderAxe() {
        super(plugin.getItemHandler().getItem("RAIDER_AXE.json"), "raider_axe");
    }

    @EventHandler
    public void onKill(SkyblockEntityDeathEvent e) {
        ItemStack inHand = e.getKiller().getBukkitPlayer().getItemInHand();
        try {
            ItemBase item = new ItemBase(inHand);

            if (item.getSkyblockId().equals("raider_axe")) {
                NBTItem nbtItem = new NBTItem(inHand);
                int kills = nbtItem.getInteger("raider_axe_kills");

                nbtItem.setInteger("raider_axe_kills", kills + 1);

                item = new ItemBase(nbtItem.getItem());

                item.setDamage((int) (80 + item.getReforge().getReforgeData(item.getRarityEnum()).get(SkyblockStat.DAMAGE) + (Math.floor((kills + 1) / 500f))));
                item.setStrength(50 + item.getReforge().getReforgeData(item.getRarityEnum()).get(SkyblockStat.STRENGTH));

                replaceLore(item);

                e.getKiller().getBukkitPlayer().setItemInHand(item.getStack());
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public String[] toReplace() {
        return new String[]{
                ChatColor.GRAY + "Kills: ",
                ChatColor.GRAY + "Wood collections: "
        };
    }

    @Override
    public String[] replaceWith(NBTItem nbtItem) {
        int kills = nbtItem.getInteger("raider_axe_kills");
        int coll = nbtItem.getInteger("raider_axe_collection");
        return new String[]{
                ChatColor.GRAY + "Kills: " + ChatColor.RED + kills,
                ChatColor.GRAY + "Wood collections: " + ChatColor.RED + coll
        };
    }
}
