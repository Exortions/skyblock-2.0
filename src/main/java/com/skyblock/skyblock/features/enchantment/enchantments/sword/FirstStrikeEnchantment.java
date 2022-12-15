package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.function.Function;

public class FirstStrikeEnchantment extends SwordEnchantment {
    public FirstStrikeEnchantment() {
        super("first_strike", "First Strike", (level) -> {
            return ChatColor.GRAY + "Increases melee damage by\n" + ChatColor.GREEN + "+" + level * 25 + "% " + ChatColor.GRAY + "for the first hit on a\n&7mob";
        }, 4);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (e.getEntity().hasMetadata("skyblockEntityData")) {
            SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());
            if (sentity == null) return damage;

            if (!sentity.getDamaged().contains(player.getBukkitPlayer())) {
                try {
                    ItemBase base = new ItemBase(player.getBukkitPlayer().getItemInHand());
                    int level = base.getEnchantment(this.getName()).getLevel();
                    sentity.getDamaged().add(player.getBukkitPlayer());
                    return damage + (damage * ((level * 25) / 100F));
                } catch (IllegalArgumentException | NullPointerException ignored) {
                    return damage;
                }
            } else {
                return damage;
            }
        }

        return damage;
    }
}
