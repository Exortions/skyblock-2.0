package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.DynamicLore;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class MidasSword extends SkyblockItem implements DynamicLore {
    public MidasSword() {
        super(plugin.getItemHandler().getItem("MIDAS_SWORD.json"), "midas_sword");
    }

    @Override
    public void onRegenerate(ItemBase item) {
        item.setDamage(150 + item.getReforge().getReforgeData(item.getRarityEnum()).get(SkyblockStat.DAMAGE));
        item.setStrength(item.getReforge().getReforgeData(item.getRarityEnum()).get(SkyblockStat.STRENGTH));

        int bonus = getBonus(new NBTItem(item.getStack()));
        item.setStrength(item.getStrength() + bonus);
        item.setDamage(item.getDamage() + bonus);

        replaceLore(item);
    }

    @Override
    public String[] toReplace() {
        return new String[] {
                ChatColor.GRAY + "Price paid: ",
                ChatColor.GRAY + "Strength Bonus: ",
                ChatColor.GRAY + "Damage Bonus: "
        };
    }

    @Override
    public String[] replaceWith(NBTItem nbtItem) {
        int pricePaid = nbtItem.getInteger("midas_sword_price_paid");
        int bonus = getBonus(nbtItem);

        return new String[] {
                ChatColor.GRAY + "Price paid: " + ChatColor.GOLD + Util.formatInt(pricePaid) + " Coins",
                ChatColor.GRAY + "Strength Bonus: " + ChatColor.RED + bonus,
                ChatColor.GRAY + "Damage Bonus: " + ChatColor.RED + bonus
        };
    }

    private int getBonus(NBTItem nbtItem) {
        int pricePaid = nbtItem.getInteger("midas_sword_price_paid");
        int bonus;

        if (pricePaid < 1_000_000) bonus = (pricePaid / 50_000);
        else if (pricePaid < 2_500_000) bonus = ((pricePaid - 1_000_000) / 100_000);
        else if (pricePaid < 7_500_000) bonus = ((pricePaid - 2_500_000) / 200_000);
        else if (pricePaid < 25_000_000) bonus = ((pricePaid - 7_500_000) / 500_000);
        else if (pricePaid < 50_000_000) bonus = ((pricePaid - 25_000_000) / 1_000_000);
        else bonus = 120;

        return bonus;
    }
}
