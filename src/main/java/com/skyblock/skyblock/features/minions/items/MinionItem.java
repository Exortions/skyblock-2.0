package com.skyblock.skyblock.features.minions.items;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.minions.MinionBase;
import com.skyblock.skyblock.features.minions.items.MinionItemType;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Getter
public abstract class MinionItem {
    protected static final Skyblock plugin = Skyblock.getPlugin(Skyblock.class);
    private final ItemStack item;
    private final String internalName;
    private final MinionItemType type;
    public final boolean canStack; 

    public MinionItem(ItemStack baseItem, String internalName, MinionItemType type, boolean canStack) {
        this.item = baseItem;
        this.internalName = internalName;
        this.type = type;
        this.canStack = canStack;
    }

    public boolean isThisItem(ItemStack item) {
        return Util.getSkyblockId(item).equals(Util.getSkyblockId(getItem()));
    }

    public void onEquip(MinionBase minion) {}
    public void onUnEquip(MinionBase minion) {}
    public void onTick(MinionBase minion) {}

    public ItemStack[] onBlockCollect(MinionBase minion, ItemStack[] drops) {
        return drops;
    }
    public boolean onItemClick(Player player, ItemStack item) {
        return true; // -> remove
    }
}
