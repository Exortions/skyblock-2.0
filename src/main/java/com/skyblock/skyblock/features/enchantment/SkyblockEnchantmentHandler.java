package com.skyblock.skyblock.features.enchantment;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.enchantment.types.*;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<SkyblockEnchantment> getEnchantments(String type) {
        List<Class<? extends SkyblockEnchantment>> instancesToCheck = new ArrayList<>(Collections.singletonList(MiscEnchantment.class));

        if (type.equalsIgnoreCase("helmet") || type.equalsIgnoreCase("chestplate") || type.equalsIgnoreCase("leggings") || type.equalsIgnoreCase("boots")) {
            instancesToCheck.add(ArmorEnchantment.class);
        } else if (type.equalsIgnoreCase("bow")) {
            instancesToCheck.add(BowEnchantment.class);
        } else if (type.equalsIgnoreCase("fishing rod")) {
            instancesToCheck.add(FishingRodEnchantment.class);
        } else if (type.equalsIgnoreCase("sword")) {
            instancesToCheck.add(SwordEnchantment.class);
        }

        if (type.equalsIgnoreCase("boots")) {
            instancesToCheck.add(BootEnchantment.class);
        }

        if (type.equalsIgnoreCase("helmet")) {
            instancesToCheck.add(HeadEnchantment.class);
        }

        return enchantments.stream().filter(enchantment -> instancesToCheck.stream().anyMatch(instance -> instance.isInstance(enchantment))).collect(Collectors.toList());
    }

}
