package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.blocks.crops.FloatingCrystal;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@RequiresPlayer
@Description(description = "Create crop cystals")
@Usage(usage = "/sb createcrystal <material> <range>")
public class CreateCrystalCommand implements Command {
    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        FloatingCrystal crystal = new FloatingCrystal(UUID.randomUUID(), Material.valueOf(args[0]),  (short) 0, player.getLocation(), Integer.valueOf(args[1]));
        crystal.spawn();

        Skyblock.getPlugin().getFloatingCrystalHandler().saveCrystal(crystal);
    }
}
