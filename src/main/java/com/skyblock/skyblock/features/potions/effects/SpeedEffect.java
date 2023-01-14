package com.skyblock.skyblock.features.potions.effects;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.potions.PotionEffect;
import com.skyblock.skyblock.utilities.Util;

import java.util.function.Function;

public class SpeedEffect extends PotionEffect {

    private static final Function<Integer, Double> calculateSpeedBonus = (level) -> (double) Util.createFetchableDictionary(level - 1, 5, 10, 15, 20, 25, 30, 35, 40);

    public SpeedEffect(SkyblockPlayer player, String ignoredName, int amplifier, double duration, boolean alreadyStarted) {
        super(player, "Speed", amplifier, duration, alreadyStarted, "&7Grants &a+" + calculateSpeedBonus.apply(amplifier) + " &f" + SkyblockStat.SPEED.getIcon() + " Speed&7.");
    }

    @Override
    public void start() {
        this.getPlayer().addStat(SkyblockStat.SPEED, calculateSpeedBonus.apply(this.getAmplifier()));
    }

    @Override
    public void end() {
        this.getPlayer().subtractStat(SkyblockStat.SPEED, calculateSpeedBonus.apply(this.getAmplifier()));
    }
}
