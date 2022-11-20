package com.skyblock.skyblock.commands.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb itemdata")
@Description(description = "Shows the skyblock data of the item in your hand.")
public class ItemDataCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (player.getItemInHand() == null) {
            player.sendMessage(plugin.getPrefix() + "You do not have an item in your hand.");
            return;
        }

        NBTItem nbt = new NBTItem(player.getItemInHand());

        if (!nbt.getBoolean("skyblockItem")) {
            player.sendMessage(plugin.getPrefix() + "This item is not a skyblock item.");
            return;
        }

        player.sendMessage(plugin.getPrefix() + "Skyblock item data:");
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Name: " + nbt.getString("name"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Rarity: " + nbt.getString("rarity"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Reforge Type: " + nbt.getInteger("reforgeType"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Reforgeable: " + nbt.getBoolean("reforgeable"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Has Ability: " + nbt.getBoolean("hasAbility"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Description: " + nbt.getString("description"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Ability Description: " + nbt.getString("abilityDescription"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Ability Name: " + nbt.getString("abilityName"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Ability Type: " + nbt.getString("abilityType"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Ability Cost: " + nbt.getInteger("abilityCost"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Ability Cooldown: " + nbt.getString("abilityCooldown"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Damage: " + nbt.getInteger("damage"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Strength: " + nbt.getInteger("strength"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Crit Chance: " + nbt.getInteger("critChance"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Crit Damage: " + nbt.getInteger("critDamage"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Attack Speed: " + nbt.getInteger("attackSpeed"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Intelligence: " + nbt.getInteger("intelligence"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Speed: " + nbt.getInteger("speed"));
        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "Defense: " + nbt.getInteger("defense"));
    }
}
