package com.skyblock.skyblock.features.time;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.data.ServerData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class SkyblockTimeManager {

    private final Skyblock skyblock;

    private final ServerData serverData;

    public static final int REAL_SECONDS_PER_SKYBLOCK_MINUTE = 1;
    public static final int TIME_BETWEEN_UPDATE = 10;

    public SkyblockTimeManager(Skyblock skyblock) {
        this.skyblock = skyblock;

        this.serverData = skyblock.getServerData();

        this.registerTask();
    }

    public void registerTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(skyblock, (20 * REAL_SECONDS_PER_SKYBLOCK_MINUTE) * TIME_BETWEEN_UPDATE, (20 * REAL_SECONDS_PER_SKYBLOCK_MINUTE) * TIME_BETWEEN_UPDATE);
    }

    public void tick() {
        serverData.set("date.minutes", (int) serverData.get("date.minutes") + 10);

        int minutes = (int) serverData.get("date.minutes");

        if (minutes >= (60 * 24)) {
            serverData.set("date.minutes", 0);
            serverData.set("date.day", (int) serverData.get("date.day") + 1);
        }

        if ((int) serverData.get("date.day") >= 30) {
            serverData.set("date.day", 1);
            serverData.set("date.season", getNextSeason());
        }

        int realLifeSeconds = minutes / REAL_SECONDS_PER_SKYBLOCK_MINUTE * TIME_BETWEEN_UPDATE;
        int realLifeMinutes = realLifeSeconds / 60;
        int realLifeHours = realLifeMinutes / 24;

        int inGameTime = ((realLifeHours * 1000) + (realLifeMinutes * 100) / 5994) % 24000;
        if (inGameTime < 0) inGameTime += 24000;

        inGameTime -= 6000;

        for (World world : skyblock.getServer().getWorlds()) {
            world.setTime(inGameTime);
        }
    }

    public String getTime() {
        int minutes = (int) serverData.get("date.minutes");
        int hours = minutes / 60;
        minutes = minutes % 60;

        String ampm = "am";
        if (hours >= 12) {
            ampm = "pm";
            hours -= 12;
        }

        if (hours == 0) hours = 12;

        return String.format("%02d:%02d%s", hours, minutes, ampm);
    }

    public String getDate() {
        return StringUtils.capitalize(serverData.get("date.season") + " " + Util.ordinalSuffixOf((int) serverData.get("date.day")));
    }

    public String getIcon() {
        int minutes = (int) serverData.get("date.minutes");
        int hours = minutes / 60;

        if (hours >= 6 && hours < 18) {
            return ChatColor.YELLOW +  "☀";
        } else {
            return ChatColor.AQUA + "☽";
        }
    }

    public String getNextSeason() {
        List<String> seasons = Arrays.asList("spring", "summer", "fall", "winter");

        int index = seasons.indexOf(serverData.get("date.season"));

        if (index == 3) {
            return seasons.get(0);
        }

        return seasons.get(index + 1);
    }

}
