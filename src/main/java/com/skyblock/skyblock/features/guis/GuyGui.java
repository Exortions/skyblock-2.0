package com.skyblock.skyblock.features.guis;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GuyGui extends Gui {

    private static final List<Integer> upgrades = Arrays.asList(-1, 60, 20, 5, 0, Integer.MAX_VALUE);
    private static final int[] costs = new int[]{0, 200000, 1000000, 5000000};
    private static final int[] collections = new int[]{15000, 50000, 100000, 250000};

    public GuyGui(Player opener) {
        super("Guy", 36, new HashMap<>());

        Util.fillEmpty(this);

        addItem(31, Util.buildCloseButton());

        SkyblockPlayer player = SkyblockPlayer.getPlayer(opener);
        addItem(13, createUpgradeItem(player));

        getClickEvents().put(getItem(13).getItemMeta().getDisplayName(), () -> {
            NBTItem item = new NBTItem(getItem(13));

            if (item.getBoolean("maxed")) return;
            if (!item.getBoolean("upgradable")) return;

            int cost = item.getInteger("cost");
            int next = item.getInteger("next");

            player.setValue("bank.personal.cooldown", next);
            player.subtractCoins(cost);

            opener.sendMessage(ChatColor.GREEN + "Successfully upgraded your Personal Bank to " + next + " minutes.");
            opener.playSound(opener.getLocation(), Sound.NOTE_PLING, 10, 2);
            opener.closeInventory();
        });
    }

    private ItemStack createUpgradeItem(SkyblockPlayer player) {
        int collection = player.getIntValue("collection.emerald.exp");
        int cooldown = player.getIntValue("bank.personal.cooldown");

        ItemBuilder item = new ItemBuilder(ChatColor.GREEN + "Personal Bank Upgrade", Material.SKULL_ITEM, (short) SkullType.PLAYER.ordinal());

        item.addLore("&7Guy can reduce the cooldown", "&7to access your Personal", "&7Bank", " ", "&7Current: " + ChatColor.GREEN + (cooldown == -1 ? "Not Unlocked!" : cooldown + " minutes"));

        int next = upgrades.get(upgrades.indexOf(cooldown) + 1);
        boolean maxed = next == Integer.MAX_VALUE;
        boolean upgradable = false;
        int cost = 0;

        item.addLore("&7Upgrade: " + ChatColor.YELLOW + (maxed ? "Maxed out" : next + " minutes"));

        if (!maxed) {
            cost = costs[upgrades.indexOf(cooldown)];
            int collect = collections[upgrades.indexOf(cooldown)];

            item.addLore(" ", "&7Cost: " + ChatColor.GOLD + (cost == 0 ? "Free" : Util.formatInt(cost)), "&7Collection: " + ChatColor.GREEN + Util.format(collect) + " emeralds", " ");

            if (player.getCoins() >= cost && collection >= collect) {
                item.addLore(ChatColor.YELLOW + "Click to upgrade!");
                upgradable = true;
            } else {
                item.addLore(ChatColor.RED + "Unable to upgrade");
            }
        }

        NBTItem nbt = new NBTItem(item.toItemStack());
        nbt.setBoolean("maxed", maxed);
        nbt.setInteger("next", next);
        nbt.setBoolean("upgradable", upgradable);
        nbt.setInteger("cost", cost);

        return Util.idToSkull(nbt.getItem(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjA5Mjk5YTExN2JlZTg4ZDMyNjJmNmFiOTgyMTFmYmEzNDRlY2FlMzliNDdlYzg0ODEyOTcwNmRlZGM4MWU0ZiJ9fX0=");
    }
}
