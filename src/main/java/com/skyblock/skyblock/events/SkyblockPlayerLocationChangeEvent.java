package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkyblockPlayerLocationChangeEvent extends SkyblockEvent {

    private final SkyblockPlayer player;
    private final String from;
    private final String to;

}
