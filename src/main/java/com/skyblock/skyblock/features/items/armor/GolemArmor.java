package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.ArmorSet;
import com.skyblock.skyblock.features.items.BlockHelmetSet;
import com.skyblock.skyblock.event.SkyblockEntityDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class GolemArmor extends ArmorSet {
    public GolemArmor() {
        super(Skyblock.getPlugin().getItemHandler().getItem("GOLEM_ARMOR_HELMET.json"),
                Skyblock.getPlugin().getItemHandler().getItem("GOLEM_ARMOR_CHESTPLATE.json"),
                Skyblock.getPlugin().getItemHandler().getItem("GOLEM_ARMOR_LEGGINGS.json"),
                Skyblock.getPlugin().getItemHandler().getItem("GOLEM_ARMOR_BOOTS.json"),
                "golem_armor"
        );
    }

    @EventHandler
    public void onEntityKill(SkyblockEntityDeathEvent event) {
        Player p = event.getKiller().getBukkitPlayer();
        if (Skyblock.getPlugin().getSkyblockItemHandler().getRegistered(p.getInventory().getArmorContents()) != this)
                return;

        if (p.hasPotionEffect(PotionEffectType.ABSORPTION)) p.removePotionEffect(PotionEffectType.ABSORPTION);
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*20, 3));
    }
}
