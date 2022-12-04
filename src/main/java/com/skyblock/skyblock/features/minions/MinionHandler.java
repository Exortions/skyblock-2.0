package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.MinionType;
import javafx.util.Pair;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MinionHandler {

    private final HashMap<SkyblockPlayer, HashMap<MinionType<?>, List<Pair<Location, MinionBase>>>> minions;

    public MinionHandler() {
        this.minions = new HashMap<>();
    }

    public void initializeMinion(SkyblockPlayer player, MinionBase minion, Location location) {
        if (!this.minions.containsKey(player)) this.minions.put(player, new HashMap<>());

        if (!this.minions.get(player).containsKey(minion.getType())) this.minions.get(player).put(minion.getType(), new ArrayList<>());

        this.minions.get(player).get(minion.getType()).add(new Pair<>(location, minion));
    }

    public void deleteAll() {
        for (SkyblockPlayer player : this.minions.keySet()) {
            for (MinionType<?> type : this.minions.get(player).keySet()) {
                for (Pair<Location, MinionBase> pair : this.minions.get(player).get(type)) {
                    pair.getValue().getMinion().remove();
                    pair.getValue().getText().remove();
                }
            }
        }

        this.minions.clear();
    }

}
