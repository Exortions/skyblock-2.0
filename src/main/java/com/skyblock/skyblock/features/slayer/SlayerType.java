package com.skyblock.skyblock.features.slayer;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.slayer.boss.RevenantHorror;
import com.skyblock.skyblock.features.slayer.boss.SvenPackmaster;
import com.skyblock.skyblock.features.slayer.boss.TarantulaBroodfather;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public enum SlayerType {

    REVENANT(RevenantHorror.class),
    TARANTULA(TarantulaBroodfather.class),
    SVEN(SvenPackmaster.class);

    final Class<? extends SlayerBoss> baseClass;

    SlayerType(Class<? extends SlayerBoss> clazz) {
        baseClass = clazz;
    }

    public SlayerBoss getNewInstance(Player player, Integer level) {
        try {
            return baseClass.getConstructor(Player.class, Integer.class).newInstance(player, level);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

}
