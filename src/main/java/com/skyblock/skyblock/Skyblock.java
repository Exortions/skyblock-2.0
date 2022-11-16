package com.skyblock.skyblock;

import com.skyblock.skyblock.commands.item.ReforgeCommand;
import com.skyblock.skyblock.commands.misc.ClearCommand;
import com.skyblock.skyblock.commands.misc.HelpCommand;
import com.skyblock.skyblock.commands.item.ItemDataCommand;
import com.skyblock.skyblock.commands.misc.TestCommand;
import com.skyblock.skyblock.utilities.command.CommandHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")
@Getter
public final class Skyblock extends JavaPlugin {

    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        this.sendMessage("Found Bukkit server v" + Bukkit.getVersion());
        long start = System.currentTimeMillis();

        this.registerCommands();

        long end = System.currentTimeMillis();
        this.sendMessage("Successfully enabled Skyblock in " + (end - start) + "ms.");
    }

    @Override
    public void onDisable() {
        sendMessage("Disabled Skyblock!");
    }

    public void registerCommands() {
        this.sendMessage("Registering commands...");

        this.commandHandler = new CommandHandler(this,
                // add commands here
                new HelpCommand(),
                new ClearCommand(),
                new TestCommand(),
                new ItemDataCommand(),
                new ReforgeCommand()
        );

        Objects.requireNonNull(getCommand("skyblock")).setExecutor(this.commandHandler);
    }

    public void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + ChatColor.translateAlternateColorCodes('&', message));
    }

    public String getVersion() {
        return this.getDescription().getVersion();
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', "&7[&3S&bB&7] &f");
    }
}
