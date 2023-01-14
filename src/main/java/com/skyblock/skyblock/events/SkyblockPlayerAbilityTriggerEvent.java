package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.item.ItemBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@AllArgsConstructor
public class SkyblockPlayerAbilityTriggerEvent extends SkyblockEvent {

    private final SkyblockPlayer player;
    private final ItemBase item;

    @Setter
    private HashMap<String, Object> data;

}
