package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DragonHunterEnchantment extends SwordEnchantment {

    public DragonHunterEnchantment() {
        super("dragon_hunter", "Dragon Hunter", level -> "&7Increases damage dealt to Ender\n&7Dragons by &a" + level * 8 + "%&7.", 5);
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(e.getEntity());

        try {
            ItemBase base = new ItemBase(player.getBukkitPlayer().getInventory().getItemInHand());
            int level = base.getEnchantment(this.getName()).getLevel();
            return sentity == null || !sentity.getEntityType().equals(EntityType.ENDER_DRAGON) ? damage : damage * (1 + level * 0.08);
        } catch (Exception ignored) {
            return damage;
        }
    }
}
