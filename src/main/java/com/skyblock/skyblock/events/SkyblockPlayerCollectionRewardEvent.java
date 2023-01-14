package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkyblockPlayerCollectionRewardEvent extends SkyblockEvent {

    private SkyblockPlayer player;
    private String reward;

}
