package com.skyblock.skyblock.features.enchantment.enchantments;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.enchantment.types.ArmorEnchantment;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.function.Function;

public class GrowthEnchantment extends ArmorEnchantment {
    public GrowthEnchantment() {
        super("growth", "Growth", (level) -> {
            String description = ChatColor.GRAY + "Grants " + ChatColor.GREEN + "+%s" + ChatColor.RED + " ❤ Health";
            return String.format(description, level * 15);
        }, 6);
    }

    @Override
    public void onEnchant(ItemBase base) {
        int level = base.getEnchantment(this.getName()).getLevel();
        base.setHealth(base.getHealth() + level * 15);
    }

    @Override
    public void onUnEnchant(ItemBase base) {
        int level = base.getEnchantment(this.getName()).getLevel();
        base.setHealth(base.getHealth() - level * 15);
    }
}
