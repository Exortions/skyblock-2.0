package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CleaveEnchantment extends SwordEnchantment {

    public CleaveEnchantment() {
        super("cleave", "Cleave", (level) -> {
            return "&7Deals " + ChatColor.GREEN + level * 3 + "% &7of your damage\n&7dealth to other mosters within\n" + ChatColor.GREEN + Math.round((3.3 + (level - 1) * 0.3)) + "&7 blocks of the target";
        }, 4);
    }

    @Override
    public void onDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (Util.isNotSkyblockEntity(e)) return;

        try {
            ItemBase base = new ItemBase(player.getBukkitPlayer().getItemInHand());
            int level = base.getEnchantment(this.getName()).getLevel();
            Entity entity = e.getEntity();

            for (Entity en : entity.getNearbyEntities((3.3 + (level - 1) * 0.3), 2, (3.3 + (level - 1) * 0.3))) {
                if (en instanceof Player) continue;

                Util.getSBEntity(en).damage((long) ((level * 3L) / 100F), player, false);
            }
        }  catch (IllegalArgumentException | NullPointerException ignored) { }
    }
}
