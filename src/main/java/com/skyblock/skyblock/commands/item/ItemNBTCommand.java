package com.skyblock.skyblock.commands.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.*;

@RequiresPlayer
@Usage(usage = "/sb itemnbt <type> <key> <value>")
public class ItemNBTCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        NBTItem item = new NBTItem(player.getItemInHand());
        String typeArg = args[0].toLowerCase();

        String key = args[1];
        String value = args[2];

        switch (typeArg.toLowerCase()) {
            case "string":
            case "str":
                item.setString(key, value);
                break;
            case "integer":
            case "int":
                item.setInteger(key, Integer.parseInt(value));
                break;
            case "double":
            case "d":
                item.setDouble(key, Double.parseDouble(value));
                break;
            case "boolean":
            case "bool":
                item.setBoolean(key, Boolean.parseBoolean(value));
                break;
        }

        Pet pet = Pet.getPet(item.getItem());

        if (pet != null) {
            player.setItemInHand(pet.toItemStack());
            return;
        }

        try {
            ItemBase base = new ItemBase(item.getItem());
            player.setItemInHand(base.createStack());
            return;
        } catch (IllegalArgumentException e) {}

        player.setItemInHand(item.getItem());
    }
}
