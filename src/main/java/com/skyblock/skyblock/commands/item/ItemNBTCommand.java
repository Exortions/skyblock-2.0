package com.skyblock.skyblock.commands.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Alias;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;

@RequiresPlayer
@Alias(aliases = "nbt")
@Usage(usage = "/sb itemnbt <action> <type> <key> [value]")
public class ItemNBTCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        NBTItem item = new NBTItem(player.getItemInHand());
        String action = args[0];

        String typeArg = args[1].toLowerCase();
        String key = args[2];

        if (action.equalsIgnoreCase("set")) {
            String value = args[3];

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

            try {
                Pet pet = Pet.getPet(item.getItem());

                if (pet != null) {
                    player.setItemInHand(pet.toItemStack());
                    return;
                }
            } catch (Exception ignored) {}

            try {
                ItemBase base = new ItemBase(item.getItem());
                player.setItemInHand(base.createStack());
                return;
            } catch (IllegalArgumentException ignored) {}

            player.setItemInHand(item.getItem());
        } else if (action.equalsIgnoreCase("get")) {
            String result = "";

            switch (typeArg.toLowerCase()) {
                case "string":
                case "str":
                    result = item.getString(key);
                    break;
                case "integer":
                case "int":
                    result = String.valueOf(item.getInteger(key));
                    break;
                case "double":
                case "d":
                    result = String.valueOf(item.getDouble(key));
                    break;
                case "boolean":
                case "bool":
                    result = String.valueOf(item.getBoolean(key));
                    break;
            }

            player.sendMessage("Value: " + result);
        }
    }
}
