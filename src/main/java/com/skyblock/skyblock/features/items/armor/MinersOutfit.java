package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.ArmorSet;
import org.bukkit.entity.Player;

public class MinersOutfit extends ArmorSet {

    public MinersOutfit() {
        super("MINERS_OUTFIT_HELMET.json", "MINERS_OUTFIT_CHESTPLATE.json", "MINERS_OUTFIT_LEGGINGS.json", "MINERS_OUTFIT_BOOTS.json", "miners_outfit");
    }

    @Override
    public void fullSetBonus(Player player) {
        super.fullSetBonus(player);
    }

    @Override
    public void stopFullSetBonus(Player player) {
        super.stopFullSetBonus(player);
    }

    @Override
    public void tick(Player player) {
        player.sendMessage("Miners Outfit is ticking");

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        skyblockPlayer.addEffect(Skyblock.getPlugin().getPotionEffectHandler().effect(skyblockPlayer, "haste", 2, 20, true));
    }
}
