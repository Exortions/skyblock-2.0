package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.MiningMinionType;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MiningMinion extends MinionBase {

    private BukkitRunnable task;

    public MiningMinion(MiningMinionType minion) {
        super(
                minion.getName(),
                minion.getRecipe(),
                minion.getHand(),
                minion.getHead(),
                minion.getLeatherArmorColor(),
                minion.getTimeBetweenActions(),
                minion.getGetMaximumStorage()
        );

        this.task = null;
    }

    @Override
    public void load(SkyblockPlayer player, int index) {

    }

    @Override
    public void spawn(SkyblockPlayer player, Location location, int level) {
        if (!location.getWorld().getName().startsWith(IslandManager.ISLAND_PREFIX)) return;

        if (this.minion != null) this.minion.remove();

        this.level = level;

        this.minion = location.getWorld().spawn(location, ArmorStand.class);
        this.minion.setCustomName("");
        this.minion.setCustomNameVisible(false);
        this.minion.setGravity(false);
        this.minion.setVisible(false);
        this.minion.setSmall(true);
        this.minion.setArms(true);
        this.minion.setBasePlate(false);

        ItemStack head = Util.IDtoSkull(new ItemBuilder("", Material.SKULL_ITEM, 1, (short) 3).toItemStack(), this.head.apply(this.level));
        this.minion.setHelmet(head);

        ItemStack hand = this.hand.apply(this.level);
        this.minion.setItemInHand(hand);

        this.minion.setChestplate(Util.colorLeatherArmor(new ItemBuilder("", Material.LEATHER_CHESTPLATE, 1).toItemStack(), this.leatherArmorColor));
        this.minion.setLeggings(Util.colorLeatherArmor(new ItemBuilder("", Material.LEATHER_LEGGINGS, 1).toItemStack(), this.leatherArmorColor));
        this.minion.setBoots(Util.colorLeatherArmor(new ItemBuilder("", Material.LEATHER_BOOTS, 1).toItemStack(), this.leatherArmorColor));
    }

    @Override
    public void pickup(SkyblockPlayer player, Location location) {

    }

    @Override
    public void showInventory(SkyblockPlayer player) {

    }

    @Override
    protected void tick(SkyblockPlayer player, Location location) {

    }
}
