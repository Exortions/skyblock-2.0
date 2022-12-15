package com.skyblock.skyblock.features.enchantment;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.item.ItemBase;
import lombok.Data;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.function.Function;

@Data
public abstract class SkyblockEnchantment implements Listener {

    protected final String name;
    protected final String displayName;
    protected final Function<Integer, String> description;

    protected final int maxLevel;

    public SkyblockEnchantment(String name, String displayName, Function<Integer, String> description, int maxLevel) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.maxLevel = maxLevel;
    }

    public String getDescription(int level) {
        return description.apply(level);
    }

    public void onUnEnchant(ItemBase base) { }

    public void onEnchant(ItemBase base) { }

    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        return damage;
    }

    public void onDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) { }
}
