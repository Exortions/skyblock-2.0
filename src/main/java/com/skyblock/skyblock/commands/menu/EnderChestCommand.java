package com.skyblock.skyblock.commands.menu;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@RequiresPlayer
@Usage(usage = "/sb enderchest")
@Description(description = "Open your enderchest")
public class EnderChestCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);

        Inventory inventory = Bukkit.createInventory(null, 27, "Ender Chest");

        inventory.setContents(player.getEnderChest().getContents());

        player.openInventory(inventory);
    }

}
