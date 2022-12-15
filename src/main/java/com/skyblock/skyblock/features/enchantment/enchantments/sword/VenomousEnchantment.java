package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class VenomousEnchantment extends SwordEnchantment {

    public VenomousEnchantment() {
        super("venomous", "Venomous", (level) -> {
            return "&7Reduces the target's walk speed\n&7by " + ChatColor.GREEN + (level / 2) * 10 + "% &7and deals " + (level / 2) * 10 + "\n&7damage per second. Lasts 4\nseconds";
        }, 4);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (player.getExtraData("venomous_enchantment") == null) return damage;

        try {
            ItemBase base = new ItemBase(player.getBukkitPlayer().getItemInHand());
            int level = base.getEnchantment(this.getName()).getLevel();
            return (level / 2f) * 10;
        } catch (IllegalArgumentException | NullPointerException ignored) {}

        return damage;
    }

    @Override
    public void onDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (player.getExtraData("venomous_enchantment") != null) return;

        final double critChance = player.getPreciseStat(SkyblockStat.CRIT_CHANCE);

        for (int i = 0; i < 4; i++) {
            Util.delay(() -> {
                if (ThunderlordEnchantment.hits.containsKey(player.getBukkitPlayer())) {
                    ThunderlordEnchantment.ThunderlordInfo info = ThunderlordEnchantment.hits.get(player.getBukkitPlayer());
                    ThunderlordEnchantment.hits.put(player.getBukkitPlayer(), new ThunderlordEnchantment.ThunderlordInfo(info.getHits() - 1, info.getEntity()));
                }

                player.setExtraData("venomous_enchantment", true);
                player.setStat(SkyblockStat.CRIT_CHANCE, 0, false);
                Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(player.getBukkitPlayer(), e.getEntity(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
                ((LivingEntity) e.getEntity()).damage(0);
                player.setStat(SkyblockStat.CRIT_CHANCE, critChance, false);
                player.setExtraData("venomous_enchantment", null);
            }, i * 20);
        }
    }
}
