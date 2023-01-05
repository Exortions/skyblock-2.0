package com.skyblock.skyblock.event;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.collections.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkyblockPlayerCollectionRewardEvent extends SkyblockEvent {

    private SkyblockPlayer player;
    private String reward;

}
