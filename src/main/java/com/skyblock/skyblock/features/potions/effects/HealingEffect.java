package com.skyblock.skyblock.features.potions.effects;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.potions.PotionEffect;
import com.skyblock.skyblock.utilities.Util;

import java.util.function.Function;

public class HealingEffect extends PotionEffect {

    private static final Function<Integer, Integer> calculateHealingBonus = (level) -> (int) Util.createFetchableDictionary(level - 1, 20, 50, 100, 150, 200, 300, 400, 500);

    public HealingEffect(SkyblockPlayer player, String ignoredName, int amplifier, double duration, boolean alreadyStarted) {
        super(player, "Healing", amplifier, duration, alreadyStarted, "&7Instantly heals for &a+" + calculateHealingBonus.apply(amplifier) + " HP&7.");
    }

    @Override
    public void start() {
        this.getPlayer().setStat(SkyblockStat.HEALTH, Math.min(this.getPlayer().getStat(SkyblockStat.HEALTH) + calculateHealingBonus.apply(this.getAmplifier()), this.getPlayer().getStat(SkyblockStat.MAX_HEALTH)));

        this.setActive(false);
    }
}
