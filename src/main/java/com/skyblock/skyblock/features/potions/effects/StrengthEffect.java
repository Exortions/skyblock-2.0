package com.skyblock.skyblock.features.potions.effects;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.potions.PotionEffect;
import com.skyblock.skyblock.utilities.Util;

import java.util.function.Function;

public class StrengthEffect extends PotionEffect {

    private final Function<Integer, Double> getStrengthBonus = (level) -> Util.createFetchableDictionary(level - 1, 5, 12.5, 20, 30, 40, 50, 60, 75);

    public StrengthEffect(SkyblockPlayer player, String name, int amplifier, double duration, boolean alreadyStarted) {
        super(player, "Strength", amplifier, duration, alreadyStarted);
    }

    @Override
    public void start() {
        this.getPlayer().addStat(SkyblockStat.STRENGTH, this.getStrengthBonus.apply(this.getAmplifier()));
    }

    @Override
    public void tick() {}

    @Override
    public void end() {
        this.getPlayer().subtractStat(SkyblockStat.STRENGTH, this.getStrengthBonus.apply(this.getAmplifier()));
    }
}
