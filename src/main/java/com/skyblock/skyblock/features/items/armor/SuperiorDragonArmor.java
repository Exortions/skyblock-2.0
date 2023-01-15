package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.ArmorSet;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SuperiorDragonArmor extends ArmorSet {

    public SuperiorDragonArmor() {
        super(handler.getItem("SUPERIOR_DRAGON_HELMET.json"),
            handler.getItem("SUPERIOR_DRAGON_CHESTPLATE.json"),
            handler.getItem("SUPERIOR_DRAGON_LEGGINGS.json"),
            handler.getItem("SUPERIOR_DRAGON_BOOTS.json"), "superior_dragon_armor");
    }

    @Override
    public void fullSetBonus(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        skyblockPlayer.forEachStat((s) -> {
            if (!s.equals(SkyblockStat.HEALTH)) skyblockPlayer.addStatMultiplier(s, 0.1);
        });
    }

    @Override
    public void stopFullSetBonus(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        skyblockPlayer.forEachStat((s) -> {
            if (!s.equals(SkyblockStat.HEALTH)) skyblockPlayer.subtractStatMultiplier(s, 0.1);
        });
    }

    @Override
    public void tick(Player player) {}
}
