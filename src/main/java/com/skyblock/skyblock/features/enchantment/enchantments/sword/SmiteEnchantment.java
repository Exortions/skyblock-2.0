package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.entities.SkyblockEntityType;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.function.Function;

public class SmiteEnchantment extends SwordEnchantment {
    public SmiteEnchantment() {
        super("smite", "Smite", (level) -> "&7Increases damage dealt to\nZombies, Zombie Pigmen, Skeletons and Withers\nby " + ChatColor.GREEN + (level * 8) + "%", 6);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        try {
            ItemBase base = new ItemBase(player.getBukkitPlayer().getItemInHand());
            int level = base.getEnchantment(this.getName()).getLevel();

            SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());

            if (sentity.isUndead()) damage += (damage * ((level * 8) / 100F));
        } catch (IllegalArgumentException | NullPointerException ignored) {}

        return damage;
    }
}
