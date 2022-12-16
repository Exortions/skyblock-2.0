package com.skyblock.skyblock.features.items;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.utilities.item.ItemHandler;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
public abstract class ArmorSet {

    protected final static ItemHandler handler = Skyblock.getPlugin(Skyblock.class).getItemHandler();
    private final HashMap<Player, BukkitRunnable> runnables;
    private final ItemStack helmet;
    private final ItemStack chest;
    private final ItemStack legs;
    private final ItemStack boots;
    private final String id;

    public ArmorSet(ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots, String id) {
        this.helmet = helmet;
        this.chest = chest;
        this.legs = legs;
        this.boots = boots;
        this.id = id;

        this.runnables = new HashMap<>();
    }

    public void fullSetBonus(Player player) {
        if (runnables.get(player) == null) {
            runnables.put(player, new BukkitRunnable() {
                @Override
                public void run() {
                    tick(player);
                }
            });
        }

        runnables.get(player).runTaskTimerAsynchronously(Skyblock.getPlugin(Skyblock.class), 5L, 1);
    }

    public void stopFullSetBonus(Player player) {
        runnables.get(player).cancel();
    }

    public void onStatChange(Player player, SkyblockStat stat) { }

    public abstract void tick(Player player);
    public double getModifiedDamage(SkyblockPlayer player, EntityDamageByEntityEvent e, double damage) {
        return damage;
    }

    public List<ItemStack> toList() {
        return Arrays.asList(helmet, chest, legs, boots);
    }
}
