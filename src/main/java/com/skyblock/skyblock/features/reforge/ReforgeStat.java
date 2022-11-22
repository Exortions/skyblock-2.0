package com.skyblock.skyblock.features.reforge;

import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.enums.SkyblockStat;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class ReforgeStat {

    private final Rarity rarity;
    private final HashMap<SkyblockStat, Integer> stats;

    public int get(SkyblockStat stat) {
        return this.stats.getOrDefault(stat, 0);
    }

}
