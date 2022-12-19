package com.skyblock.skyblock.features.objectives.impl.starting;

import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;

public class GettingStartedQuest extends QuestLine {
    public GettingStartedQuest() {
        super("getting_started", "Getting Started", new BreakLogObjective(), new WorkbenchObjective(), new PickaxeObjective(),
                new JerryObjective(), new TeleporterObjective());
    }
}
