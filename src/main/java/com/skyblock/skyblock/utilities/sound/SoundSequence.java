package com.skyblock.skyblock.utilities.sound;

import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public class SoundSequence {

    public static final SoundSequence BATPHONE = new SoundSequence(
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 1),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 3),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 5),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 7),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 9),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 18),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 20),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 22),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 24),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 26),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 35),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 37),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 39),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 41),
            new Sound(org.bukkit.Sound.NOTE_PLING, 1f, 26f, 43),
            new Sound(org.bukkit.Sound.WOOD_CLICK, 1f, 1f, 52));

    public static final SoundSequence BOSS_SPAWN = new SoundSequence(
            new Sound(org.bukkit.Sound.WITHER_SHOOT, 1f, 9f, 1),
            new Sound(org.bukkit.Sound.WITHER_SHOOT, 1f, 9f, 4),
            new Sound(org.bukkit.Sound.WITHER_SHOOT, 1f, 5f, 7),
            new Sound(org.bukkit.Sound.WITHER_SHOOT, 1f, 5f, 10),
            new Sound(org.bukkit.Sound.WITHER_SHOOT, 1f, 5f, 13),
            new Sound(org.bukkit.Sound.WITHER_SHOOT, 1f, 5f, 16),
            new Sound(org.bukkit.Sound.WITHER_SHOOT, 1f, 1f, 19),
            new Sound(org.bukkit.Sound.WITHER_SHOOT, 1f, 1f, 22),
            new Sound(org.bukkit.Sound.WITHER_SHOOT, 1f, 1f, 25),
            new Sound(org.bukkit.Sound.WITHER_SPAWN, 1f, -25f, 28),
            new Sound(org.bukkit.Sound.EXPLODE, 1f, 2f, 28));

    private final List<Sound> sounds;

    public SoundSequence(Sound... sounds) {
        this.sounds = Arrays.asList(sounds);
    }

    public void play(Location location) {
        for (Sound sound : sounds) sound.play(location);
    }
}
