package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.ArmorSet;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YoungDragonArmor extends ArmorSet {

    private static final List<Player> speedBonuses = new ArrayList<>();
    public YoungDragonArmor() {
        super(handler.getItem("YOUNG_DRAGON_HELMET.json"),
                handler.getItem("YOUNG_DRAGON_CHESTPLATE.json"),
                handler.getItem("YOUNG_DRAGON_LEGGINGS.json"),
                handler.getItem("YOUNG_DRAGON_BOOTS.json"), "young_dragon_armor");
    }

    @Override
    public void fullSetBonus(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        skyblockPlayer.setExtraData("young_dragon_bonus", true);
    }

    @Override
    public void stopFullSetBonus(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        skyblockPlayer.setExtraData("young_dragon_bonus", false);
    }

    @Override
    public void onStatChange(Player player, SkyblockStat stat) {
        if (!stat.equals(SkyblockStat.HEALTH) && !stat.equals(SkyblockStat.MAX_HEALTH)) return;

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (speedBonuses.contains(player)) {
            skyblockPlayer.subtractStat(SkyblockStat.SPEED, 70);
            speedBonuses.remove(player);
        }

        if (skyblockPlayer.getStat(SkyblockStat.HEALTH) >= skyblockPlayer.getStat(SkyblockStat.MAX_HEALTH)) {
            skyblockPlayer.addStat(SkyblockStat.SPEED, 70);
            speedBonuses.add(player);
        }
    }

    @Override
    public void tick(Player player) { }
}
