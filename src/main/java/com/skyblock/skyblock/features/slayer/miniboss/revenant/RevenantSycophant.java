package com.skyblock.skyblock.features.slayer.miniboss.revenant;

import com.skyblock.skyblock.features.slayer.miniboss.SlayerMiniboss;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class RevenantSycophant extends SlayerMiniboss {

    public RevenantSycophant(Player spawner) {
        super(EntityType.ZOMBIE, spawner);

        loadStats(24000, 320, true, false, true,
                new Equipment(null, new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack(),
                        new ItemBuilder(Material.CHAINMAIL_LEGGINGS).addEnchantmentGlint().toItemStack(),
                        new ItemStack(Material.IRON_BOOTS), new ItemBuilder(Material.DIAMOND_SWORD).addEnchantmentGlint().toItemStack()),
                "Revenant Sycophant", 80, 360);
    }

    @Override
    protected void tick() {
        Zombie zombie = (Zombie) getVanilla();
        zombie.setBaby(false);
        zombie.setVillager(false);
    }
}
