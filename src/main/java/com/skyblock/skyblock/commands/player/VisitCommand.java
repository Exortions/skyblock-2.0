package com.skyblock.skyblock.commands.player;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

@RequiresPlayer
@Usage(usage = "/sb visit <player>")
@Description(description = "Teleports you to a player's island")
public class VisitCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length == 0) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Please input a player!");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) return;

        Inventory inventory = Bukkit.createInventory(null, 36, "Visit " + target.getName());

        Util.fillEmpty(inventory);

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(target.getName());
        meta.setDisplayName(ChatColor.GREEN + "Visit player island");

        meta.setLore(Arrays.asList(Util.buildLore(
                "&7Players:\n&8 - &7" + target.getName() + "\n\n&7Profile: &eStrawberry\n\n&7Players: &a" + IslandManager.getIsland(target.getPlayer()).getPlayers().size() + "/5\n&7Server: &8island-" + target.getName().toLowerCase() + "\n\n&eClick to visit!"
        )));
        skull.setItemMeta(meta);

        inventory.setItem(13, skull);
        inventory.setItem(31, Util.buildCloseButton());

        player.openInventory(inventory);
    }
}
