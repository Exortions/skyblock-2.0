package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.skills.Combat;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.features.slayer.SlayerHandler;
import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.features.slayer.boss.RevenantHorror;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb test")
@Description(description = "Command for testing features")
public class TestCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        plugin.getSlayerHandler().startQuest(player, SlayerType.REVENANT, 4);
        Skill.reward(new Combat(), 4799, SkyblockPlayer.getPlayer(player));
    }
}
