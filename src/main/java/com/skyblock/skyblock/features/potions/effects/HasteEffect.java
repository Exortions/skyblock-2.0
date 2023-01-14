package com.skyblock.skyblock.features.potions.effects;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.potions.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HasteEffect extends PotionEffect {

    public HasteEffect(SkyblockPlayer player, String ignoredName, int amplifier, double duration, boolean alreadyStarted) {
        super(player, "Haste", amplifier, duration, alreadyStarted, "&7Increases your mining speed by\n&a" + (amplifier * 50) + "%&7.");
    }

    @Override
    public void start() {
        this.getPlayer().getBukkitPlayer().addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(Integer.MAX_VALUE, this.getAmplifier()));
    }

    @Override
    public void end() {
        this.getPlayer().getBukkitPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
    }
}
