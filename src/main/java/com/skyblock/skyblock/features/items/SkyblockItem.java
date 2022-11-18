package com.skyblock.skyblock.features.items;

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

    public void onRightClick(PlayerInteractEvent e){ }

    public void onLeftClick(PlayerInteractEvent e){ }

    public void onBowShoot(EntityShootBowEvent e){ }

    public void onEntityDamage(EntityDamageByEntityEvent e){ }

    /*

    Currently unused

    public void onArmorEquip(PlayerArmorEquipEvent e) { }

    public void onArmorUnEquip(PlayerArmorUnequipEvent e) {}

     */
}
