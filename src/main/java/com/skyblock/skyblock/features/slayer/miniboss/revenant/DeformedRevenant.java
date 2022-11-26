package com.skyblock.skyblock.features.slayer.miniboss.revenant;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.slayer.miniboss.SlayerMiniboss;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class DeformedRevenant extends SlayerMiniboss {

    public DeformedRevenant(Player spawner) {
        super(EntityType.ZOMBIE, spawner);
        loadStats(360000, 1600, true, false, true,
                new Equipment(new ItemBuilder(Material.LEATHER_HELMET).addEnchantmentGlint().dyeColor(Color.RED).toItemStack(),
                    new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack(),
                    new ItemBuilder(Material.LEATHER_LEGGINGS).addEnchantmentGlint().dyeColor(Color.RED).toItemStack(),
                    new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantmentGlint().toItemStack(),
                    new ItemBuilder(Material.GOLD_SWORD).addEnchantmentGlint().toItemStack()),
                "Deformed Revenant", 300, 1200);
    }

    @Override
    protected void tick() {
        Zombie zombie = (Zombie) getVanilla();
        zombie.setBaby(false);
        zombie.setVillager(false);
    }
}
