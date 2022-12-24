package com.skyblock.skyblock.features.potions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Triple;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

@Data
public abstract class PotionEffect {

    public static final HashMap<String, Triple<Integer, PotionType, ChatColor>> getMaxLevelsAndColors = new HashMap<String, Triple<Integer, PotionType, ChatColor>>() {{
        put("speed", Triple.of(8, PotionType.SPEED, ChatColor.DARK_BLUE));
        put("jump_boost", Triple.of(4, PotionType.JUMP, ChatColor.AQUA));
        put("healing", Triple.of(8, PotionType.INSTANT_HEAL, ChatColor.RED));
        put("poison", Triple.of(4, PotionType.POISON, ChatColor.DARK_GREEN));
        put("water_breathing", Triple.of(6, PotionType.WATER_BREATHING, ChatColor.DARK_BLUE));
        put("fire_resistance", Triple.of(1, PotionType.FIRE_RESISTANCE, ChatColor.RED));
        put("night_vision", Triple.of(1, PotionType.NIGHT_VISION, ChatColor.DARK_PURPLE));
        put("strength", Triple.of(8, PotionType.STRENGTH, ChatColor.DARK_RED));
        put("invisibility", Triple.of(1, PotionType.INVISIBILITY, ChatColor.DARK_GRAY));
        put("regeneration", Triple.of(8, PotionType.REGEN, ChatColor.DARK_RED));
        put("weakness", Triple.of(8, PotionType.WEAKNESS, ChatColor.GRAY));
        put("slowness", Triple.of(8, PotionType.SLOWNESS, ChatColor.GRAY));
        put("damage", Triple.of(8, PotionType.INSTANT_DAMAGE, ChatColor.DARK_RED));
        put("haste", Triple.of(4, PotionType.FIRE_RESISTANCE, ChatColor.YELLOW));
        put("rabbit", Triple.of(6, PotionType.JUMP, ChatColor.GREEN));
    }};

    private final SkyblockPlayer player;
    private final String name;
    private final int amplifier;
    private double duration;

    private String description;

    private boolean active;

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
                    active = true;

                    if (!alreadyStarted) start();
                    i++;
                }

                if (getDuration() <= 0 || !active) {
                    end();

                    getPlayer().removeEffect(getName());

                    cancel();

                    active = false;
                    return;
                }
                tick();

                setDuration(getDuration() - 1);
            }
        }.runTaskTimer(Skyblock.getPlugin(), 0, 1);
    }

    public void start() {}
    public void tick() {}
    public void end() {}

}
