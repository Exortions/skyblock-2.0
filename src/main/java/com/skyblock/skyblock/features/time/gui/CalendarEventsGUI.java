package com.skyblock.skyblock.features.time.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.time.CalendarEvent;
import com.skyblock.skyblock.features.time.SkyblockDate;
import com.skyblock.skyblock.features.time.SkyblockTimeManager;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;

import java.sql.Timestamp;
import java.util.HashMap;

public class CalendarEventsGUI extends Gui {
    public CalendarEventsGUI() {
        super("Calendar and Events", 54, new HashMap<>());

        Util.fillBorder(this);

        addItem(49, Util.buildCloseButton());
        addItem(48, Util.buildBackButton());

        SkyblockTimeManager timeManager = Skyblock.getPlugin().getTimeManager();

        SkyblockDate date = timeManager.getCurrentDate();

        int diff = 0;

        String season = date.getSeason();
        int day = date.getDate();

        int i = 9;
        while (i != 44) {
            i++;

            if (getItems().containsKey(i)) continue;

            day++;
            if (day == 31) {
                season = timeManager.getNextSeason(season);
                day = 1;
            }

            diff++;

            if (season.equals("fall") && day == 30) {
                addItem(i, new CalendarEvent(CalendarEvent.EventType.SPOOKY_FESTIVAL, new Timestamp(System.currentTimeMillis() + diff * 120000L)).getDisplayItem());
            } else if (season.equals("winter") && day == 25) {
                addItem(i, new CalendarEvent(CalendarEvent.EventType.SEASON_OF_JERRY, new Timestamp(System.currentTimeMillis() + diff * 120000L)).getDisplayItem());
            } else if ((season.equals("summer") && day == 2) || (season.equals("winter") && day == 2)) {
                addItem(i, new CalendarEvent(CalendarEvent.EventType.TRAVELING_ZOO, new Timestamp(System.currentTimeMillis() + diff * 120000L)).getDisplayItem());
            } else {
                i--;
            }
        }
    }
}
