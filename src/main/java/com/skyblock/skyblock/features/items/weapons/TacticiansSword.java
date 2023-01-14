package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockPlayerCollectItemEvent;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.items.DynamicLore;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TacticiansSword extends ListeningItem implements DynamicLore {

    public TacticiansSword() {
        super (plugin.getItemHandler().getItem("TACTICIAN_SWORD.json"), "tacticians_sword");
    }

    @Override
    public void onRegenerate(ItemBase item) {
        item.setDamage(50 + item.getReforge().getReforgeData(item.getRarityEnum()).get(SkyblockStat.DAMAGE));

        int bonus = getBonus(new NBTItem(item.getStack()));
        item.setDamage(item.getDamage() + bonus);

        replaceLore(item);
    }

    @Override
    public String[] toReplace() {
        return new String[] {
                ChatColor.GRAY + "Your Collections: ",
        };
    }

    @Override
    public String[] replaceWith(NBTItem nbtItem) {
        int bonus = getBonus(nbtItem);

        return new String[] {
                ChatColor.GRAY + "Your Collections: " + ChatColor.YELLOW + (bonus / 15) + ChatColor.GRAY + "/" + ChatColor.YELLOW + "10",
        };
    }

    private int getBonus(NBTItem nbt) {
        int collections = nbt.getInteger("tacticians_sword.collections");

        return collections * 15;
    }

    @EventHandler
    public void onCollectItem(SkyblockPlayerCollectItemEvent event) {
        SkyblockPlayer player = event.getPlayer();

        List<String> combatCollections = new ArrayList<String>() {{
            add("Blaze Rod");
            add("Bone");
            add("Ender Pearl");
            add("Ghast Tear");
            add("Gunpowder");
            add("Magma Cream");
            add("Rotten Flesh");
            add("Slimeball");
            add("Spider Eye");
            add("String");
        }};

        int totalCollections = 0;

        for (Collection collection : Collection.getCollections()) {
            if (!combatCollections.contains(collection.getName())) continue;
            int levelOfCollection = (int) player.getValue("collection." + event.getCollection().getName().toLowerCase().replace("_", " ") + ".level");
            if (levelOfCollection < 7) continue;

            totalCollections++;
        }

        for (ItemStack item : player.getBukkitPlayer().getInventory().getContents()) {
            if (!Util.notNull(item)) continue;
            if (!Util.getSkyblockId(item).equals("tactician_sword")) continue;

            try {
                NBTItem nbt = new NBTItem(item);

                nbt.setInteger("tacticians_sword.collections", totalCollections);
                ItemBase base = new ItemBase(nbt.getItem());

                replaceLore(base);

                event.getPlayer().getBukkitPlayer().getInventory().removeItem(item);
                event.getPlayer().getBukkitPlayer().getInventory().addItem(base.getStack());
            } catch (Exception ignored) {}
        }
    }
}
