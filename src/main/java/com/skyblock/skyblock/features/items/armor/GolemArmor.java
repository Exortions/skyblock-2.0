package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.items.ArmorSet;
import com.skyblock.skyblock.events.SkyblockEntityDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
