package com.skyblock.skyblock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkyblockStat {

    STRENGTH("❁"),
    HEALTH("❤"),
    MAX_HEALTH(""),
    SPEED("✦"),
    MANA("✎"),
    MAX_MANA(""),
    MINING_SPEED("⸕"),
    ATTACK_SPEED("⚔"),
    CRIT_CHANCE("☣"),
    CRIT_DAMAGE("☠"),
    PET_LUCK("♣"),
    MAGIC_FIND("✯"),
    SEA_CREATURE_CHANCE("α"),
    TRUE_DEFENSE("❂"),
    DEFENSE("❈"),
    FEROCITY("⫽"),
    ABILITY_DAMAGE("๑"),
    DAMAGE(""),

    ;

    private final String icon;

}
