package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkyblockPlayerCoinUpdateEvent extends SkyblockEvent {

    private SkyblockPlayer player;

}
