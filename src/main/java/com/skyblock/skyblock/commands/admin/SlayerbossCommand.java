package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.slayer.SlayerBoss;
import com.skyblock.skyblock.features.slayer.boss.RevenantHorror;
import com.skyblock.skyblock.features.slayer.boss.SvenPackmaster;
import com.skyblock.skyblock.features.slayer.boss.TarantulaBroodfather;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Permission;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiresPlayer
@Permission(permission = "skyblock.admin")
@Usage(usage = "/sb slayerboss zombie/spider/wolf level")
@Description(description = "Spawns a slayer boss")
public class SlayerbossCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length != 2)  { sendUsage(player); return; }

        try {
            String bossId = args[0];
            int level = Integer.parseInt(args[1]);

            SlayerBoss boss;

            switch (bossId.toLowerCase()) {
                case "spider":
                    boss = new TarantulaBroodfather(player, level);
                    break;
                case "wolf":
                    boss = new SvenPackmaster(player, level);
                    break;
                default:
                    boss = new RevenantHorror(player, level);
                    break;
            }

            boss.spawn(player.getLocation());

            player.sendMessage(ChatColor.GREEN + "Success!");
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid level!");
        }
    }
}
