package com.skyblock.skyblock.enums;

import java.util.Arrays;
import java.util.List;

public enum Item {

    // TODO: Implement all qualified names

    NONE,
    SWORD("SWORD"),
    RANGED("BOW"),
    ARMOR("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"),
    ACCESSORY("ACCESSORY", "TALISMAN", "RING", "ARTIFACT");

    private final List<String> qualifiedNames;

    Item(String... qualifiedNames) {
        this.qualifiedNames = Arrays.asList(qualifiedNames);
    }

    public List<String> getQualifiedNames() {
        return qualifiedNames;
    }

    public boolean containsQualifiedName(String string) {
        for (String s : this.qualifiedNames) {
            if (string.contains(s)) return true;
        }

        return false;
    }

}
