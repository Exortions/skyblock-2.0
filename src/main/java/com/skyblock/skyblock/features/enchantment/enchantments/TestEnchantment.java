package com.skyblock.skyblock.features.enchantment.enchantments;

import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TestEnchantment extends SwordEnchantment {

    public TestEnchantment() {
        super("test", "Test Enchantment", (level) -> "Test enchantment gives " + level * 20 + "% damage", 5);
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (((Player) event.getDamager()).getItemInHand() != null && !((Player) event.getDamager()).getItemInHand().getType().equals(Material.AIR)) return;

            Player player = (Player) event.getDamager();

            ItemBase item;

            try {
                item = new ItemBase(player.getInventory().getItemInHand());
            } catch (Exception ex) {
                return;
            }

            if (item.hasEnchantment(this)) {
                event.setDamage(event.getDamage() * (1 + (item.getEnchantment(this.getName()).getLevel() * 0.2)));
            }
        }
    }

}
