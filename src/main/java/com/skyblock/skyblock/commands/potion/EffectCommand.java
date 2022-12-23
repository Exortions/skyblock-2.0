package com.skyblock.skyblock.commands.potion;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.potions.PotionEffect;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb effect <effect> <amplifier> <duration>")
@Description(description = "Gives you a SkyBlock potion effect")
public class EffectCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length != 3) {
            player.sendMessage(ChatColor.RED + "Usage: /sb effect <effect> <amplifier> <duration>");
            return;
        }

        String effect = args[0];
        int amplifier = Integer.parseInt(args[1]);
        double duration = Double.parseDouble(args[2]);

        PotionEffect potionEffect = plugin.getPotionEffectHandler().effect(SkyblockPlayer.getPlayer(player), effect, amplifier, duration, false);

        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "BUFF! " + ChatColor.RESET + ChatColor.WHITE + "You have gained " + PotionEffect.getMaxLevelsAndColors.get(effect).getThird() + potionEffect.getName() + " " + Util.toRoman(amplifier) + ChatColor.WHITE + "! Type /effects to view your active effects!");
    }
}
