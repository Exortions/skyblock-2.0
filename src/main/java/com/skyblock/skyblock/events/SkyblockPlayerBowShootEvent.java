package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public class SkyblockPlayerBowShootEvent extends SkyblockEvent {

    private final SkyblockPlayer player;
    private final Material arrow;
    private final float force;

}
