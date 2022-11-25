package com.skyblock.skyblock.features.enchantment;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class SkyblockEnchantmentHandler {

    private final List<SkyblockEnchantment> enchantments = new ArrayList<>();

    private final Skyblock skyblock;

    public SkyblockEnchantmentHandler(Skyblock skyblock) {
        this.skyblock = skyblock;
    }

    public void registerEnchantment(SkyblockEnchantment enchantment) {
        enchantments.add(enchantment);

        Bukkit.getPluginManager().registerEvents(enchantment, skyblock);
    }

    public SkyblockEnchantment getEnchantment(String name) {
        for (SkyblockEnchantment enchantment : enchantments) {
            if (enchantment.getName().replace(" ", "_").equalsIgnoreCase(name)) return enchantment;
        }

        return null;
    }

    public List<SkyblockEnchantment> getEnchantments() {
        return enchantments;
    }
}
