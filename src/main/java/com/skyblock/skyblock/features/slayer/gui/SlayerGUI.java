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
        super("Slayer", 36, new HashMap<String, Runnable>() {{
            put(ChatColor.RED + "☠ " + ChatColor.YELLOW + "Revenant Horror", () -> {
                new RevenantGUI(opener).show(opener);
            });

            put(ChatColor.RED + "☠ " + ChatColor.YELLOW + "Tarantula Broodfather", () -> {
                new TarantulaGUI(opener).show(opener);
            });

            put(ChatColor.RED + "☠ " + ChatColor.YELLOW + "Sven Packmaster", () -> {
                new SvenGUI(opener).show(opener);
            });

            put(ChatColor.GREEN + "Slayer Quest Complete", () -> {
                SlayerHandler slayerHandler = Skyblock.getPlugin(Skyblock.class).getSlayerHandler();
                SlayerQuest quest = slayerHandler.getSlayer(opener).getQuest();
                int level = getLevel(quest.getType(), getXP(opener, quest.getType()));
                int newLevel = getLevel(quest.getType(), getXP(opener, quest.getType()) + quest.getExpReward());
                
                SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(opener);
                skyblockPlayer.setValue("slayer." + quest.getType().name().toLowerCase() + ".exp", getXP(opener, quest.getType()) + quest.getExpReward());

                opener.sendMessage("  " + ChatColor.GREEN + ChatColor.BOLD + "SLAYER QUEST COMPLETED!");

                if (newLevel > level) {
                    opener.sendMessage("  " + ChatColor.GREEN + ChatColor.BOLD + "LVL UP! " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "➜" + ChatColor.YELLOW + " " + quest.getType().getAlternative() + " Slayer LVL " + newLevel);
                    opener.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " REWARD AVAILABLE");
                    opener.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "  CLICK TO COLLECT");
                } else {
                    if (level == 9) {
                        opener.sendMessage(ChatColor.YELLOW + "  " + quest.getType().getAlternative() + " Slayer LVL " + level + "    " + ChatColor.GREEN + "" + ChatColor.BOLD + "LVL MAXED OUT!");
                    } else {
                        opener.sendMessage("   " + ChatColor.YELLOW + quest.getType().getAlternative() + " Slayer LVL " +
                                level +  ChatColor.DARK_RED + " - " + ChatColor.GRAY + "Next LVL in " + ChatColor.LIGHT_PURPLE +
                                Util.formatInt(getXP(quest.getType()).get(level) - getXP(skyblockPlayer.getBukkitPlayer(), quest.getType())) + " XP" + ChatColor.GRAY + "!");
                    }
                }
                
                opener.playSound(opener.getLocation(), Sound.LEVEL_UP, 1, 2);
                
                slayerHandler.unregisterSlayer(opener);
               
                opener.closeInventory();
               new SlayerGUI(opener).show(opener);
            });
        }});

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

        ability1.add(" ");
        ability2.add(" ");
        ability3.add(" ");

        switch (abilityAmount) {
            case 1:
                ability2.clear();
                ability3.clear();
                break;
            case 2:
                ability3.clear();
                break;
        }

        return new ItemBuilder(color + name + " " + Util.toRoman(level), material).addLore(ChatColor.DARK_GRAY + DESCRIPTIONS.get(level - 1), " ", ChatColor.GRAY + "Health: " + ChatColor.RED + Util.formatInt(hp) + "❤", ChatColor.GRAY + "Damage: " + ChatColor.RED + Util.formatInt(dps) + ChatColor.GRAY + " per second", " ").addLore(ability1).addLore(ability2).addLore(ability3).addLore(ChatColor.GRAY + "Reward: " + ChatColor.LIGHT_PURPLE + xp + " " + type.getAlternative() + " Slayer XP", ChatColor.DARK_GRAY + "  + Boss Drops", " ", ChatColor.GRAY + "Cost to start: " + ChatColor.GOLD + Util.formatInt(cost) + " coins", " ", ChatColor.YELLOW + "Click to slay!").toItemStack();
    }

    private static final List<Integer> ZOMBIE_XP = Arrays.asList(5, 15, 200, 1000, 5000, 20000, 100000, 400000, 1000000);
    private static final List<Integer> SPIDER_XP = Arrays.asList(5, 25, 200, 1000, 5000, 20000, 100000, 400000, 1000000);
    private static final List<Integer> WOLF_XP = Arrays.asList(10, 30, 250, 1500, 5000, 20000, 100000, 400000, 1000000);
    public static int getLevel(SlayerType type, int exp) {
        List<Integer> list = new ArrayList<>();

        switch (type) {
            case REVENANT:
                list = ZOMBIE_XP;
                break;
            case TARANTULA:
                list = SPIDER_XP;
                break;
            case SVEN:
                list = WOLF_XP;
                break;
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i) <= exp)
                return i + 1;
        }

        return 0;
    }

    public static int getXP(Player player, SlayerType type) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        return (int) skyblockPlayer.getValue("slayer." + type.name().toLowerCase() + ".exp");
    }

    public static List<Integer> getXP(SlayerType type) {
        switch (type) {
            case REVENANT:
                return ZOMBIE_XP;
            case SVEN:
                return WOLF_XP;
            case TARANTULA:
                return SPIDER_XP;
        }

        return new ArrayList<>();
    }
}
