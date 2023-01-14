package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Permission;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb skill <skill> amount")
@Permission(permission = "skyblock.admin")
@Description(description = "Edit skill xp of player")
public class SkillXpCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length != 2) { sendUsage(player); return; }

        String skill = WordUtils.capitalize(args[0].toLowerCase());
        int amount = Integer.parseInt(args[1]);

        Skill.reward(Skill.parseSkill(skill), amount, SkyblockPlayer.getPlayer(player));

        player.sendMessage(String.format(ChatColor.GRAY + "Successfully modified skill %s", skill));
    }
}
