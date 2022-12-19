package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.function.Function;

public class CubismEnchantment extends SwordEnchantment {

    private static final Function<Integer, Integer> getDamageByLevel = (level) -> {
        if (level <= 4) return level * 10;
        else if (level == 5) return 60;

        return 0;
    };

    public CubismEnchantment() {
        super("cubism", "Cubism", level -> "&7Increases damage dealt to\nSlimes, Creepers, and\n&7Magma Cubes by &a" + getDamageByLevel.apply(level) + "%&7.", 5);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());

        if (sentity == null) return damage;

        EntityType type = sentity.getEntityType();

        if (type == EntityType.CREEPER || type == EntityType.MAGMA_CUBE || type == EntityType.SLIME) {
            try {
                ItemBase base = new ItemBase(player.getBukkitPlayer().getInventory().getItemInHand());
                int level = base.getEnchantment("cubism").getLevel();

                return damage * (1 + (getDamageByLevel.apply(level) / 100.0));
            } catch (Exception ex) {
                return damage;
            }
        }

        return damage;
    }
}
