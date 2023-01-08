package com.skyblock.skyblock.commands.menu;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Alias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiresPlayer
@Alias(aliases = "skill")
@Usage(usage = "/sb skills [menu]")
@Description(description = "Opens the skills menu.")
public class SkillsCommand implements Command, Listener {

    public SkillsCommand() {
        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin(Skyblock.class));
    }

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        List<String> skillStringList = Skill.SKILLS;
        List<Skill> skills = new ArrayList<>();

        for (String skill : skillStringList) {
            skills.add(Skill.parseSkill(skill));
        }

        if (args.length == 0 || args[0].equals("main")) {
            Inventory inventory = Bukkit.createInventory(null, 54, "Your Skills");

            Util.fillEmpty(inventory);

            inventory.setItem(4, new ItemBuilder(
                    ChatColor.GREEN + "Your Skills",
                    Material.DIAMOND_SWORD)
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                    .addLore(
                            ChatColor.GRAY + "View your Skill progression and",
                            ChatColor.GRAY + "rewards.", "",
                            ChatColor.GOLD + "14.8 Skill Avg. " + ChatColor.DARK_GRAY + "(non-cosmetic)",
                            "",
                            ChatColor.YELLOW + "Click to show rankings!")
                    .toItemStack());

            for (int i = 19; i < 19 + skills.size(); i++) inventory.setItem(i, createSkillPreviewItem(skyblockPlayer, skills.get(i - 19), false));

            inventory.setItem(48, Util.buildBackButton());
            inventory.setItem(49, Util.buildCloseButton());

            player.openInventory(inventory);
        } else if (args[0].equals("rankings")) {
            // TODO: rankings
        } else if (Skill.SKILLS.contains(StringUtils.capitalize(args[0]))) {
            Skill skill = Skill.parseSkill(args[0]);

            if (skill == null) {
                player.sendMessage(ChatColor.RED + "Unknown skill: " + args[0]);
                return;
            }

            Inventory inventory = Bukkit.createInventory(null, 54, args[0] + " Skill");

            Util.fillEmpty(inventory);

            int menu;

            if (Skill.getLevel(Skill.getXP(skill, skyblockPlayer)) <= 25) menu = 0;
            else if (Skill.getLevel(Skill.getXP(skill, skyblockPlayer)) <= 50) menu = 1;
            else if (Skill.getLevel(Skill.getXP(skill, skyblockPlayer)) <= 60) menu = 2;
            else menu = 0;

            if (menu == 0) this.generateSkillTree(skyblockPlayer, inventory, skill, 1, 25);
            else if (menu == 1) this.generateSkillTree(skyblockPlayer, inventory, skill, 26, 50);
            else this.generateSkillTree(skyblockPlayer, inventory, skill, 51, 60);

            inventory.setItem(0, this.createSkillPreviewItem(skyblockPlayer, skill, true));

            inventory.setItem(48, Util.buildBackButton("&7To Your Skills"));
            inventory.setItem(49, Util.buildCloseButton());

            player.openInventory(inventory);
        } else {
            player.sendMessage(ChatColor.RED + "Failed to open skills menu: could not find menu " + ChatColor.DARK_GRAY + args[0] + ChatColor.RED + "!");
        }
    }

    public ItemStack createSkillPreviewItem(SkyblockPlayer player, Skill skill, boolean menu) {
        double xp = Skill.getXP(skill, player);
        int level = Skill.getLevel(xp);
        double xpRequired = Skill.XP.get(level + 1);
        int nextLevel = level + 1;

        double progress = Math.round(xp / xpRequired * 1000) / 10.0;

        ItemStack stack = skill.getGuiIcon();
        ItemMeta meta = stack.getItemMeta();

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        meta.setDisplayName(ChatColor.GREEN + skill.getName() + " " + Util.toRoman(level));

        List<String> rewards = skill.getRewards(nextLevel, level);

        StringBuilder rewardsString = new StringBuilder();

        rewardsString.append(ChatColor.YELLOW).append("  ").append(skill.getAlternate()).append(" ").append(Util.toRoman(nextLevel)).append("\n");

        for (String reward : rewards) {
            rewardsString.append("&7   ").append(reward).append("\n");
        }

        if (nextLevel == 51) {
            meta.setLore(
                    Arrays.asList(
                            Util.buildLore(
                                    "&7" + skill.getDescription() + "\n\n" +
                                            ChatColor.DARK_GRAY + "Max Skill level reached!\n" +
                                            ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "------------ " + ChatColor.RESET + ChatColor.GOLD  + "  " + Util.formatLong((long) xp) + " XP" + "\n\n" +
                                            (menu ? "&8Increase your " + skill.getName() + " level to\n&8unlock Perks, statistic bonuses\n&8and more!" : "&eClick to view!"),
                                    '7'
                            )
                    )
            );
        } else {
            meta.setLore(
                    Arrays.asList(
                            Util.buildLore(
                                    "&7" + skill.getDescription() + "\n\n" +
                                            "Progress to Level " + Util.toRoman(nextLevel) + ": &e" + progress + "%\n" +
                                            "" + Util.getProgressBar(progress, 20, 5) + " &e" + xp + "&6/&e" + Util.abbreviate((int) xpRequired) + "\n\n" +
                                            (menu ? "&8Increase your " + skill.getName() + " level to\n&8unlock Perks, statistic bonuses\n&8and more!" : "Level " + Util.toRoman(nextLevel) + " Rewards:\n" +
                                                    rewardsString + "\n" +
                                                    "&eClick to view!"),
                                    '7'
                            )
                    )
            );
        }

        stack.setItemMeta(meta);

        return stack;
    }

    public void generateSkillTree(SkyblockPlayer player, Inventory inventory, Skill skill, int startLevel, int endLevel) {
        int index = 0;

        int previousSlot = 0;

        for (int i = startLevel - 1; i < endLevel + 1; i++) {
            try {
                if (i == 0) {
                    index++;
                    continue;
                }

                int slot;

                Material material;
                ChatColor color;
                short data = 0;

                if (i % 5 == 0) material = skill.getGuiMilestoneIcon().getType();
                else material = Material.STAINED_GLASS_PANE;

                int level = Skill.getLevel(Skill.getXP(skill, player)) + 1;
                boolean pane = material.equals(Material.STAINED_GLASS_PANE);

                if (level > i) {
                    if (pane) data = (short) 5;
                    color = ChatColor.GREEN;
                } else if (level == i) {
                    if (pane) data = (short) 4;
                    color = ChatColor.YELLOW;
                } else {
                    if (pane) data = (short) 14;
                    color = ChatColor.RED;
                }

                if (index <= 3 || index == 11 || index == 12 || index == 13) slot = previousSlot + 9;
                else if (index == 4 || index == 5 || index == 9 || index == 10 || index == 14 || index == 15 || index == 19 || index == 20) slot = previousSlot + 1;
                else if (index <= 8 || index == 16 || index == 17 || index == 18) slot = previousSlot - 9;
                else slot = previousSlot + 9;

                index++;

                List<String> rewards = skill.getRewards(i, i - 1);

                StringBuilder rewardsString = new StringBuilder();

                rewardsString.append(ChatColor.YELLOW).append("  ").append(skill.getAlternate()).append(" ").append(Util.toRoman(i)).append("\n");

                for (String reward : rewards) {
                    rewardsString.append("&7   ").append(reward).append("\n");
                }

                double xp = Skill.getXP(skill, player);
                double required = Skill.XP.get(i - 1);

                double percent = Math.round(xp / required * 1000) / 10.0;

                inventory.setItem(slot, new ItemBuilder(" ", material, data)
                        .setDisplayName(color + skill.getName() + " Level " + Util.toRoman(i))
                        .setAmount(i)
                        .setLore(
                                Util.buildLore(
                                        "Rewards:\n" + rewardsString
                                                + (color == ChatColor.YELLOW ? "\n\nProgress: &e" + percent + "%\n" + Util.getProgressBar(percent, 20, 5) + " &e" + xp + "&6/&e" + Util.abbreviate((int) required) : ""),
                                        '7')
                        )
                        .toItemStack());

                previousSlot = slot;
            } catch (ArrayIndexOutOfBoundsException ignored) { }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        List<String> possibleNames = new ArrayList<>();

        possibleNames.add("Your Skills");

        for (String name : Skill.SKILLS) possibleNames.add(name + " Skill");

        if (!possibleNames.contains(event.getClickedInventory().getName())) return;

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        NBTItem nbt = new NBTItem(item);

        if (nbt.getBoolean("close").equals(true)) {
            event.getWhoClicked().closeInventory();
        }

        if (nbt.getBoolean("back").equals(true)) {
            String command;

            if (event.getClickedInventory().getName().equals("Your Skills")) command = "sb menu";
            else command = "sb skills";

            ((Player) event.getWhoClicked()).performCommand(command);
        }

        if (event.getClickedInventory().getName().equals("Your Skills")) {
            for (String n : possibleNames) {
                n = n.replace(" Skill", "");

                if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")[0].equalsIgnoreCase(n)) {
                    ((Player) event.getWhoClicked()).performCommand("sb skills " + n);
                }
            }
        }
    }

}
