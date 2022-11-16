package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.ReforgeType;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb test")
@Description(description = "Command for testing features")
public class TestCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        ItemBase thick_scorpion_foil = new ItemBase(Material.WOOD_SWORD, ChatColor.GOLD + "Thick Scorpion Foil", ReforgeType.NO_REFORGE, 1, Util.listOf(ChatColor.GRAY + "Deals " + ChatColor.GREEN + "+150% " + ChatColor.GRAY + "to Zombies.", ""), false, true, "Heartstopper", Util.listOf(ChatColor.GRAY + "You have " + ChatColor.YELLOW + "4 Ⓞ tickers" + ChatColor.GRAY + ".", ChatColor.GRAY + "Blocking clears" + ChatColor.YELLOW + " 1 Ⓞ " + ChatColor.GRAY + "and heals " + ChatColor.RED + "60❤"  + ChatColor.GRAY + ".", ChatColor.GRAY + "Once all tickers are cleared,", ChatColor.GRAY + "your next attack is empowered", ChatColor.GRAY + "for" + ChatColor.RED + " +250% damage" + ChatColor.GRAY + ".", ChatColor.DARK_GRAY + "Tickers refill after 5 seconds."), "", 0, "", "LEGENDARY SWORD", 100, 200, 0, 0 ,0 ,0 ,0,0, true);

        NBTItem nbt = new NBTItem(thick_scorpion_foil.getStack());

        player.sendMessage(nbt.getString("rarity"));

        thick_scorpion_foil.give(player);
    }
}
