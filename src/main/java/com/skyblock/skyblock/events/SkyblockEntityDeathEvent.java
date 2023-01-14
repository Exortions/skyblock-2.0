package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkyblockEntityDeathEvent extends SkyblockEvent {

    private SkyblockEntity entity;
    private SkyblockPlayer killer;

}
