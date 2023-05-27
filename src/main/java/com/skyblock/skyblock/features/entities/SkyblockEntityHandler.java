package com.skyblock.skyblock.features.entities;

import org.bukkit.entity.Entity;

import java.util.HashMap;

public class SkyblockEntityHandler {

    private final HashMap<Integer, SkyblockEntity> entities;
    private final SkyblockMobDropHandler mobDropHandler;

    public SkyblockEntityHandler() {
        entities = new HashMap<>();
        mobDropHandler = new SkyblockMobDropHandler();
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

    public SkyblockEntity getEntity(int id) { return entities.get(id); }

    public SkyblockMobDropHandler getMobDropHandler() {
        return mobDropHandler;
    }
}
