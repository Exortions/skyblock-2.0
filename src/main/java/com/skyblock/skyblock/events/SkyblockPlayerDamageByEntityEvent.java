package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkyblockPlayerDamageByEntityEvent extends SkyblockEvent {
    private SkyblockPlayer player;
    private SkyblockEntity entity;
    private boolean trueDamage;
    private double damage;
}
