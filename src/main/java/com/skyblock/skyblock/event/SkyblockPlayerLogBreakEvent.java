package com.skyblock.skyblock.event;

import com.skyblock.skyblock.SkyblockPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.Block;

@Getter
@AllArgsConstructor
public class SkyblockPlayerLogBreakEvent extends SkyblockEvent {

    private final SkyblockPlayer player;
    private final Block block;

}
