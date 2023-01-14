package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class SkyblockPlayerItemHeldChangeEvent extends SkyblockEvent {

    private SkyblockPlayer player;
    private ItemStack oldItem;
    private ItemStack newItem;

}
