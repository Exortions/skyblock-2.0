package com.skyblock.skyblock.event;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.collections.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkyblockCollectItemEvent extends SkyblockEvent {

    private final SkyblockPlayer player;
    private final Collection collection;
    private final int amount;

}
