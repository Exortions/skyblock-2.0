package com.skyblock.skyblock;

import com.skyblock.skyblock.commands.item.ReforgeCommand;
import com.skyblock.skyblock.commands.misc.ClearCommand;
import com.skyblock.skyblock.commands.misc.HelpCommand;
import com.skyblock.skyblock.commands.item.ItemDataCommand;
import com.skyblock.skyblock.commands.misc.TestCommand;
import com.skyblock.skyblock.listeners.BlockBreakListener;
import com.skyblock.skyblock.listeners.HungerListener;
import com.skyblock.skyblock.listeners.PlayerListener;
import com.skyblock.skyblock.utilities.command.CommandHandler;
import com.skyblock.skyblock.utilities.gui.GuiHandler;
import com.skyblock.skyblock.utilities.item.ItemHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")
@Getter
public final class Skyblock extends JavaPlugin {

    private CommandHandler commandHandler;
    private ItemHandler itemHandler;
    private GuiHandler guiHandler;

    @Override
    public void onEnable() {
        this.sendMessage("Found Bukkit server v" + Bukkit.getVersion());
        long start = System.currentTimeMillis();

        this.initializeGameRules();
        this.initializeNEUItems();

        this.registerListeners();
        this.registerCommands();

        long end = System.currentTimeMillis();
        this.sendMessage("Successfully enabled Skyblock in " + (end - start) + "ms.");
    }

    @Override
    public void onDisable() {
        sendMessage("Disabled Skyblock!");
    }

    public void registerGuis() {
        this.guiHandler = new GuiHandler(this);

        // TODO: register guis
    }

    public void registerListeners() {
        registerListener(new BlockBreakListener());
        registerListener(new HungerListener());
        registerListener(new PlayerListener());
    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
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

    public void initializeGameRules() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doDaylightCycle false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doWeatherCycle false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doFireTick false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doMobSpawning false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doMobLoot false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doTileDrops false");
    }

    public void initializeNEUItems() {
        this.itemHandler = new ItemHandler(this);
        this.itemHandler.init();
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
