package com.skyblock.skyblock.utilities.command;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public interface TrueAlias<T extends Command> {

    default void register() {
        T command;

        try {
            command = (T) this.getClass().newInstance();
        } catch (Exception ex) {
            Skyblock.getPlugin().sendMessage("&cCould not register command alias: " + this.getClass().getName() + " because it does not implement com.skyblock.skyblock.utilities.command.Command.");

            return;
        }

        Skyblock.getPlugin().getCommand(command.name().toLowerCase()).setExecutor(
                (sender, command1, label, args) -> {
                    if (sender instanceof Player) ((Player) sender).performCommand("sb " + command.name().toLowerCase() + " " + String.join(" ", args));
                    else Bukkit.dispatchCommand(sender, "sb " + command.name().toLowerCase() + " " + String.join(" ", args));

                    return false;
                }
        );

        if (command instanceof ArgumentAlias) {
            ArgumentAlias annotation = (ArgumentAlias) command;

            HashMap<String, String> aliases = annotation.getArgumentAliases();

            for (String cmd : aliases.keySet()) {
                Skyblock.getPlugin().getCommand(cmd).setExecutor(
                        (sender, command1, label, args) -> {
                            if (sender instanceof Player) ((Player) sender).performCommand("sb " + command.name().toLowerCase() + " " + aliases.get(cmd));
                            else Bukkit.dispatchCommand(sender, "sb " + command.name().toLowerCase() + " " + aliases.get(cmd));

                            return false;
                        }
                );
            }
        }
    }

}
