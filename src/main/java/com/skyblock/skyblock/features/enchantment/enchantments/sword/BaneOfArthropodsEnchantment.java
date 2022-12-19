package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.function.Function;

public class BaneOfArthropodsEnchantment extends SwordEnchantment {

    private static final Function<Integer, Integer> getBonusDamageByLevel = (level) -> {
        if (level <= 4) return level * 10;
        else if (level <= 6) return 40 + (level - 4) * 20;

        return 0;
    };

    public BaneOfArthropodsEnchantment() {
        super("bane_of_arthropods", "Bane of Arthropods", level -> "&7Increases damage dealt to\n&7Spiders, Silverfish, and\n&7Cave Spiders by &a" + getBonusDamageByLevel.apply(level) + "%&7.", 6);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());

        if (sentity == null) return damage;

        if (sentity.getVanilla().getType().equals(EntityType.SPIDER) || sentity.getVanilla().getType().equals(EntityType.CAVE_SPIDER) || sentity.getVanilla().getType().equals(EntityType.SILVERFISH)) {
            try {
                ItemBase base = new ItemBase(player.getBukkitPlayer().getItemInHand());
                int level = base.getEnchantment(this.getName()).getLevel();

                return damage + (damage * (getBonusDamageByLevel.apply(level) / 100.0));
            } catch (Exception ex) {
                return damage;
            }
        }

        return damage;
    }
}
