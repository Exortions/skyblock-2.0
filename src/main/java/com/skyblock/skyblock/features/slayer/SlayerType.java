package com.skyblock.skyblock.features.slayer;

import com.skyblock.skyblock.features.slayer.boss.RevenantHorror;
import com.skyblock.skyblock.features.slayer.boss.SvenPackmaster;
import com.skyblock.skyblock.features.slayer.boss.TarantulaBroodfather;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@AllArgsConstructor
@Getter
public enum SlayerType {

    REVENANT(RevenantHorror.class, "Revenant Horror", "Zombie"),
    TARANTULA(TarantulaBroodfather.class, "Tarantula Broodfather", "Spider"),
    SVEN(SvenPackmaster.class, "Sven Packmaster", "Wolf");

    final Class<? extends SlayerBoss> baseClass;
    final String name;
    final String alternative;

    public SlayerBoss getNewInstance(Player player, Integer level) {
        try {
            return baseClass.getConstructor(Player.class, Integer.class).newInstance(player, level);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

}
