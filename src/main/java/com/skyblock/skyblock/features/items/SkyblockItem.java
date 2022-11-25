package com.skyblock.skyblock.features.items;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import lombok.Getter;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class SkyblockItem {

    private final ItemStack item;
    private final String internalName;
    public static String ID;

    public SkyblockItem(ItemStack baseItem, String internalName) {
        this.item = baseItem;
        this.internalName = internalName;
    }

    public void onRightClick(PlayerInteractEvent event) { }

    public void onLeftClick(PlayerInteractEvent event) { }

    public void onBowShoot(EntityShootBowEvent event) { }

    public void onEntityDamage(EntityDamageByEntityEvent event) { }

    public void onArmorEquip(PlayerArmorEquipEvent event) { }

    public void onArmorUnEquip(PlayerArmorUnequipEvent event) {}
}
