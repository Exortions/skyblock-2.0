package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.ArmorSet;
import org.bukkit.entity.Player;

public class SpeedsterArmor extends ArmorSet {
    public SpeedsterArmor() {
        super("SPEEDSTER_HELMET", "SPEEDSTER_CHESTPLATE", "SPEEDSTER_LEGGINGS", "SPEEDSTER_BOOTS", "speedster_armor");
    }

    @Override
    public void fullSetBonus(Player player) {
        super.fullSetBonus(player);
        SkyblockPlayer.getPlayer(player).addStat(SkyblockStat.SPEED, 20);
    }

    @Override
    public void stopFullSetBonus(Player player) {
        super.fullSetBonus(player);
        SkyblockPlayer.getPlayer(player).subtractStat(SkyblockStat.SPEED, 20);
    }
}
