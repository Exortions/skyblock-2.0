package com.skyblock.skyblock.features.slayer.miniboss;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.features.slayer.miniboss.revenant.DeformedRevenant;
import com.skyblock.skyblock.features.slayer.miniboss.revenant.RevenantChampion;
import com.skyblock.skyblock.features.slayer.miniboss.revenant.RevenantSycophant;
import com.skyblock.skyblock.features.slayer.miniboss.sven.PackEnforcer;
import com.skyblock.skyblock.features.slayer.miniboss.sven.SvenAlpha;
import com.skyblock.skyblock.features.slayer.miniboss.sven.SvenFollower;
import com.skyblock.skyblock.features.slayer.miniboss.tarantula.MutantTarantula;
import com.skyblock.skyblock.features.slayer.miniboss.tarantula.TarantulaBeast;
import com.skyblock.skyblock.features.slayer.miniboss.tarantula.TarantulaVermin;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public abstract class SlayerMiniboss extends SkyblockEntity {

    protected final Player spawner;

    public SlayerMiniboss(EntityType type, Player spawner) {
        super(type);

        this.spawner = spawner;
    }

    @Override
    public Entity spawn(Location location) {
        spawner.playEffect(location, Effect.EXPLOSION_LARGE, 10);
        spawner.playSound(location, Sound.EXPLODE, 10, 1);

        Util.delay(() -> {
            spawner.playSound(location, Sound.EXPLODE, 10, 1);
            spawner.playEffect(location, Effect.EXPLOSION_LARGE, 10);
        }, 4);

        Util.delay(() -> {
            spawner.playSound(location, Sound.EXPLODE, 10, 1);
            spawner.playEffect(location, Effect.EXPLOSION_LARGE, 10);
        }, 8);

        return super.spawn(location);
    }

    public static SlayerMiniboss getMiniBoss(Player player, int level, double combatXP, SlayerType type) {
        switch (type) {
            case REVENANT:
                if (level == 3 && Util.random(0, 12) == 0) return new RevenantSycophant(player);
                if (level == 4 && Util.random(0, 12) == 0) return new RevenantChampion(player);
                if (level == 4 && Util.random(0, 20) == 0) return new DeformedRevenant(player);
                break;
            case SVEN:
                if (level == 3 && Util.random(0, 12) == 0) return new PackEnforcer(player);
                if (level == 4 && Util.random(0, 12) == 0) return new SvenFollower(player);
                if (level == 4 && Util.random(0, 20) == 0) return new SvenAlpha(player);
                break;
            case TARANTULA:
                if (level == 3 && Util.random(0, 12) == 0) return new TarantulaVermin(player);
                if (level == 4 && Util.random(0, 12) == 0) return new TarantulaBeast(player);
                if (level == 4 && Util.random(0, 20) == 0) return new MutantTarantula(player);
                break;
        }

        return null;
    }
}
