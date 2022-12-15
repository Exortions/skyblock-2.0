package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.utilities.item.ItemBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.UUID;

public class ThunderlordEnchantment extends SwordEnchantment {

    @Getter
    @AllArgsConstructor
    private static class ThunderlordInfo {
        private int hits;
        private UUID entity;

        @Override
        public String toString() {
            return "Hits: " + hits + " UUID: " + entity;
        }
    }

    private static final HashMap<Player, ThunderlordInfo> hits = new HashMap<>();

    public ThunderlordEnchantment() {
        super("thunderlord", "Thunderlord", (level) -> "&7Strikes a monster with lightning\n&7every 3 consecutive hits,\n&7dealing " + ChatColor.GREEN + level * 10 + "% &7of your Strength\n&7as damage", 4);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (player.getExtraData("thunderlord_enchantment") == null) return damage;

        try {
            ItemBase base = new ItemBase(player.getBukkitPlayer().getItemInHand());
            int level = base.getEnchantment(this.getName()).getLevel();
            return player.getStat(SkyblockStat.STRENGTH) * ((level * 10) / 100F);
        } catch (IllegalArgumentException | NullPointerException ignored) {}

        return damage;
    }

    @Override
    public void onDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (hits.containsKey(player.getBukkitPlayer())) {
            if (!hits.get(player.getBukkitPlayer()).getEntity().equals(e.getEntity().getUniqueId())) {
                hits.put(player.getBukkitPlayer(), new ThunderlordInfo(1, e.getEntity().getUniqueId()));
            } else {
                hits.put(player.getBukkitPlayer(), new ThunderlordInfo(hits.get(player.getBukkitPlayer()).getHits() + 1, e.getEntity().getUniqueId()));

                if (hits.get(player.getBukkitPlayer()).getHits() % 3 == 0) {
                    e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
                    player.setExtraData("thunderlord_enchantment", true);
                    Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(player.getBukkitPlayer(), e.getEntity(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
                    player.setExtraData("thunderlord_enchantment", null);
                }
            }
        } else {
            hits.put(player.getBukkitPlayer(), new ThunderlordInfo(1, e.getEntity().getUniqueId()));
        }
    }
}
