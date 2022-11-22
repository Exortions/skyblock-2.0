package com.skyblock.skyblock.features.reforge;

import com.skyblock.skyblock.enums.Item;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.enums.Reforge;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class ReforgeData {

    private final Reforge reforge;
    private final Item applicable;
    private final HashMap<Rarity, ReforgeStat> stats;

}
