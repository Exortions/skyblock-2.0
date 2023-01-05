package com.skyblock.skyblock.features.entities;

import com.skyblock.skyblock.features.entities.creeper.SneakyCreeper;
import com.skyblock.skyblock.features.entities.dragon.Dragon;
import com.skyblock.skyblock.features.entities.end.Endermite;
import com.skyblock.skyblock.features.entities.end.ObsidianDefender;
import com.skyblock.skyblock.features.entities.end.Watcher;
import com.skyblock.skyblock.features.entities.end.enderman.Enderman;
import com.skyblock.skyblock.features.entities.skeleton.Skeleton;
import com.skyblock.skyblock.features.entities.slime.EmeraldSlime;
import com.skyblock.skyblock.features.entities.spider.Spider;
import com.skyblock.skyblock.features.entities.wolf.Wolf;
import com.skyblock.skyblock.features.entities.zombie.Zombie;
import com.skyblock.skyblock.features.entities.pigman.RedstonePigman;

import java.lang.reflect.InvocationTargetException;

public enum SkyblockEntityType {

    ZOMBIE(Zombie.class),
    ENDERMAN(Enderman.class),
    WOLF(Wolf.class),
    SPIDER(Spider.class),
    OBSIDIAN_DEFENDER(ObsidianDefender.class),
    WATCHER(Watcher.class),
    ENDERMITE(Endermite.class),
    SNEAKY_CREEPER(SneakyCreeper.class),
    EMERALD_SLIME(EmeraldSlime.class),
    SKELETON(Skeleton.class),
    DRAGON(Dragon.class),
    REDSTONE_PIGMAN(RedstonePigman.class);

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
