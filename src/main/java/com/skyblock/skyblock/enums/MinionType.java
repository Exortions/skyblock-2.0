package com.skyblock.skyblock.enums;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface MinionType<T extends Enum<T>> extends Serializable {

    String name();
    Class<T> getDeclaringClass();
    T getRaw();

    static List<Class<? extends Enum<?>>> getMinionTypes() {
        return Arrays.asList(
                MiningMinionType.class
        );
    }

    static List<MinionType<?>> getAllMinionTypes() {
        List<MinionType<?>> list = new ArrayList<>();

        for (Class<? extends Enum<?>> type : getMinionTypes()) {
            for (Enum<?> e : type.getEnumConstants()) {
                list.add((MinionType<?>) e);
            }
        }

        return list;
    }

    static MinionType<?> getEnumValue(String name) {
        for (Class<? extends Enum<?>> minionClass : MinionType.getMinionTypes()) {
            for (Enum<?> minion : minionClass.getEnumConstants()) {
                if (name.equals(minion.name())) {
                    return (MinionType<?>) minion;
                }
            }
        }

        return null;
    }

}
