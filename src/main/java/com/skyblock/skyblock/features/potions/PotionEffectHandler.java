package com.skyblock.skyblock.features.potions;

import com.skyblock.skyblock.SkyblockPlayer;
import net.citizensnpcs.api.exception.NPCLoadException;
import org.bukkit.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class PotionEffectHandler {

    private final HashMap<String, Class<? extends PotionEffect>> effects = new HashMap<>();

    @SafeVarargs
    public PotionEffectHandler(Class<? extends PotionEffect>... effects) {
        for (Class<? extends PotionEffect> effect : effects) {
            this.effects.put(effect.getSimpleName().replace("Effect", "").toLowerCase(), effect);
        }
    }

    public PotionEffect createEffect(String name, SkyblockPlayer player, int amplifier, double duration, boolean alreadyStarted) {
        try {
            return this.effects.containsKey(name) ? this.effects.get(name).getConstructor(SkyblockPlayer.class, String.class, int.class, double.class, boolean.class).newInstance(player, name, amplifier, duration, alreadyStarted) : null;
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();

            return null;
        }
    }

    public PotionEffect effect(SkyblockPlayer player, String name, int amplifier, double duration, boolean alreadyStarted) {
        PotionEffect effect = this.createEffect(name.toLowerCase(), player, amplifier, duration, alreadyStarted);

        if (effect == null) return null;

        player.addEffect(effect);

        return effect;
    }

}
