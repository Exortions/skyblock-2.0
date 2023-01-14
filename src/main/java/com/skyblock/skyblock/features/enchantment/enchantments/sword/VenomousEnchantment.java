package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class VenomousEnchantment extends SwordEnchantment {

    public VenomousEnchantment() {
        super("venomous", "Venomous", (level) -> {
            return "&7Reduces the target's walk speed\n&7by " + ChatColor.GREEN + (level / 2) * 10 + "% &7and deals " + (level / 2) * 10 + "\n&7damage per second. Lasts 4\nseconds";
        }, 4);
    }

    @Override
    public void onDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (Util.isNotSkyblockEntity(e)) return;

        for (int i = 0; i < 4; i++) {
            Util.delay(() -> {
                if (ThunderlordEnchantment.hits.containsKey(player.getBukkitPlayer())) {
                    ThunderlordEnchantment.ThunderlordInfo info = ThunderlordEnchantment.hits.get(player.getBukkitPlayer());
                    ThunderlordEnchantment.hits.put(player.getBukkitPlayer(), new ThunderlordEnchantment.ThunderlordInfo(info.getHits() - 1, info.getEntity()));
                }

                SkyblockEntity entity = Util.getSBEntity(e);

                if (entity == null) return;

                entity.damage((long) ((Util.getEnchantmentLevel(this.getName(), player) / 2f) * 10), player, false, ChatColor.DARK_GREEN);
            }, (i + 1) * 20);
        }
    }
}
