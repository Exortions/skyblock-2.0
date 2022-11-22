package com.skyblock.skyblock;

import com.skyblock.skyblock.commands.economy.DepositCommand;
import com.skyblock.skyblock.commands.economy.WithdrawCommand;
import com.skyblock.skyblock.commands.enchantment.AddEnchantmentCommand;
import com.skyblock.skyblock.commands.item.ItemBrowserCommand;
import com.skyblock.skyblock.commands.item.ItemCommand;
import com.skyblock.skyblock.commands.item.ReforgeCommand;
import com.skyblock.skyblock.commands.menu.CollectionCommand;
import com.skyblock.skyblock.commands.menu.CraftCommand;
import com.skyblock.skyblock.commands.menu.EnderChestCommand;
import com.skyblock.skyblock.commands.menu.MenuCommand;
import com.skyblock.skyblock.commands.menu.npc.BankerCommand;
import com.skyblock.skyblock.commands.merchant.SpawnMerchantCommand;
import com.skyblock.skyblock.commands.misc.*;
import com.skyblock.skyblock.commands.item.ItemDataCommand;
import com.skyblock.skyblock.commands.player.PlayerDataCommand;
import com.skyblock.skyblock.commands.player.VisitCommand;
import com.skyblock.skyblock.commands.player.WarpCommand;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.crafting.RecipeHandler;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantmentHandler;
import com.skyblock.skyblock.features.enchantment.enchantments.TestEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntityHandler;
import com.skyblock.skyblock.features.collections.CollectionListener;
import com.skyblock.skyblock.features.items.SkyblockItemHandler;
import com.skyblock.skyblock.features.launchpads.LaunchPadHandler;
import com.skyblock.skyblock.features.location.SkyblockLocationManager;
import com.skyblock.skyblock.features.merchants.Merchant;
import com.skyblock.skyblock.features.merchants.MerchantHandler;
import com.skyblock.skyblock.features.npc.NPC;
import com.skyblock.skyblock.features.npc.NPCHandler;
import com.skyblock.skyblock.features.reforge.ReforgeHandler;
import com.skyblock.skyblock.listeners.*;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.CommandHandler;
import com.skyblock.skyblock.utilities.data.ServerData;
import com.skyblock.skyblock.utilities.gui.GuiHandler;
import com.skyblock.skyblock.utilities.item.ItemHandler;
import com.skyblock.skyblock.features.time.SkyblockTimeManager;
import lombok.Getter;
import net.citizensnpcs.api.event.DespawnReason;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("unused")
@Getter
public final class Skyblock extends JavaPlugin {

    private SkyblockEnchantmentHandler enchantmentHandler;
    private SkyblockLocationManager locationManager;
    private SkyblockItemHandler skyblockItemHandler;
    private SkyblockEntityHandler entityHandler;
    private LaunchPadHandler launchPadHandler;
    private MerchantHandler merchantHandler;
    private SkyblockTimeManager timeManager;
    private CommandHandler commandHandler;
    private ReforgeHandler reforgeHandler;
    private RecipeHandler recipeHandler;
    private ItemHandler itemHandler;
    private NPCHandler npcHandler;
    private ServerData serverData;
    private GuiHandler guiHandler;

    private Random random;
    private Date date;

    @Override
    public void onEnable() {
        this.sendMessage("Found Bukkit server v" + Bukkit.getVersion());
        long start = System.currentTimeMillis();

        this.initializeServerData();
        this.registerTimeHandlers();

        this.registerReforges();

        this.registerEnchantments();

        this.registerLocations();

        this.initializeRecipes();
        this.initializeGameRules();
        this.initializeNEUItems();

        this.registerMerchants();

        this.registerCollections();
        this.registerNpcs();
        this.registerGuis();
        this.registerMobs();
        this.registerLaunchPads();

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

        for (Merchant merchant : this.merchantHandler.getMerchants().values()) {
            merchant.getNpc().destroy();
            merchant.getStand().remove();
            merchant.getClick().remove();

            merchant.getNpc().getOwningRegistry().despawnNPCs(DespawnReason.PLUGIN);
            merchant.getNpc().getOwningRegistry().deregisterAll();
        }

        this.npcHandler.killAll();

        File file = new File("plugins/Citizens/saves.yml");
        if (file.exists()) file.delete();

        this.serverData.disable();

        sendMessage("Successfully disabled Skyblock [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerReforges() {
        this.sendMessage("Registering reforges...");
        long start = System.currentTimeMillis();

        this.reforgeHandler = new ReforgeHandler(this);

        this.sendMessage("Successfully registered &a" + this.reforgeHandler.getReforges().size() + "&f reforges [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerLaunchPads() {
        this.sendMessage("Registering launch pads...");
        long start = System.currentTimeMillis();

        this.launchPadHandler = new LaunchPadHandler();

        this.sendMessage("Successfully registered launch pads [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerNpcs() {
        this.sendMessage("Registering NPCs...");
        long start = System.currentTimeMillis();

        this.npcHandler = new NPCHandler();

        // TODO: Load from file
        this.npcHandler.registerNPC(
                "banker",
                new NPC(
                        "Banker",
                        true,
                        true,
                        false,
                        null,
                        new Location(Bukkit.getWorld("world"), 20.5, 71, -40.5),
                        (player) -> this.getGuiHandler().show("banker", player),
                        "ewogICJ0aW1lc3RhbXAiIDogMTY1NTg0NTIwODg3OSwKICAicHJvZmlsZUlkIiA6ICI2NmI0ZDRlMTFlNmE0YjhjYTFkN2Q5YzliZTBhNjQ5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcmFzdG9vWXNmIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzYyYTExMGIwMmVmYzU4ZjJiYTc3YWVlZjE3ZGY3ZTMyOWQ4OTZjNDU5MDI0NDIzMzg0OWY0MmRhMDIzMjhhOSIKICAgIH0KICB9Cn0=",
                        "EBEcNTFPKGK8a1kGPyV0rHzZlwjp3s6jH3NBpVnrt0dgiieIChfiknBr8AAeC6Petrw1YAeHPwq5hC358BLggCNQQOgcJ0vcrZpISSPMfxi03WliH7lY6l5kboc6ht1vEdAZgCt/Sn9mKXqw4DzuHK2+2kl1hPkBX3rE5swVcqm9e/xLGsftE6NWWVpxw90YobRYF3NMzHX4PlFXHpndbDdMaPMTIAwSjDyR+scuOJKgV8tVYRp27aGBKevJXafYxxg9v8P06rFYif6DlyhDgU5/qnwFZdxnYUPrT7CeyLKptxPUzjy+G9iOiH7rkSJwkj22zk4BEdrcmAL0jNFr4dXq9n9d9MFtZ6KEqjBwPfB1T5ixMYS6tdmnbZYSamFAKUuKv1Jxs6WqwS3FesA7lALNNuZfXdsWaSBlT7d+TCsqjhlUccOEW5KyeLdgBsmACiPfQ+EGH6NET+plxDAdoVU21YPJJosqHvWR5+RZUlaXZIEXnPfeN/2BzYjoQVktn1T44Qdv0MfYerfDG0GsyrVAMcoi6I2zzB97OeQi/eUtOxv4KIvTHLtmULJtvrr6jqeodg+RoL9twIPLfG/+CBm9lznYnp5kIJxIGCUJ8fk7mzSnO5vW/Ej0vxADYYwpJStrkapaspWe1LNRGEqYBw2kTnk10wFQiVeYdhTJH1I="
                )
        );

        this.npcHandler.registerNPC(
                "blacksmith",
                new NPC(
                        "Blacksmith",
                        true,
                        false,
                        true,
                        Villager.Profession.BLACKSMITH,
                        new Location(Bukkit.getWorld("world"), -19.5, 71, -124.5),
                        (player) -> this.getGuiHandler().show("reforge", player),
                        "",
                        ""
                )
        );

        this.npcHandler.spawnAll();

        this.sendMessage("Successfully registered NPCs [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerMerchants() {
        this.sendMessage("Registering merchants...");
        long start = System.currentTimeMillis();

        this.merchantHandler = new MerchantHandler(this);

        for (Merchant merchant : this.merchantHandler.getMerchants().values()) {
            merchant.createNpc();
        }

        this.sendMessage("Successfully registered merchants [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeRecipes() {
        this.recipeHandler = new RecipeHandler();

        this.recipeHandler.init();
    }

    public void registerLocations() {
        this.sendMessage("Registering locations...");
        long start = System.currentTimeMillis();

        this.locationManager = new SkyblockLocationManager(this);

        this.sendMessage("Successfully registered &a" + this.locationManager.getLocations().size() + " &flocations [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
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

        this.date = new Date();
        this.date.setHours(0);
        this.date.setMinutes(0);
        this.date.setSeconds(0);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - date.getTime() >= 86400000) {
                    date = new Date();
                    date.setHours(0);
                    date.setMinutes(0);
                    date.setSeconds(0);
                }
            }
        }.runTaskTimer(this, 0, 200);

        this.sendMessage("Successfully registered time handlers [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeServerData() {
        this.sendMessage("Initializing server data...");

        long start = System.currentTimeMillis();

        this.serverData = new ServerData(this);

        this.random = new Random();

        this.sendMessage("Sucessfully initialized server data [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerGuis() {
        this.sendMessage("Registering guis...");
        long start = System.currentTimeMillis();

        this.guiHandler = new GuiHandler(this);

        this.guiHandler.registerGuiCommand("collection", "sb collection");
        this.guiHandler.registerGuiCommand("skyblock_menu", "sb menu");
        this.guiHandler.registerGuiCommand("ender_chest", "sb enderchest");
        this.guiHandler.registerGuiCommand("crafting_table", "sb craft");
        this.guiHandler.registerGuiCommand("banker", "sb banker");
        this.guiHandler.registerGuiCommand("reforge", "sb reforge");

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

        registerListener(new BlockListener());
        registerListener(new HungerListener());
        registerListener(new PlayerListener(this));
        registerListener(new SkyblockMenuListener(this));
        registerListener(new PlayerJoinListener());
        registerListener(new CollectionListener());
        registerListener(new ItemListener(this));
        registerListener(new VisitMenuListener());
        registerListener(new EnderChestListener());
        registerListener(new BankerListener());
        registerListener(new ReforgeListener());

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
                new EnderChestCommand(),
                new CreateLocationCommand(),
                new SpawnMerchantCommand(),
                new CraftCommand(),
                new ItemCommand(),
                new BankerCommand(),
                new DepositCommand(),
                new WithdrawCommand(),
                new ReloadCommand()
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
