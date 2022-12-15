package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.function.Function;

public class SharpnessEnchantment extends SwordEnchantment {
    public SharpnessEnchantment() {
        super("sharpness", "Sharpness", (level) -> ChatColor.GRAY + "Increases melee damage by\n" + ChatColor.GREEN + level * 5 + "%", 6);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        try {
            ItemBase base = new ItemBase(player.getBukkitPlayer().getItemInHand());
            int level = base.getEnchantment(this.getName()).getLevel();
            return damage + (damage * ((level * 5) / 100F));
        } catch (IllegalArgumentException | NullPointerException ignored) {}

        return damage;
    }
}
