package com.skyblock.skyblock.features.enchantment.enchantments;

import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

public class TestEnchantment extends SwordEnchantment {

    private static HashMap<Integer, String> description = new HashMap<>();

    static {
        description.put(1, "Test enchantment gives 20% damage");
        description.put(2, "Test enchantment gives 40% damage");
        description.put(3, "Test enchantment gives 60% damage");
        description.put(4, "Test enchantment gives 80% damage");
        description.put(5, "Test enchantment gives 100% damage");
    }

    public TestEnchantment() {
        super("Test", description, 5);
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (((Player) event.getDamager()).getItemInHand() != null && !((Player) event.getDamager()).getItemInHand().getType().equals(Material.AIR)) return;

            Player player = (Player) event.getDamager();

            ItemBase item;

            NBTItem nbtItem = new NBTItem(player.getInventory().getItemInHand());

            System.out.println("Enchantments: " + nbtItem.getString("enchantments"));

            try {
                item = new ItemBase(player.getInventory().getItemInHand());
            } catch (IllegalArgumentException ex) {
                return;
            }

            if (item.hasEnchantment(this)) {
                System.out.println("Test enchantment is working");

                event.setDamage(event.getDamage() * (1 + (item.getEnchantment(this.getName()).getLevel() * 0.2)));
            }
        }
    }

}
