package com.skyblock.skyblock.features.objectives.starting;

import com.skyblock.skyblock.features.objectives.QuestLine;

public class GettingStartedQuest extends QuestLine {
    public GettingStartedQuest() {
        super("getting_started", "Getting Started", new BreakLogObjective(), new WorkbenchObjective(), new PickaxeObjective(),
                new JerryObjective(), new TeleporterObjective());
    }
}
