package com.skyblock.skyblock.features.slayer.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.slayer.SlayerBoss;
import com.skyblock.skyblock.features.slayer.SlayerHandler;
import com.skyblock.skyblock.features.slayer.SlayerQuest;
import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SlayerGUI extends CraftInventoryCustom implements Listener {

    public SlayerGUI(Player opener) {
        super(null, 36, "Slayer");

        Util.fillEmpty(this);

        setItem(31, Util.buildCloseButton());

        Skyblock skyblock = Skyblock.getPlugin(Skyblock.class);

        if (SkyblockPlayer.getPlayer(opener).hasActiveSlayer()) {
            SlayerHandler.SlayerData data = skyblock.getSlayerHandler().getSlayer(opener);
            SlayerQuest quest = data.getQuest();
            SlayerBoss boss = data.getBoss();

            if (quest.getState().equals(SlayerQuest.QuestState.FINISHED)) {
                Material type = Material.ROTTEN_FLESH;

                switch (quest.getType()) {
                    case SVEN:
                        type = Material.MUTTON;
                        break;
                    case TARANTULA:
                        type = Material.WEB;
                        break;
                }

                setItem(13, new ItemBuilder(ChatColor.GREEN + "Slayer Quest Complete", type).addLore(ChatColor.GRAY + "You've slain the boss! Skyblock", ChatColor.GRAY + "is now a little safer thanks to you!", " ", ChatColor.GRAY + "Boss: " + ChatColor.DARK_RED + boss.getName(), " ", ChatColor.DARK_GRAY + "Time to spawn: 00m00s", ChatColor.DARK_GRAY + "Time to kill: 00m00s", " ", ChatColor.GRAY + "Reward: " + ChatColor.DARK_PURPLE + quest.getExpReward(), " ", ChatColor.YELLOW + "Click to collect reward!").toItemStack());
            }
        } else {
            setItem(10, getSlayerItem(SlayerType.REVENANT, opener));
            setItem(11, getSlayerItem(SlayerType.TARANTULA, opener));
            setItem(12, getSlayerItem(SlayerType.SVEN, opener));

            for (int i = 13; i < 17; i++) {
                setItem(i, new ItemBuilder(ChatColor.RED + "Not released yet!", Material.COAL_BLOCK).addLore(ChatColor.GRAY + "This boss is still in", ChatColor.GRAY + "development!").toItemStack());
            }
        }
    }

    private ItemStack getSlayerItem(SlayerType type, Player player) {
        Material material = Material.AIR;
        String alternate = "";
        String desc = "";
        String name = "";

        switch (type) {
            case REVENANT:
                material = Material.ROTTEN_FLESH;
                alternate = "Zombie";
                desc = "Abhorrent Zombie stuck\nbetween life and death for\nan eternity";
                name = "Revenant Horror";
                break;
            case TARANTULA:
                material = Material.WEB;
                alternate = "Spider";
                desc = "Monstrous Spider who poisons\nand devours its victims";
                name = "Tarantula Broodfather";
                break;
            case SVEN:
                material = Material.MUTTON;
                alternate = "Wolf";
                desc = "Rabid Wolf genetically\nmodified by a famous mad\nscientist. Eats bones and\nflesh";
                name = "Sven Packmaster";
                break;
        }

        return new ItemBuilder(ChatColor.RED + "☠ " + ChatColor.YELLOW + name, material).addLore(ChatColor.GRAY + desc, " ", ChatColor.GRAY + alternate + " Slayer: " + ChatColor.YELLOW + "N/A", " ", ChatColor.YELLOW + "Click to view boss!").toItemStack();
    }
}
