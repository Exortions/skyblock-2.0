package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.ArmorSet;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class EmeraldArmor extends ArmorSet {
    public EmeraldArmor() {
        super("EMERALD_ARMOR_HELMET", "EMERALD_ARMOR_CHESTPLATE", "EMERALD_ARMOR_LEGGINGS", "EMERALD_ARMOR_BOOTS", "emerald_armor");
    }

    HashMap<String, Double> bonus = new HashMap<>();

    @Override
    public void fullSetBonus(Player player) {
        super.fullSetBonus(player);
        SkyblockPlayer sbp = SkyblockPlayer.getPlayer(player);
        Double bonusval = Math.floor(sbp.getIntValue("collection.emerald.exp") / 3000);
        bonus.put(player.getName(), bonusval);
        sbp.addStat(SkyblockStat.DEFENSE, bonusval);
        sbp.addStat(SkyblockStat.MAX_HEALTH, bonusval);
    }

    @Override
    public void stopFullSetBonus(Player player) {
        super.fullSetBonus(player);
        SkyblockPlayer sbp = SkyblockPlayer.getPlayer(player);
        Double bonusval = bonus.get(player.getName());
        sbp.subtractStat(SkyblockStat.DEFENSE, bonusval);
        sbp.subtractStat(SkyblockStat.MAX_HEALTH, bonusval);
    }
}
