package com.skyblock.skyblock.events;

import com.skyblock.skyblock.features.npc.NPC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class SkyblockPlayerNPCClickEvent extends SkyblockEvent {

    private Player player;
    private NPC npc;

}
