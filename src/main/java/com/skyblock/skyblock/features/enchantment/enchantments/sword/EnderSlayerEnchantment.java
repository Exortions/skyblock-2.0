package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.function.Function;

public class EnderSlayerEnchantment extends SwordEnchantment {

    private static final Function<Integer, Double> getDamage = (level) -> (double) Util.createFetchableDictionary(level - 1, 15, 30, 45, 60, 80, 100);

    public EnderSlayerEnchantment() {
        super("ender_slayer", "Ender Slayer", (level) -> {
            String description = "Increases damage dealt to\nEnder Dragons and\nEnderman by {damage}%.";

            return description.replace("{damage}", String.valueOf(getDamage.apply(level)));
        }, 6);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        try {
            ItemBase base = new ItemBase(player.getBukkitPlayer().getItemInHand());
            int level = base.getEnchantment(this.getName()).getLevel();

            SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());

            if (sentity.getEntityType().equals(EntityType.ENDERMAN)) damage += (damage * ((getDamage.apply(level)) / 100F));
        } catch (IllegalArgumentException | NullPointerException ignored) {}

        return damage;
    }
}
