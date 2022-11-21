package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.ArmorSet;
import com.skyblock.skyblock.utilities.item.ItemHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        HashMap<SkyblockStat, Integer> extraData = new HashMap();

        skyblockPlayer.forEachStat((s) -> {
            int perc = (int) Math.floor(skyblockPlayer.getStat(s) * 0.1);
            extraData.put(s, perc);
            skyblockPlayer.addStat(s, perc);
        });

        skyblockPlayer.setExtraData("superior_dragon_bonus", extraData);
    }

    @Override
    public void stopFullSetBonus(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        HashMap<SkyblockStat, Integer> extraData = (HashMap<SkyblockStat, Integer>) skyblockPlayer.getExtraData("superior_dragon_bonus");

        skyblockPlayer.forEachStat((s) -> {
            skyblockPlayer.subtractStat(s, extraData.get(s));
        });

        skyblockPlayer.setExtraData("superior_dragon_bonus", new HashMap());
    }
}
