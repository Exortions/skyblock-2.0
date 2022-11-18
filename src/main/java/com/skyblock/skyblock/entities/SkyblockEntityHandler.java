package com.skyblock.skyblock.entities;

import org.bukkit.entity.Entity;

import java.util.HashMap;

public class SkyblockEntityHandler {

    private final HashMap<Integer, SkyblockEntity> entities;

    public SkyblockEntityHandler() {
        entities = new HashMap<>();
    }

    public void registerEntity(SkyblockEntity entity) {
        entities.put(entity.getVanilla().getEntityId(), entity);
    }

    public void unregisterEntity(int entityId) {
        entities.remove(entityId);
    }

    public SkyblockEntity getEntity(Entity entity) {
        return entities.get(entity.getEntityId());
    }
}
