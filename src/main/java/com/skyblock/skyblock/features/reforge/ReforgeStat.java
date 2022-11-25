package com.skyblock.skyblock.features.reforge;

import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.enums.SkyblockStat;
import lombok.Data;

import java.util.HashMap;

@Data
public class ReforgeStat {

    private final Rarity rarity;
    private final HashMap<SkyblockStat, Integer> stats;
    private final ReforgeData parent;

    public int get(SkyblockStat stat) {
        return this.stats.getOrDefault(stat, 0);
    }

}
