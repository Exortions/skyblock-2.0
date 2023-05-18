package com.skyblock.skyblock.features.objectives;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.objectives.foraging.IntoTheWoodsQuest;
import com.skyblock.skyblock.features.objectives.hub.IntroduceYourselfQuest;
import com.skyblock.skyblock.features.objectives.hub.TimeToStrikeQuest;
import com.skyblock.skyblock.features.objectives.hub.AuctioneerQuest;
import com.skyblock.skyblock.features.objectives.foraging.TimberQuest;
import com.skyblock.skyblock.features.objectives.mines.GoingDeeperQuest;
import com.skyblock.skyblock.features.objectives.mines.LostAndFoundQuest;
import com.skyblock.skyblock.features.objectives.starting.GettingStartedQuest;
import lombok.Getter;

import java.util.*;

@Getter
public class QuestLineHandler {

    private final HashMap<String, List<QuestLine>> quests = new HashMap<>();

    public QuestLineHandler() {
        register("Private Island", new GettingStartedQuest());
        register("Village", new IntroduceYourselfQuest());
        register("Auction House", new AuctioneerQuest());
        register(new String[] { "Forest", "Birch Park" }, new TimberQuest());
        register(new String[] { "Forest", "Birch Park", "Spruce Woods", "Dark Thicket", "Savanna Woodland", "Jungle Island" }, new IntoTheWoodsQuest());
        register(new String[] { "Bar", "Graveyard", "Spiders Den" }, new TimeToStrikeQuest());
        register("Gold Mine", new LostAndFoundQuest());
        register(new String[] {"Gold Mine", "Deep Caverns", "Gunpowder Mines", "Lapis Quarry", "Pigman's Den", "Slimehill", "Diamond Reserve", "Obsidian Sanctuary"}, new GoingDeeperQuest());

        for (List<QuestLine> quest : quests.values()) {
            quest.forEach(QuestLine::onEnable);
        }
    }

    public void disable() {
        for (List<QuestLine> quest : quests.values()) {
            quest.forEach(QuestLine::onDisable);
        }
    }

    public void register(String[] locations, QuestLine line) {
        for (String loc : locations) {
            register(loc, line);
        }
    }

    public void register(String location, QuestLine line) {
        if (quests.containsKey(location)) {
            quests.get(location).add(line);
        } else {
            ArrayList<QuestLine> list = new ArrayList<>();
            list.add(line);

            quests.put(location, list);
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

    public List<QuestLine> getQuests() {
        List<QuestLine> quests = new ArrayList<>();

        for (List<QuestLine> questLines : this.quests.values()) {
            for (QuestLine quest : questLines) {
                if (quests.contains(quest)) continue;
                quests.add(quest);
            }
        }

        return quests;
    }
}
