package com.skyblock.skyblock.features.items;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import lombok.Getter;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Getter
public abstract class SkyblockItem {

    protected static final Skyblock plugin = Skyblock.getPlugin(Skyblock.class);
    private final ItemStack item;
    private final String internalName;

    public SkyblockItem(ItemStack baseItem, String internalName) {
        this.item = baseItem;
        this.internalName = internalName;
    }

    public SkyblockItem(String internalName) {
        this.internalName = internalName;
        this.item = plugin.getItemHandler().getItem(internalName);
    }

    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        throw new UnsupportedOperationException("This item does not support right clicking");
    }

    public void onLeftClick(PlayerInteractEvent event) {
        throw new UnsupportedOperationException("This item does not support left clicking");
    }

    public void onBowShoot(EntityShootBowEvent event) {
        throw new UnsupportedOperationException("This item does not support shooting a bow");
    }

    public void onEntityDamage(EntityDamageByEntityEvent event) {
        throw new UnsupportedOperationException("This item does not support dealing damage");
    }

    public void onEntityDamage(SkyblockPlayerDamageEntityEvent event) {
        throw new UnsupportedOperationException("This item does not support dealing damage");
    }

    public void onArmorEquip(PlayerArmorEquipEvent event) {
        throw new UnsupportedOperationException("This item does not support equipping armor");
    }

    public void onArmorUnEquip(PlayerArmorUnequipEvent event) {
        throw new UnsupportedOperationException("This item does not support unequipping armor");
    }

    public void onBlockBreak(BlockBreakEvent event) {
        throw new UnsupportedOperationException("This item does not support breaking blocks");
    }

    public void onRegenerate(ItemBase item) {
        throw new UnsupportedOperationException("This item does not support regeneration");
    }

    public double getModifiedIncomingDamage(SkyblockPlayer player, EntityDamageEvent event, double damage) {
        return damage;
    }

    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        return damage;
    }

    protected boolean isThisItem(ItemStack item) {
        return Util.getSkyblockId(item).equals(Util.getSkyblockId(getItem()));
    }
}
