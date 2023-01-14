package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@Data
@AllArgsConstructor
public class SkyblockPlayerDamageEntityEvent extends SkyblockEvent {

    private SkyblockPlayer player;
    private SkyblockEntity entity;
    private double damage;
    private EntityDamageByEntityEvent bukkitEvent;

}
