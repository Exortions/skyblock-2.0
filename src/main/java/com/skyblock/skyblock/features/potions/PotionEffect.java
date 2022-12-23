package com.skyblock.skyblock.features.potions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import lombok.Data;
import org.bukkit.scheduler.BukkitRunnable;

@Data
public abstract class PotionEffect {

    private final SkyblockPlayer player;
    private final String name;
    private final int amplifier;
    private double duration;

    public PotionEffect(SkyblockPlayer player, String name, int amplifier, double duration, boolean alreadyStarted) {
        this.player = player;
        this.name = name;
        this.amplifier = amplifier;
        this.duration = duration;

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
