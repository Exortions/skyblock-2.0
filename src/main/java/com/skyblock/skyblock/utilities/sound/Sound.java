package com.skyblock.skyblock.utilities.sound;

import com.skyblock.skyblock.utilities.Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public class Sound {

    private final org.bukkit.Sound sound;
    private final float volume;
    private final float pitch;
    private final long delay;

    public void play(Location location) {
        Util.delay(() -> location.getWorld().playSound(location, sound, volume, pitch), delay);
    }

}
