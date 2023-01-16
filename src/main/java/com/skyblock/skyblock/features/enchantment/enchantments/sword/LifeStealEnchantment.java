package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class LifeStealEnchantment extends SwordEnchantment {
    public LifeStealEnchantment() {
        super("life_steal", "Life Steal", (l) -> {
            return "Heals for " + ChatColor.GREEN + "0." + l + "%" + ChatColor.GRAY + " of the damage\nyou deal to mobs";
        }, 4);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(SkyblockPlayerDamageEntityEvent e) {
        try {
            ItemBase base = new ItemBase(e.getPlayer().getBukkitPlayer().getItemInHand());
            int level = base.getEnchantment(this.getName()).getLevel();

            e.getPlayer().heal((int) (e.getDamage() * (level / 1000f)));
        } catch (IllegalArgumentException | NullPointerException ignored) {}
    }
}
