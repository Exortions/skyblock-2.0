package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.collections.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkyblockPlayerCollectItemEvent extends SkyblockEvent {

    private final SkyblockPlayer player;
    private final Collection collection;
    private final int amount;

}
