package com.skyblock.skyblock.features.slayer.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.slayer.SlayerBoss;
import com.skyblock.skyblock.features.slayer.SlayerHandler;
import com.skyblock.skyblock.features.slayer.SlayerQuest;
import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SlayerGUI extends Gui {

    public static final List<ChatColor> COLORS = Arrays.asList(ChatColor.GREEN, ChatColor.YELLOW, ChatColor.RED, ChatColor.DARK_RED);
    private static final List<Integer> ZOMBIE_XP = Arrays.asList(5, 15, 200, 1000, 5000, 20000, 100000, 400000, 1000000);
    private static final List<Integer> SPIDER_XP = Arrays.asList(5, 25, 200, 1000, 5000, 20000, 100000, 400000, 1000000);
    private static final List<Integer> WOLF_XP = Arrays.asList(10, 30, 250, 1500, 5000, 20000, 100000, 400000, 1000000);
    private static final List<String> DESCRIPTIONS = Arrays.asList("Beginner", "Strong", "Challenging", "Deadly");
    public static final List<Integer> COINS = Arrays.asList(100, 2000, 10000, 50000);

    public SlayerGUI(Player opener) {
        super("Slayer", 36, new HashMap<String, Runnable>() {{
            put(ChatColor.RED + "☠ " + ChatColor.YELLOW + "Revenant Horror", () -> new RevenantGUI(opener).show(opener));

            put(ChatColor.RED + "☠ " + ChatColor.YELLOW + "Tarantula Broodfather", () -> new TarantulaGUI(opener).show(opener));

            put(ChatColor.RED + "☠ " + ChatColor.YELLOW + "Sven Packmaster", () -> new SvenGUI(opener).show(opener));

            put(ChatColor.GREEN + "Slayer Quest Complete", () -> {
                SlayerHandler slayerHandler = Skyblock.getPlugin().getSlayerHandler();
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

            put(ChatColor.GREEN + "Ongoing Slayer Quest", () -> {
                SlayerHandler slayerHandler = Skyblock.getPlugin().getSlayerHandler();
                slayerHandler.unregisterSlayer(opener);
                opener.closeInventory();
                opener.sendMessage(ChatColor.GREEN + "Your Slayer quest has been cancelled.");
            });

            put(ChatColor.RED + "Slayer Quest Failed", () -> {
                SlayerHandler slayerHandler = Skyblock.getPlugin().getSlayerHandler();
                slayerHandler.unregisterSlayer(opener);
                opener.closeInventory();
                new SlayerGUI(opener).show(opener);
            });

            put(ChatColor.RED + "Close", opener::closeInventory);
        }});

        Util.fillEmpty(this);

        addItem(31, Util.buildCloseButton());

        Skyblock skyblock = Skyblock.getPlugin(Skyblock.class);

        if (SkyblockPlayer.getPlayer(opener).hasActiveSlayer()) {
            SlayerHandler.SlayerData data = skyblock.getSlayerHandler().getSlayer(opener);
            SlayerQuest quest = data.getQuest();
            SlayerBoss boss = data.getBoss();

            Material type;

            switch (quest.getType()) {
                case REVENANT:
                    type = Material.ROTTEN_FLESH;
                    break;
                case SVEN:
                    type = Material.MUTTON;
                    break;
                case TARANTULA:
                    type = Material.WEB;
                    break;
                default:
                    type = null;
                    skyblock.sendMessage("&cCould not find material for slayer quest type &8" + quest.getType().getName() + " &8[" + opener.getDisplayName() + "]&c!");
                    return;
            }

            if (quest.getState().equals(SlayerQuest.QuestState.FINISHED)) {
                String timeToKill = Util.formatTime(quest.getTimeToKill());
                String timeToSpawn = Util.formatTime(quest.getTimeToSpawn());

                addItem(13, new ItemBuilder(ChatColor.GREEN + "Slayer Quest Complete", type).addLore(ChatColor.GRAY + "You've slain the boss! Skyblock", ChatColor.GRAY + "is now a little safer thanks to you!", " ", ChatColor.GRAY + "Boss: " + ChatColor.DARK_RED + boss.getEntityData().entityName + " " + Util.toRoman(boss.getLevel()), " ", ChatColor.DARK_GRAY + "Time to spawn: " + timeToSpawn, ChatColor.DARK_GRAY + "Time to kill: " + timeToKill, " ", ChatColor.GRAY + "Reward: " + ChatColor.DARK_PURPLE + quest.getExpReward() + " " + quest.getType().getAlternative() + " Slayer XP", " ", ChatColor.YELLOW + "Click to collect reward!").toItemStack());
            } else if (quest.getState().equals(SlayerQuest.QuestState.SUMMONING)) {
                addItem(13, new ItemBuilder(ChatColor.GREEN + "Ongoing Slayer Quest", type).addLore(ChatColor.GRAY + "You have an active Slayer quest.", " ", ChatColor.GRAY + "Boss: " + COLORS.get(boss.getLevel() - 1) + boss.getEntityData().entityName + " " + boss.getLevel(), ChatColor.GRAY + "Kill " + quest.getType().getAlternative() + " to spawn the boss!", " ", ChatColor.YELLOW + "Click to cancel quest!").toItemStack());
            } else if (quest.getState().equals(SlayerQuest.QuestState.FAILED)) {
                addItem(13, new ItemBuilder(ChatColor.RED + "Slayer Quest Failed", type).addLore(ChatColor.GRAY + "You didn't succeed in", ChatColor.GRAY + "killing the boss on your", ChatColor.GRAY + "last Slayer quest.", " ", ChatColor.DARK_GRAY + "It's no big deal! You can", ChatColor.DARK_GRAY + "always try again!", " ", ChatColor.YELLOW + "Ok, thanks for reminding me!").toItemStack());
            }
        } else {
            addItem(10, getSlayerItem(SlayerType.REVENANT, opener));
            addItem(11, getSlayerItem(SlayerType.TARANTULA, opener));
            addItem(12, getSlayerItem(SlayerType.SVEN, opener));

            for (int i = 13; i < 17; i++) {
                addItem(i, new ItemBuilder(ChatColor.RED + "Not released yet!", Material.COAL_BLOCK).addLore(ChatColor.GRAY + "This boss is still in", ChatColor.GRAY + "development!").toItemStack());
            }
        }
    }

    public static ItemStack getSlayerItem(SlayerType type, Player player) {
        Material material = Material.AIR;
        String alternate = type.getAlternative();
        String name = type.getName();
        List<String> desc = new ArrayList<>();

        ChatColor c = ChatColor.GRAY;

        switch (type) {
            case REVENANT:
                material = Material.ROTTEN_FLESH;
                desc.add(c + "Abhorrent Zombie stuck");
                desc.add(c + "between life and death for");
                desc.add(c + "an eternity");
                break;
            case TARANTULA:
                material = Material.WEB;
                desc.add(c + "Monstrous Spider who poisons");
                desc.add(c + "and devours its victims");
                break;
            case SVEN:
                material = Material.MUTTON;
                desc.add(c + "Rabid Wolf genetically");
                desc.add(c + "modified by a famous mad");
                desc.add(c + "scientist. Eats bones and");
                desc.add(c + "flesh");
                break;
            default:
                break;
        }

        ItemBuilder item = new ItemBuilder(ChatColor.RED + "☠ " +
                ChatColor.YELLOW + name, material);
        int level = getLevel(type, getXP(player, type));

        if (level != 0) {
            return item.addLore(desc).
                    addLore(" ", ChatColor.GRAY + alternate +
                    " Slayer: " + ChatColor.YELLOW + "LVL " + level, " ",
                    ChatColor.YELLOW + "Click to view boss!").toItemStack();
        } else {
            return item.addLore(desc).
                    addLore(" ", ChatColor.YELLOW + "Click to view boss!").toItemStack();
        }
    }

    public static ItemStack getStartItem(SlayerType type, int level, int hp, int dps, int xp, int cost, int abilityAmount) {
        Material material = Material.AIR;
        ChatColor color = COLORS.get(level - 1);
        String name = type.getName();
        List<String> ability1 = new ArrayList<>();
        List<String> ability2 = new ArrayList<>();
        List<String> ability3 = new ArrayList<>();

        switch (type) {
            case REVENANT:
                material = Material.ROTTEN_FLESH;
                ability1.add(ChatColor.RED + "Life Drain");
                ability1.add(ChatColor.GRAY + "Drains health every few seconds.");
                ability2.add(ChatColor.GREEN + "Pestilence");
                ability2.add(ChatColor.GRAY + "Deals AOE damage every second,");
                ability2.add(ChatColor.GRAY + "shredding armor by 25%.");
                ability3.add(ChatColor.RED + "Enrage");
                ability3.add(ChatColor.GRAY + "Gets real mad once in a while.");
                break;
            case TARANTULA:
                material = Material.WEB;
                ability1.add(ChatColor.YELLOW + "Combat Jump");
                ability1.add(ChatColor.GRAY + "The spider will often attempt to");
                ability1.add(ChatColor.GRAY + "jump behind you.");
                ability2.add(ChatColor.RED + "Noxious");
                ability2.add(ChatColor.GRAY + "Deals AOE damage every second,");
                ability2.add(ChatColor.GRAY + "reducing your healing by 50%.");
                break;
            case SVEN:
                material = Material.MUTTON;
                ability1.add(ChatColor.GREEN + "Agile");
                ability1.add(ChatColor.GRAY + "The wolf is small and fast, making");
                ability1.add(ChatColor.GRAY + "it hard to hit.");
                ability2.add(ChatColor.WHITE + "True Damage");
                ability2.add(ChatColor.GRAY + "Ignores your defense. Very painful.");
                ability3.add(ChatColor.AQUA + "Call the pups!");
                ability3.add(ChatColor.GRAY + "At 50% health, calls its deadly pack");
                ability3.add(ChatColor.GRAY + "of pups.");
                break;
            default:
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
            default:
                break;
        }

        return new ItemBuilder(color + name + " " + Util.toRoman(level), material).addLore(ChatColor.DARK_GRAY + DESCRIPTIONS.get(level - 1), " ", ChatColor.GRAY + "Health: " + ChatColor.RED + Util.formatInt(hp) + "❤", ChatColor.GRAY + "Damage: " + ChatColor.RED + Util.formatInt(dps) + ChatColor.GRAY + " per second", " ").addLore(ability1).addLore(ability2).addLore(ability3).addLore(ChatColor.GRAY + "Reward: " + ChatColor.LIGHT_PURPLE + xp + " " + type.getAlternative() + " Slayer XP", ChatColor.DARK_GRAY + "  + Boss Drops", " ", ChatColor.GRAY + "Cost to start: " + ChatColor.GOLD + Util.formatInt(cost) + " coins", " ", ChatColor.YELLOW + "Click to slay!").toItemStack();
    }

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
