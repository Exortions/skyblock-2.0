package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.ArmorSet;
import com.skyblock.skyblock.features.items.BlockHelmetSet;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class LapisArmor extends BlockHelmetSet {
    public LapisArmor() {
        super("LAPIS_ARMOR_HELMET", "LAPIS_ARMOR_CHESTPLATE",
                "LAPIS_ARMOR_LEGGINGS", "LAPIS_ARMOR_BOOTS", "lapis_armor");
    }

    @Override
    public void fullSetBonus(Player player) {
        SkyblockPlayer.getPlayer(player).addStat(SkyblockStat.MAX_HEALTH, 60);
    }

    @Override
    public void stopFullSetBonus(Player player) {
        SkyblockPlayer.getPlayer(player).subtractStat(SkyblockStat.MAX_HEALTH, 60);
    }
}
