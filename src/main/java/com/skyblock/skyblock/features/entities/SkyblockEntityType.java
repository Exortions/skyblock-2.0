package com.skyblock.skyblock.features.entities;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.enderman.Enderman;
import com.skyblock.skyblock.features.entities.spider.Spider;
import com.skyblock.skyblock.features.entities.wolf.Wolf;
import com.skyblock.skyblock.features.entities.zombie.Zombie;

import java.lang.reflect.InvocationTargetException;

public enum SkyblockEntityType {

    ZOMBIE(Zombie.class),
    ENDERMAN(Enderman.class),
    WOLF(Wolf.class),
    SPIDER(Spider.class);

    Class<? extends SkyblockEntity> baseClass;
    SkyblockEntityType(Class<? extends SkyblockEntity> clazz) {
        baseClass = clazz;
    }

    public SkyblockEntity getNewInstance(String type) {
        try {
            return baseClass.getConstructor(String.class).newInstance(type);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
