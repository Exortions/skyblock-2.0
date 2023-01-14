package com.skyblock.skyblock.features.potions.effects;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.potions.PotionEffect;
import com.skyblock.skyblock.utilities.Util;

import java.util.function.Function;

public class StrengthEffect extends PotionEffect {

    private static final Function<Integer, Double> getStrengthBonus = (level) -> Util.createFetchableDictionary(level - 1, 5.0, 12.5, 20.0, 30.0, 40.0, 50.0, 60.0, 75.0);

    public StrengthEffect(SkyblockPlayer player, String ignoredName, int amplifier, double duration, boolean alreadyStarted) {
        super(player, "Strength", amplifier, duration, alreadyStarted,
                "Increases &c" + SkyblockStat.STRENGTH.getIcon() + " Strength &7by\n&a" + getStrengthBonus.apply(amplifier) + "&7.");
    }

    @Override
    public void start() {
        this.getPlayer().addStat(SkyblockStat.STRENGTH, getStrengthBonus.apply(this.getAmplifier()));
    }

    @Override
    public void end() {
        this.getPlayer().subtractStat(SkyblockStat.STRENGTH, getStrengthBonus.apply(this.getAmplifier()));
    }
}
