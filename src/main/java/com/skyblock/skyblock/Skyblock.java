package com.skyblock.skyblock;

import com.skyblock.skyblock.commands.enchantment.AddEnchantmentCommand;
import com.skyblock.skyblock.commands.item.ItemBrowserCommand;
import com.skyblock.skyblock.commands.item.ReforgeCommand;
import com.skyblock.skyblock.commands.menu.CollectionCommand;
import com.skyblock.skyblock.commands.menu.EnderChestCommand;
import com.skyblock.skyblock.commands.menu.MenuCommand;
import com.skyblock.skyblock.commands.misc.*;
import com.skyblock.skyblock.commands.item.ItemDataCommand;
import com.skyblock.skyblock.commands.player.PlayerDataCommand;
import com.skyblock.skyblock.commands.player.VisitCommand;
import com.skyblock.skyblock.commands.player.WarpCommand;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantmentHandler;
import com.skyblock.skyblock.features.enchantment.enchantments.TestEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntityHandler;
import com.skyblock.skyblock.features.collections.CollectionListener;
import com.skyblock.skyblock.features.items.SkyblockItemHandler;
import com.skyblock.skyblock.listeners.*;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.CommandHandler;
import com.skyblock.skyblock.utilities.data.ServerData;
import com.skyblock.skyblock.utilities.gui.GuiHandler;
import com.skyblock.skyblock.utilities.item.ItemHandler;
import com.skyblock.skyblock.utilities.time.SkyblockTimeManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@Getter
public final class Skyblock extends JavaPlugin {

    private SkyblockEnchantmentHandler enchantmentHandler;
    private SkyblockItemHandler skyblockItemHandler;
    private SkyblockEntityHandler entityHandler;
    private SkyblockTimeManager timeManager;
    private CommandHandler commandHandler;
    private ItemHandler itemHandler;
    private ServerData serverData;
    private GuiHandler guiHandler;

    @Override
    public void onEnable() {
        this.sendMessage("Found Bukkit server v" + Bukkit.getVersion());
        long start = System.currentTimeMillis();

        this.initializeServerData();
        this.registerTimeHandlers();

        this.registerEnchantments();

        this.initializeGameRules();
        this.initializeNEUItems();

        this.registerCollections();
        this.registerGuis();
        this.registerMobs();

        this.registerListeners();
        this.registerCommands();

        this.initializeAlreadyOnlinePlayers();

        long end = System.currentTimeMillis();
        this.sendMessage("Successfully enabled Skyblock in " + Util.getTimeDifferenceAndColor(start, end) + ChatColor.WHITE + ".");

    }
    @Override
    public void onDisable() {
        this.sendMessage("Disabling Skyblock...");
        long start = System.currentTimeMillis();

        this.serverData.disable();

        sendMessage("Successfully disabled Skyblock [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeAlreadyOnlinePlayers() {
        this.sendMessage("Reloading already online players...");

        for (Player player : Bukkit.getOnlinePlayers()) {
            Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, ""));
        }

        this.sendMessage("Successfully reloaded already online players.");
    }

    public void registerTimeHandlers() {
        this.sendMessage("Registering time handlers...");
        long start = System.currentTimeMillis();

        this.timeManager = new SkyblockTimeManager(this);

        this.sendMessage("Successfully registered time handlers [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeServerData() {
        this.sendMessage("Initializing server data...");

        long start = System.currentTimeMillis();

        this.serverData = new ServerData(this);

        this.sendMessage("Sucessfully initialized server data [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerGuis() {
        this.sendMessage("Registering guis...");
        long start = System.currentTimeMillis();

        this.guiHandler = new GuiHandler(this);

        this.guiHandler.registerGuiCommand("collection", "sb collection");
        this.guiHandler.registerGuiCommand("skyblock_menu", "sb menu");
        this.guiHandler.registerGuiCommand("ender_chest", "sb enderchest");

        this.sendMessage("Successfully registered guis [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerEnchantments() {
        this.sendMessage("Registering enchantments...");

        long start = System.currentTimeMillis();

        this.enchantmentHandler = new SkyblockEnchantmentHandler(this);

        this.enchantmentHandler.registerEnchantment(new TestEnchantment());

        this.sendMessage("Successfully registered enchantments [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerMobs() {
        this.sendMessage("Registering mobs...");
        long start = System.currentTimeMillis();

        this.entityHandler = new SkyblockEntityHandler();

        this.sendMessage("Successfully registered mobs [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerCollections() {
        long start = System.currentTimeMillis();
        if (!Collection.INITIALIZED) Collection.initializeCollections(this);
    }

    private int registeredListeners = 0;

    public void registerListeners() {
        this.sendMessage("Registering listeners...");

        long start = System.currentTimeMillis();

        registerListener(new BlockBreakListener());
        registerListener(new HungerListener());
        registerListener(new PlayerListener(this));
        registerListener(new SkyblockMenuListener(this));
        registerListener(new PlayerJoinListener());
        registerListener(new CollectionListener());
        registerListener(new ItemListener(this));
        registerListener(new VisitMenuListener());
        registerListener(new EnderChestListener());

        this.sendMessage("Successfully registered " + ChatColor.GREEN + registeredListeners + ChatColor.WHITE + " listeners [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);

        this.registeredListeners++;
    }

    public void registerCommands() {
        this.sendMessage("Registering commands...");
        long start = System.currentTimeMillis();

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
                new AddEnchantmentCommand(),
                new SummonCommand(),
                new WarpCommand(),
                new VisitCommand(),
                new EnderChestCommand()
        );

        Objects.requireNonNull(getCommand("skyblock")).setExecutor(this.commandHandler);

        this.sendMessage("Successfully registered commands [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeGameRules() {
        this.sendMessage("Initializing game rules...");
        long start = System.currentTimeMillis();
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

        this.sendMessage("Successfully initialized game rules [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeNEUItems() {
        this.sendMessage("Initializing items...");
        long start = System.currentTimeMillis();
        this.itemHandler = new ItemHandler(this);
        this.itemHandler.init();

        this.skyblockItemHandler = new SkyblockItemHandler(this);

        this.sendMessage("Successfully initialized items [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + ChatColor.translateAlternateColorCodes('&', message) + ChatColor.RESET + " ");
    }

    public String getVersion() {
        return this.getDescription().getVersion();
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', "&7[&3S&bB&7] &f");
    }
}
