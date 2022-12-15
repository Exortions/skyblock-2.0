package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.Pair;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.function.Function;

public class GiantKillerEnchantment extends SwordEnchantment {

    private static final Function<Integer, Pair<Double, Integer>> getDamageAndPercent = (level) -> {
        double damage = 0.1;
        int percent = 5;

        if (level >= 1 && level <= 4) {
            damage = level * 0.1;
            percent = level * 5;
        } else if (level == 5) {
            damage = 0.6;
            percent = 30;
        } else if (level >= 6 && level <= 7) damage = (0.6) + ((level - 5) * 0.3);

        if (level == 6) percent = 45;
        else if (level == 9) percent = 65;

        return Pair.of(damage, percent);
    };

    public GiantKillerEnchantment() {
        super("giant_killer", "Giant Killer", (level) -> {
            Pair<Double, Integer> damageAndPercent = getDamageAndPercent.apply(level);

            return "&7Increases damage dealt by &a" + damageAndPercent.getFirst() + "% &7for each\n&7percent of extra &6Health &7that your target has\n&7above you, up to &a" + damageAndPercent.getSecond() + "% &7per level.";
        }, 7);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        if (!e.getEntity().hasMetadata("skyblockEntityData")) return damage;

        SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());
        if (sentity == null) return damage;

        try {
            ItemBase base = new ItemBase(player.getBukkitPlayer().getItemInHand());
            int level = base.getEnchantment(this.getName()).getLevel();
            Pair<Double, Integer> damageAndPercent = getDamageAndPercent.apply(level);
            sentity.getDamaged().add(player.getBukkitPlayer());

            double entityHealth = sentity.getEntityData().maximumHealth;
            double playerHealth = player.getBukkitPlayer().getMaxHealth();

            double extraHealthPercent = ((entityHealth - playerHealth) / entityHealth) * 100;

            double damageIncrease = damage;

            for (int i = 0; i < extraHealthPercent; i++) {
                damageIncrease += (damage * damageAndPercent.getFirst()) / 100;
            }

            return damageIncrease;
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return damage;
        }
    }
}
