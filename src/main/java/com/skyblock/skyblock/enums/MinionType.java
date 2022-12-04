package com.skyblock.skyblock.enums;

import java.util.Arrays;

public interface MinionType<T extends Enum<T>> {

    String name();
    Class<T> getDeclaringClass();
    T getRaw();

    static MinionType<?> getEnumValue(String name) {
        for (Class<? extends Enum<?>> minionClass : Arrays.asList(MiningMinionType.class)) {
            for (Enum<?> minion : minionClass.getEnumConstants()) {
                if (name.equals(minion.name())) {
                    return (MinionType<?>) minion;
                }
            }
        }

        return null;
    }

}
