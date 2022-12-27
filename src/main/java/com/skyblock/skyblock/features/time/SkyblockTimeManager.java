package com.skyblock.skyblock.features.time;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.data.ServerData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkyblockTimeManager {

    private final Skyblock skyblock;

    private final ServerData serverData;
    private final List<String> activeEvents;
    private SkyblockDate currentDate;

    public static final int REAL_SECONDS_PER_SKYBLOCK_MINUTE = 1;
    public static final int TIME_BETWEEN_UPDATE = 10;

    public SkyblockTimeManager(Skyblock skyblock) {
        this.skyblock = skyblock;

        this.serverData = skyblock.getServerData();

        this.activeEvents = new ArrayList<>();

        this.currentDate = new SkyblockDate((String) serverData.get("date.season"), (int) serverData.get("date.day"));

        this.updateInGameTime();

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

        if ((int) serverData.get("date.day") >= 31) {
            serverData.set("date.day", 1);
            serverData.set("date.season", getNextSeason());
        }

        this.currentDate = new SkyblockDate((String) serverData.get("date.season"), (int) serverData.get("date.day"));

        this.updateInGameTime();
        this.updateEvents();
    }

    public void updateEvents() {
        List<String> prev = new ArrayList<>(activeEvents);

        activeEvents.clear();

        int day = currentDate.getDate();
        String season = currentDate.getSeason();

        if (day % 3 == 0) {
            activeEvents.add("dark_auction");
        }

        if (season.equals("fall") && day >= 30) {
            activeEvents.add("spooky_festival");
        } else if (season.equals("winter") && (day == 25 || day == 26)) {
            activeEvents.add("season_of_jerry");
        } else if ((season.equals("summer") && (day == 2 || day == 3)) || (season.equals("winter") && (day == 2 || day == 3))) {
            activeEvents.add("traveling_zoo");
        } else if (season.equals("winter") && (day == 31)) {
            activeEvents.add("new_year_cake_event");
        } else if (season.equals("winter") && (day == 1)) {
            activeEvents.add("jerry_workshop_open");
        }

        if (!prev.equals(activeEvents)) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "NEW EVENT STARTED, ACTIVE EVENTS: " + activeEvents);
        }
    }

    public void updateInGameTime() {
        int gameMinutes = (int) serverData.get("date.minutes");
        int gameHours = gameMinutes / 60;
        gameMinutes = gameMinutes % 60;

        int worldTime = (gameHours * 1000 + gameMinutes * 1000 / 60) % 24000;

        worldTime -= 6000;

        if (worldTime < 0) {
            worldTime += 24000;
        }

        for (World world : skyblock.getServer().getWorlds()) {
            world.setTime(worldTime);
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
        return getNextSeason((String) serverData.get("date.season"));
    }

    public String getNextSeason(String season) {
        List<String> seasons = Arrays.asList("spring", "summer", "fall", "winter");

        int index = seasons.indexOf(season);

        if (index == 3) {
            return seasons.get(0);
        }

        return seasons.get(index + 1);
    }

    public String getLastSeason(String season) {
        List<String> seasons = Arrays.asList("spring", "summer", "fall", "winter");

        int index = seasons.indexOf(season);

        if (index == 0) {
            return seasons.get(3);
        }

        return seasons.get(index - 1);
    }

    public CalendarEvent getNextEvent() {
        int diff = 0;

        String season = currentDate.getSeason();
        int day = currentDate.getDate();
        while (true) {
            day++;
            if (day == 32) {
                season = getNextSeason(season);
                day = 1;
            }

            diff++;

            if (season.equals("fall") && day == 31) {
                return new CalendarEvent(CalendarEvent.EventType.SPOOKY_FESTIVAL, new Timestamp(System.currentTimeMillis() + diff * 120000L));
            } else if (season.equals("winter") && day == 25) {
                return new CalendarEvent(CalendarEvent.EventType.SEASON_OF_JERRY, new Timestamp(System.currentTimeMillis() + diff * 120000L));
            } else if ((season.equals("summer") && day == 2) || (season.equals("winter") && day == 2)) {
                return new CalendarEvent(CalendarEvent.EventType.TRAVELING_ZOO, new Timestamp(System.currentTimeMillis() + diff * 120000L));
            }
        }
    }

    public SkyblockDate getCurrentDate() { return currentDate; }

}
