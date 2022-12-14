package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.ArmorSet;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WiseDragonArmor extends ArmorSet {
    public WiseDragonArmor() {
        super(handler.getItem("WISE_DRAGON_HELMET.json"),
                handler.getItem("WISE_DRAGON_CHESTPLATE.json"),
                handler.getItem("WISE_DRAGON_LEGGINGS.json"),
                handler.getItem("WISE_DRAGON_BOOTS.json"), "wise_dragon_armor");
    }

    @Override
    public void fullSetBonus(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        skyblockPlayer.setExtraData("wise_dragon_bonus", true);
    }

    @Override
    public void stopFullSetBonus(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        skyblockPlayer.setExtraData("wise_dragon_bonus", false);
    }

    @Override
    public void tick(Player player) {
        
    }
}
