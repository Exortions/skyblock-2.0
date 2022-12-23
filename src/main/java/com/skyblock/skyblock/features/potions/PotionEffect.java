package com.skyblock.skyblock.features.potions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Pair;
import com.skyblock.skyblock.utilities.Triple;
import com.skyblock.skyblock.utilities.Util;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Data
public abstract class PotionEffect {

    public static final HashMap<String, Triple<Integer, PotionType, ChatColor>> getMaxLevelsAndColors = new HashMap<String, Triple<Integer, PotionType, ChatColor>>() {{
        put("strength", Triple.of(8, PotionType.STRENGTH, ChatColor.DARK_RED));
    }};

    private final SkyblockPlayer player;
    private final String name;
    private final int amplifier;
    private double duration;

    private String description;

    public PotionEffect(SkyblockPlayer player, String name, int amplifier, double duration, boolean alreadyStarted, String description) {
        this.player = player;
        this.name = name;
        this.amplifier = amplifier;
        this.duration = duration;

        this.description = description;

        if (player == null) return;

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (i == 0) {
                    if (!alreadyStarted) start();
                    i++;
                }

                if (getDuration() <= 0) {
                    end();

                    getPlayer().removeEffect(getName());

                    cancel();
                    return;
                }
                tick();

                setDuration(getDuration() - 1);
            }
        }.runTaskTimer(Skyblock.getPlugin(), 0, 1);
    }

    public abstract void start();
    public abstract void tick();
    public abstract void end();

}
