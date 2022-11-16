package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import de.tr7zw.nbtapi.NBTItem;
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

        player.sendMessage(plugin.getPrefix() + "Name: " + nbt.getString("name"));
        player.sendMessage(plugin.getPrefix() + "Rarity: " + nbt.getString("rarity"));
        player.sendMessage(plugin.getPrefix() + "Reforge Type: " + nbt.getInteger("reforgeType"));
        player.sendMessage(plugin.getPrefix() + "Reforgeable: " + nbt.getBoolean("reforgeable"));
        player.sendMessage(plugin.getPrefix() + "Has Ability: " + nbt.getBoolean("hasAbility"));
        player.sendMessage(plugin.getPrefix() + "Ability Name: " + nbt.getString("abilityName"));
        player.sendMessage(plugin.getPrefix() + "Ability Type: " + nbt.getString("abilityType"));
        player.sendMessage(plugin.getPrefix() + "Ability Cost: " + nbt.getInteger("abilityCost"));
        player.sendMessage(plugin.getPrefix() + "Ability Cooldown: " + nbt.getString("abilityCooldown"));
        player.sendMessage(plugin.getPrefix() + "Damage: " + nbt.getInteger("damage"));
        player.sendMessage(plugin.getPrefix() + "Strength: " + nbt.getInteger("strength"));
        player.sendMessage(plugin.getPrefix() + "Crit Chance: " + nbt.getInteger("critChance"));
        player.sendMessage(plugin.getPrefix() + "Crit Damage: " + nbt.getInteger("critDamage"));
        player.sendMessage(plugin.getPrefix() + "Attack Speed: " + nbt.getInteger("attackSpeed"));
        player.sendMessage(plugin.getPrefix() + "Intelligence: " + nbt.getInteger("intelligence"));
        player.sendMessage(plugin.getPrefix() + "Speed: " + nbt.getInteger("speed"));
        player.sendMessage(plugin.getPrefix() + "Defense: " + nbt.getInteger("defense"));
    }
}
