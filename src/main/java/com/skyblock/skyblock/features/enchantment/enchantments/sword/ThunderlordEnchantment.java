package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.utilities.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.UUID;

public class ThunderlordEnchantment extends SwordEnchantment {

    public static final HashMap<Player, ThunderlordInfo> hits = new HashMap<>();

    @Getter
    @AllArgsConstructor
    public static class ThunderlordInfo {
        private int hits;
        private UUID entity;

        @Override
        public String toString() {
            return "Hits: " + hits + " UUID: " + entity;
        }
    }

    public ThunderlordEnchantment() {
        super("thunderlord", "Thunderlord", (level) -> "&7Strikes a monster with lightning\n&7every 3 consecutive hits,\n&7dealing " + ChatColor.GREEN + level * 10 + "% &7of your Strength\n&7as damage", 4);
    }

    @Override
    public void onDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (Util.isNotSkyblockEntity(e)) return;

        if (hits.containsKey(player.getBukkitPlayer())) {
            if (!hits.get(player.getBukkitPlayer()).getEntity().equals(e.getEntity().getUniqueId())) {
                hits.put(player.getBukkitPlayer(), new ThunderlordInfo(1, e.getEntity().getUniqueId()));
            } else {
                hits.put(player.getBukkitPlayer(), new ThunderlordInfo(hits.get(player.getBukkitPlayer()).getHits() + 1, e.getEntity().getUniqueId()));

                if (hits.get(player.getBukkitPlayer()).getHits() % 3 == 0) {
                    e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
                    Util.getSBEntity(e).damage((long) (player.getStat(SkyblockStat.STRENGTH) * ((Util.getEnchantmentLevel(this.getName(), player) * 10L) / 100F)), player, false);
                }
            }
        } else {
            hits.put(player.getBukkitPlayer(), new ThunderlordInfo(1, e.getEntity().getUniqueId()));
        }
    }
}
