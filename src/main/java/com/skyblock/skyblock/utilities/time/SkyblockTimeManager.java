package com.skyblock.skyblock.utilities.time;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.data.ServerData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class SkyblockTimeManager {

    private final Skyblock skyblock;

    private final ServerData serverData;

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
        }.runTaskTimer(skyblock, 0, 20);
    }

    public void tick() {
        // every 10 seconds, this method will be called
        // 10 seconds in real life = 10 minutes in skyblock
        // 60 minutes in skyblock = 1 hour in skyblock
        // 24 hours in skyblock = 1 day in skyblock (1 day in skyblock = in real life 10 minutes)
        // 30 days in skyblock = 1 season (spring, summer, fall, winter)

        serverData.set("date.minutes", (int) serverData.get("date.minutes") + 10);

        // date.minutes = real life seconds
        // date.minutes = skyblock minutes

        // ampm
        if ((int) serverData.get("date.minutes") >= (60 * 12)) {
            serverData.set("date.minutes", 0);
            serverData.set("date.ampm", serverData.get("date.ampm").equals("am") ? "pm" : "am");
        }

        if ((int) serverData.get("date.minutes") >= (60 * 24)) {
            serverData.set("date.minutes", 0);
            serverData.set("date.ampm", serverData.get("date.ampm").equals("am") ? "pm" : "am");
            serverData.set("date.day", (int) serverData.get("date.day") + 1);
        }

        if ((int) serverData.get("date.day") >= 30) {
            serverData.set("date.day", 1);
            serverData.set("date.season", getNextSeason());
        }

//        skyblock.getServer().broadcastMessage(
//                "It is now " + serverData.get("date.minutes") + " minutes " + serverData.get("date.ampm") + " on day " + serverData.get("date.day") + " of the " + serverData.get("date.season") + " season."
//        );
    }

    public String getTime() {
        int minutes = (int) serverData.get("date.minutes");
        int hours = minutes / 60;

        return "";
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
