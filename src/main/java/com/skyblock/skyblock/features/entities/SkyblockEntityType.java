package com.skyblock.skyblock.features.entities;

import com.skyblock.skyblock.features.entities.end.Endermite;
import com.skyblock.skyblock.features.entities.end.ObsidianDefender;
import com.skyblock.skyblock.features.entities.end.Watcher;
import com.skyblock.skyblock.features.entities.end.enderman.Enderman;
import com.skyblock.skyblock.features.entities.spider.Spider;
import com.skyblock.skyblock.features.entities.wolf.Wolf;
import com.skyblock.skyblock.features.entities.zombie.Zombie;

import java.lang.reflect.InvocationTargetException;

public enum SkyblockEntityType {

    ZOMBIE(Zombie.class),
    ENDERMAN(Enderman.class),
    WOLF(Wolf.class),
    SPIDER(Spider.class),
    OBSIDIAN_DEFENDER(ObsidianDefender.class),
    WATCHER(Watcher.class),
    ENDERMITE(Endermite.class);

    Class<? extends SkyblockEntity> baseClass;
    SkyblockEntityType(Class<? extends SkyblockEntity> clazz) {
        baseClass = clazz;
    }

    public SkyblockEntity getNewInstance(String type) {
        try {
            return baseClass.getConstructor(String.class).newInstance(type);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e1) {
            try {
                return baseClass.getConstructor().newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e2) {
                e2.printStackTrace();
            }
        }

        return null;
    }
}
