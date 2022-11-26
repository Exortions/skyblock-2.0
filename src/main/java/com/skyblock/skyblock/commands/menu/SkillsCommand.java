package com.skyblock.skyblock.commands.menu;

import com.sk89q.util.StringUtil;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.skills.Farming;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Alias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Menus:

main
rankings
<skill>
 */

@RequiresPlayer
@Alias(aliases = "skill")
@Usage(usage = "/sb skills [menu]")
@Description(description = "Opens the skills menu.")
public class SkillsCommand implements Command {

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

            System.out.println(skills);

            for (int i = 19; i < 19 + skills.size(); i++) {
                inventory.setItem(i, createSkillPreviewItem(skyblockPlayer, skills.get(i - 19)));
            }

            inventory.setItem(48, Util.buildBackButton());
            inventory.setItem(49, Util.buildCloseButton());

            player.openInventory(inventory);
        } else if (args[0].equals("rankings")) {
            // rankings
        } else if (Skill.SKILLS.contains(StringUtils.capitalize(args[0]))) {
            // <skill>
        } else {
            player.sendMessage(ChatColor.RED + "Failed to open skills menu: could not find menu " + ChatColor.DARK_GRAY + args[0] + ChatColor.RED + "!");
        }
    }

    public ItemStack createSkillPreviewItem(SkyblockPlayer player, Skill skill) {
        double xp = Skill.getXP(skill, player);
        int level = Skill.getLevel(xp);
        double xpRequired = Skill.XP.get(level + 1);
        int nextLevel = level + 1;

        double progress = Math.round(xp / xpRequired * 1000) / 10.0;

        ItemStack stack = skill.getGuiIcon();
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(ChatColor.GREEN + skill.getName() + " " + Util.toRoman(level));

        List<String> rewards = skill.getRewards(nextLevel, level);

        StringBuilder rewardsString = new StringBuilder();

        rewardsString.append(ChatColor.YELLOW).append("  ").append(skill.getAlternate()).append(" ").append(Util.toRoman(nextLevel)).append("\n");

        for (String reward : rewards) {
            rewardsString.append("&7   ").append(reward).append("\n");
        }

        meta.setLore(
                Arrays.asList(
                    Util.buildLore(
                            "&7" + skill.getDescription() + "\n\n" +
                                    "Progress to Level " + Util.toRoman(nextLevel) + ": &e" + progress + "%\n" +
                                    "" + Util.getProgressBar(progress, 20, 5) + " &e" + xp + "&6/&e" + Util.abbreviate((int) xpRequired) + "\n\n" +
                                    "Level " + Util.toRoman(nextLevel) + " Rewards:\n" +
                                    rewardsString + "\n" +
                                    "&eClick to view!",
                            '7'
                    )
                )
        );

        stack.setItemMeta(meta);

        return stack;
    }

}
