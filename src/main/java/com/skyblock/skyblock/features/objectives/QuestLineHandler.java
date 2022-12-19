package com.skyblock.skyblock.features.objectives;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.features.objectives.impl.hub.ExploreHubQuest;
import com.skyblock.skyblock.features.objectives.impl.hub.IntroduceYourselfQuest;
import com.skyblock.skyblock.features.objectives.impl.hub.auction.AuctioneerQuest;
import com.skyblock.skyblock.features.objectives.impl.starting.GettingStartedQuest;

import java.util.*;

public class QuestLineHandler {

    private final HashMap<String, List<QuestLine>> quests = new HashMap<>();

    public QuestLineHandler() {
        register("Private Island", new GettingStartedQuest());
        register("Village", new IntroduceYourselfQuest());
        register("Auction House", new AuctioneerQuest());

        for (List<QuestLine> quest : quests.values()) {
            quest.forEach(QuestLine::onEnable);
        }
    }

    public void disable() {
        for (List<QuestLine> quest : quests.values()) {
            quest.forEach(QuestLine::onDisable);
        }
    }

    public void register(String location, QuestLine line) {
        if (quests.containsKey(location)) {
            quests.get(location).add(line);
        } else {
            quests.put(location, Collections.singletonList(line));
        }
    }

    public QuestLine getFromPlayer(SkyblockPlayer player) {
        String loc = player.getCurrentLocationName();

        List<QuestLine> lines = quests.get(loc);
        List<String> completed = (ArrayList<String>) player.getValue("quests.completedQuests");

        if (lines == null) return null;

        for (QuestLine quest : lines) {
            if (completed.contains(quest.getName())) continue;

            return quest;
        }

        return null;
    }

    public QuestLine getQuest(Objective objective) {
        for (List<QuestLine> lines : quests.values()) {
            for (QuestLine line : lines) {
                for (Objective obj : line.getLine()) {
                    if (obj.getId().equals(objective.getId())) return line;
                }
            }
        }

        return null;
    }
}
