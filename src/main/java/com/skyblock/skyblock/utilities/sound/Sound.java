package com.skyblock.skyblock.utilities.sound;

import com.skyblock.skyblock.utilities.Util;
import lombok.AllArgsConstructor;
import org.bukkit.Location;

@AllArgsConstructor
public class Sound {
    private org.bukkit.Sound sound;
    private float volume;
    private float pitch;
    private long delay;

    public void play(Location location) {
        Util.delay(() -> {
            location.getWorld().playSound(location, sound, volume, pitch);
        }, (int) delay);
    }
}
