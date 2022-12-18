package com.skyblock.skyblock.features.objectives;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.features.objectives.impl.starting.BreakLogObjective;
import com.skyblock.skyblock.features.objectives.impl.starting.PickaxeObjective;
import com.skyblock.skyblock.features.objectives.impl.starting.WorkbenchObjective;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class QuestLine {

    private final List<Objective> line;
    private final String name;

    public QuestLine(String name, Objective... objectives) {
        this.line = Arrays.asList(objectives);
        this.name = name;
    }

    public Objective getObjective(SkyblockPlayer skyblockPlayer) {
        List<String> completed = (ArrayList<String>) skyblockPlayer.getValue("quests.completedObjectives");

        for (Objective obj : line) {
            if (completed.contains(obj.getId())) continue;

            return obj;
        }

        return null;
    }

    public Objective getNext(Objective obj) {
        return line.get(line.indexOf(obj) + 1);
    }
}
