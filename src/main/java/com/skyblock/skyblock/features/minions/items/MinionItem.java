package com.skyblock.skyblock.features.minions.items;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.minions.MinionBase;
import com.skyblock.skyblock.features.minions.items.MinionItemType;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;

import lombok.Data;
import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Data
public abstract class MinionItem {
    protected static final Skyblock plugin = Skyblock.getPlugin(Skyblock.class);
    private final ItemStack item;
    private final String internalName;
    protected final MinionItemType type;
    public final boolean stackable; // use getter
    public final boolean guiEquippable; // use getter

    public MinionItem(ItemStack baseItem, String internalName, MinionItemType type, boolean stackable, boolean guiEquippable) {
        this.item = baseItem;
        this.internalName = internalName;
        this.type = type;
        this.stackable = stackable;
        this.guiEquippable = guiEquippable;
    }

    public boolean isThisItem(ItemStack item) {
        return Util.getSkyblockId(item).equals(Util.getSkyblockId(getItem()));
    }

    public void onEquip(MinionBase minion) {}
    public void onUnEquip(MinionBase minion) {}
    public void onTick(MinionBase minion) {}
    public void postTick(MinionBase minion) {}

    public float onSleep(MinionBase minion, float duration) {
        return duration;
    }

    public ArrayList<ItemStack> onBlockCollect(MinionBase minion, ArrayList<ItemStack> drops) {
        return drops;
    }
    public boolean onItemClick(Player player, ItemStack item) {
        return true; // -> remove
    }
}
