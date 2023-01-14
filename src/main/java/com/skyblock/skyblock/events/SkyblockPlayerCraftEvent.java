package com.skyblock.skyblock.events;

import com.skyblock.skyblock.features.crafting.SkyblockRecipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class SkyblockPlayerCraftEvent extends SkyblockEvent {

    private final SkyblockRecipe recipe;
    private final Player player;

}
