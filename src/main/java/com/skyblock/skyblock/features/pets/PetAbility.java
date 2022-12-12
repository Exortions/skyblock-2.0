package com.skyblock.skyblock.features.pets;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.Rarity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public interface PetAbility {

    String getName();
    List<String> getDescription();
    default Rarity getRequiredRarity() {
        return Rarity.COMMON;
    }

    default void onHurt(EntityDamageByEntityEvent e, Entity damager) {
        throw new UnsupportedOperationException("This pet ability does not support being hurt");
    }

    default void onDamage(EntityDamageByEntityEvent e) {
        throw new UnsupportedOperationException("This pet ability does not support dealing damage");
    }

    default void onEquip(SkyblockPlayer player) {
        throw new UnsupportedOperationException("This pet ability does not support being equipped");
    }

    default void onUnequip(SkyblockPlayer player) {
        throw new UnsupportedOperationException("This pet ability does not support being unequipped");
    }

}
