package com.skyblock.skyblock.event;

import com.skyblock.skyblock.SkyblockPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkyblockCoinsChangeEvent extends SkyblockEvent {

    private SkyblockPlayer player;

}
