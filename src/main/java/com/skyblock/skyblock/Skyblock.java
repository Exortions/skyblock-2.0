package com.skyblock.skyblock;

import com.skyblock.skyblock.commands.enchantment.AddEnchantmentCommand;
import com.skyblock.skyblock.commands.item.ItemBrowserCommand;
import com.skyblock.skyblock.commands.item.ReforgeCommand;
import com.skyblock.skyblock.commands.menu.CollectionCommand;
import com.skyblock.skyblock.commands.menu.MenuCommand;
import com.skyblock.skyblock.commands.misc.ClearCommand;
import com.skyblock.skyblock.commands.misc.GuiCommand;
import com.skyblock.skyblock.commands.misc.HelpCommand;
import com.skyblock.skyblock.commands.item.ItemDataCommand;
import com.skyblock.skyblock.commands.misc.TestCommand;
import com.skyblock.skyblock.commands.player.PlayerDataCommand;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantmentHandler;
import com.skyblock.skyblock.features.enchantment.enchantments.TestEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntityHandler;
import com.skyblock.skyblock.features.collections.CollectionListener;
import com.skyblock.skyblock.features.items.SkyblockItemHandler;
import com.skyblock.skyblock.listeners.*;
import com.skyblock.skyblock.utilities.command.CommandHandler;
import com.skyblock.skyblock.utilities.gui.GuiHandler;
import com.skyblock.skyblock.utilities.item.ItemHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@Getter
public final class Skyblock extends JavaPlugin {

    private SkyblockEnchantmentHandler enchantmentHandler;
    private SkyblockItemHandler skyblockItemHandler;
    private SkyblockEntityHandler entityHandler;
    private CommandHandler commandHandler;
    private ItemHandler itemHandler;
    private GuiHandler guiHandler;

    @Override
    public void onEnable() {
        this.sendMessage("Found Bukkit server v" + Bukkit.getVersion());
        long start = System.currentTimeMillis();

        this.registerEnchantments();

        this.initializeGameRules();
        this.initializeNEUItems();

        this.registerCollections();
        this.registerGuis();
        this.registerMobs();

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

        this.guiHandler.registerGuiCommand("collection", "sb collection");
        this.guiHandler.registerGuiCommand("skyblock_menu", "sb menu");
    }

    public void registerEnchantments() {
        this.enchantmentHandler = new SkyblockEnchantmentHandler(this);

        this.enchantmentHandler.registerEnchantment(new TestEnchantment());
    }

    public void registerMobs() {
        this.entityHandler = new SkyblockEntityHandler();
    }

    public void registerCollections() {
        if (!Collection.INITIALIZED) Collection.initializeCollections(this);
    }

    public void registerListeners() {
        registerListener(new BlockBreakListener());
        registerListener(new HungerListener());
        registerListener(new PlayerListener(this));
        registerListener(new SkyblockMenuListener(this));
        registerListener(new PlayerJoinListener());
        registerListener(new CollectionListener());
        registerListener(new ItemListener(this));
    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void registerCommands() {
        this.sendMessage("Registering commands...");

        this.commandHandler = new CommandHandler(this,
                new HelpCommand(),
                new ClearCommand(),
                new TestCommand(),
                new ItemDataCommand(),
                new ReforgeCommand(),
                new ItemBrowserCommand(),
                new GuiCommand(),
                new CollectionCommand(),
                new MenuCommand(),
                new PlayerDataCommand(),
                new AddEnchantmentCommand()
        );

        Objects.requireNonNull(getCommand("skyblock")).setExecutor(this.commandHandler);
    }

    public void initializeGameRules() {
        List<World> worlds = Bukkit.getWorlds();
        
        for (World world : worlds) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doWeatherCycle", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doMobLoot", "false");
            world.setGameRuleValue("doTileDrops", "false");
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setGameRuleValue("showDeathMessages", "false");
        }
    }

    public void initializeNEUItems() {
        this.itemHandler = new ItemHandler(this);
        this.itemHandler.init();

        this.skyblockItemHandler = new SkyblockItemHandler(this);
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
