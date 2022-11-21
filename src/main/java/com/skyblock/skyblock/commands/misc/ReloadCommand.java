package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.command.CommandSender;

@Usage(usage = "/sb reload")
@Description(description = "Reloads Skyblock")
public class ReloadCommand implements Command {

    @Override
    public void execute(CommandSender sender, String[] args, Skyblock plugin) {
        plugin.onDisable();

        plugin.onEnable();

        sender.sendMessage(plugin.getPrefix() + "Successfully reloaded Skyblock");
    }
}
