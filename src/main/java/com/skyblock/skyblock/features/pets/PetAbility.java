package com.skyblock.skyblock.features.pets;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.Rarity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public abstract class PetAbility {

    protected abstract String getName();
    protected abstract List<String> getDescription();
    protected Rarity getRequiredRarity() {
        return Rarity.COMMON;
    }

    public void onHurt(EntityDamageByEntityEvent e, Entity damager) {
        throw new UnsupportedOperationException("This pet ability does not support being hurt");
    }

    public void onDamage(EntityDamageByEntityEvent e) {
        throw new UnsupportedOperationException("This pet ability does not support dealing damage");
    }

    public void onEquip(SkyblockPlayer player) {
        throw new UnsupportedOperationException("This pet ability does not support being equipped");
    }

    public void onUnequip(SkyblockPlayer player) {
        throw new UnsupportedOperationException("This pet ability does not support being unequipped");
    }

}
