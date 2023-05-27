package com.skyblock.skyblock;

import com.skyblock.skyblock.commands.admin.*;
import com.skyblock.skyblock.commands.economy.AuctionCommand;
import com.skyblock.skyblock.commands.economy.BazaarCommand;
import com.skyblock.skyblock.commands.economy.DepositCommand;
import com.skyblock.skyblock.commands.economy.WithdrawCommand;
import com.skyblock.skyblock.commands.enchantment.EnchantCommand;
import com.skyblock.skyblock.commands.game.HarpCommand;
import com.skyblock.skyblock.commands.item.*;
import com.skyblock.skyblock.commands.menu.*;
import com.skyblock.skyblock.commands.menu.npc.BankerCommand;
import com.skyblock.skyblock.commands.merchant.SpawnMerchantCommand;
import com.skyblock.skyblock.commands.misc.*;
import com.skyblock.skyblock.commands.player.*;
import com.skyblock.skyblock.commands.potion.CreatePotionCommand;
import com.skyblock.skyblock.commands.potion.EffectCommand;
import com.skyblock.skyblock.commands.potion.EffectsCommand;
import com.skyblock.skyblock.features.auction.AuctionBid;
import com.skyblock.skyblock.features.auction.AuctionHouse;
import com.skyblock.skyblock.features.auction.AuctionSettings;
import com.skyblock.skyblock.features.bags.Bag;
import com.skyblock.skyblock.features.bags.BagManager;
import com.skyblock.skyblock.features.bazaar.Bazaar;
import com.skyblock.skyblock.features.bazaar.impl.SkyblockBazaar;
import com.skyblock.skyblock.features.blocks.RegenerativeBlockHandler;
import com.skyblock.skyblock.features.blocks.SpongeBlock;
import com.skyblock.skyblock.features.blocks.SpongeReplacer;
import com.skyblock.skyblock.features.blocks.SpongeReplacerHandler;
import com.skyblock.skyblock.features.blocks.crops.FloatingCrystalHandler;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.collections.CollectionListener;
import com.skyblock.skyblock.features.crafting.RecipeHandler;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantmentHandler;
import com.skyblock.skyblock.features.enchantment.enchantments.armor.FireProtectionEnchantment;
import com.skyblock.skyblock.features.enchantment.enchantments.armor.GrowthEnchantment;
import com.skyblock.skyblock.features.enchantment.enchantments.armor.ProtectionEnchantment;
import com.skyblock.skyblock.features.enchantment.enchantments.bow.AimingEnchantment;
import com.skyblock.skyblock.features.enchantment.enchantments.misc.TelekinesisEnchantment;
import com.skyblock.skyblock.features.enchantment.enchantments.sword.*;
import com.skyblock.skyblock.features.enchantment.enchantments.tool.EfficiencyEnchantment;
import com.skyblock.skyblock.features.entities.EntityListener;
import com.skyblock.skyblock.features.entities.SkyblockEntityHandler;
import com.skyblock.skyblock.features.entities.dragon.DragonAltar;
import com.skyblock.skyblock.features.entities.dragon.DragonSequence;
import com.skyblock.skyblock.features.entities.spawners.EntitySpawnerHandler;
import com.skyblock.skyblock.features.fairysouls.FairySoulHandler;
import com.skyblock.skyblock.features.fairysouls.TiaGUI;
import com.skyblock.skyblock.features.guis.GuyGui;
import com.skyblock.skyblock.features.guis.LiftOperatorGui;
import com.skyblock.skyblock.features.guis.SeymourGui;
import com.skyblock.skyblock.features.holograms.HologramManager;
import com.skyblock.skyblock.features.items.Accessory;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.features.items.SkyblockItemHandler;
import com.skyblock.skyblock.features.launchpads.LaunchPadHandler;
import com.skyblock.skyblock.features.location.SkyblockLocationManager;
import com.skyblock.skyblock.features.merchants.Merchant;
import com.skyblock.skyblock.features.merchants.MerchantHandler;
import com.skyblock.skyblock.features.minions.MinionHandler;
import com.skyblock.skyblock.features.minions.MinionListener;
import com.skyblock.skyblock.features.minions.items.MinionItemHandler;
import com.skyblock.skyblock.features.npc.NPC;
import com.skyblock.skyblock.features.npc.NPCHandler;
import com.skyblock.skyblock.features.objectives.QuestLineHandler;
import com.skyblock.skyblock.features.pets.PetListener;
import com.skyblock.skyblock.features.potions.PotionEffectHandler;
import com.skyblock.skyblock.features.potions.effects.HasteEffect;
import com.skyblock.skyblock.features.potions.effects.HealingEffect;
import com.skyblock.skyblock.features.potions.effects.SpeedEffect;
import com.skyblock.skyblock.features.potions.effects.StrengthEffect;
import com.skyblock.skyblock.features.reforge.ReforgeHandler;
import com.skyblock.skyblock.features.slayer.SlayerHandler;
import com.skyblock.skyblock.features.time.SkyblockTimeManager;
import com.skyblock.skyblock.features.trades.TradeHandler;
import com.skyblock.skyblock.listeners.*;
import com.skyblock.skyblock.updater.DependencyUpdater;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.CommandHandler;
import com.skyblock.skyblock.utilities.data.ServerData;
import com.skyblock.skyblock.utilities.gui.GuiHandler;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemHandler;
import com.skyblock.skyblock.utilities.sign.SignManager;
import de.tr7zw.nbtapi.NBTEntity;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

@Getter
@SuppressWarnings({"unused", "deprecation"})
public final class Skyblock extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(MinionHandler.MinionSerializable.class, "Minion");
        ConfigurationSerialization.registerClass(AuctionSettings.class, "AuctionSettings");
        ConfigurationSerialization.registerClass(AuctionBid.class, "AuctionBid");
    }

    private RegenerativeBlockHandler regenerativeBlockHandler;
    private FloatingCrystalHandler floatingCrystalHandler;
    private SkyblockEnchantmentHandler enchantmentHandler;
    private SpongeReplacerHandler spongeReplacerHandler;
    private EntitySpawnerHandler entitySpawnerHandler;
    private SkyblockLocationManager locationManager;
    private PotionEffectHandler potionEffectHandler;
    private SkyblockItemHandler skyblockItemHandler;
    private MinionItemHandler minionItemHandler;
    private SkyblockEntityHandler entityHandler;
    private LaunchPadHandler launchPadHandler;
    private QuestLineHandler questLineHandler;
    private FairySoulHandler fairySoulHandler;
    private HologramManager hologramManager;
    private MerchantHandler merchantHandler;
    private SkyblockTimeManager timeManager;
    private CommandHandler commandHandler;
    private ReforgeHandler reforgeHandler;
    private MinionHandler minionHandler;
    private SlayerHandler slayerHandler;
    private RecipeHandler recipeHandler;
    private TradeHandler tradeHandler;
    private AuctionHouse auctionHouse;
    private List<Entity> removeables;
    private SignManager signManager;
    private ItemHandler itemHandler;
    private BagManager bagManager;
    private NPCHandler npcHandler;
    private ServerData serverData;
    private GuiHandler guiHandler;
    private Bazaar bazaar;
    private Random random;
    private Date date;

    private int registeredListeners = 0;

    @Override
    public void onEnable() {
        this.sendMessage("Found Bukkit server v" + Bukkit.getVersion());
        long start = System.currentTimeMillis();

        new DependencyUpdater(this).update();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc remove all");

        Bukkit.createWorld(new WorldCreator("deep_caverns").type(WorldType.FLAT).generator(new ChunkGenerator() {
            @Override
            public byte[] generate(World world, Random random, int x, int z) {
                return new byte[32768];
            }
        }));

        this.removeables = new ArrayList<>();

        this.initializeServerData();
        this.registerTimeHandlers();

        this.registerReforges();
        this.registerEnchantments();
        this.registerLocations();
        this.registerMinions();

        this.initializePotions();
        this.initializeRecipes();
        this.initializeGameRules();
        this.initializeNEUItems();
        this.initializeFairySouls();
        this.initializeAuctionHouse();
        this.initializeSignGui();
        this.initializeBazaar();
        this.initializeSpongeReplacers();
        this.initializeFloatingCrystals();

        this.registerMerchants();
        this.registerTrades();
        this.registerCollections();
        this.registerNpcs();
        this.registerGuis();
        this.registerMobs();
        this.registerSlayers();
        this.registerLaunchPads();

        this.initializeQuests();

        this.registerBags();

        this.registerBlockHandler();

        this.registerHolograms();

        this.registerListeners();
        this.registerCommands();

        this.npcHandler.spawnAll();

        this.initializeAlreadyOnlinePlayers();

        long end = System.currentTimeMillis();
        this.sendMessage("Successfully enabled Skyblock in " + Util.getTimeDifferenceAndColor(start, end) + ChatColor.WHITE + ".");
    }

    @Override
    public void onDisable() {
        this.sendMessage("Disabling Skyblock...");
        long start = System.currentTimeMillis();

        int i = 0;

        File cacheFile = new File(this.getDataFolder(), ".cache.yml");
        FileConfiguration cache = YamlConfiguration.loadConfiguration(cacheFile);

        List<String> removeablesList = new ArrayList<>();

        for (Entity entity : removeables) {
            UUID uuid = entity.getUniqueId();

            removeablesList.add(uuid.toString());

            i++;
        }

        cache.set("removeables", removeablesList);
        try {
            cache.save(cacheFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        sendMessage(String.format("Removed %s Entities", i));

        this.spongeReplacerHandler.endGeneration();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc remove all");

        this.auctionHouse.saveToDisk();

        this.hologramManager.despawnAll();

        File file = new File("plugins/Citizens/saves.yml");

        if (file.exists()) {
            file.delete();
            sendMessage("Deleted citizens file");
        }

        this.serverData.disable();

        for (SkyblockPlayer skyblockPlayer : SkyblockPlayer.playerRegistry.values()) {
            skyblockPlayer.onQuit();
        }

//        try {
//            DragonAltar.getMainAltar().onDisable();
//            DragonSequence.endingSequence();
//        } catch (Exception ignored) { }

        sendMessage("Successfully disabled Skyblock [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerTrades() {
        this.sendMessage("Registering trades...");
        long start = System.currentTimeMillis();

        this.tradeHandler = new TradeHandler(this);

        this.sendMessage("Successfully registered &a" + this.tradeHandler.getTrades().size() + " &ftrades [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializePotions() {
        this.sendMessage("Initializing potions...");
        long start = System.currentTimeMillis();

        this.potionEffectHandler = new PotionEffectHandler(
                SpeedEffect.class,
                HealingEffect.class,
                StrengthEffect.class,
                HasteEffect.class
        );

        this.sendMessage("Successfully initialized potions [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeFloatingCrystals() {
        this.sendMessage("Initializing floating crystals...");
        long start = System.currentTimeMillis();

        this.floatingCrystalHandler = new FloatingCrystalHandler();

        this.sendMessage("Successfully initialized floating crystals [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerHolograms() {
        this.sendMessage("Registering Holograms...");
        long start = System.currentTimeMillis();

        this.hologramManager = new HologramManager();

        this.hologramManager.spawnAll();

        this.sendMessage("Successfully registered holograms [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    private void initializeQuests() {
        this.sendMessage("Initializing quests...");
        long start = System.currentTimeMillis();

        this.questLineHandler = new QuestLineHandler();

        this.sendMessage("Successfully registered " + ChatColor.GREEN + this.questLineHandler.getQuests().size() + ChatColor.WHITE + " quests [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeBazaar() {
        this.sendMessage("Initializing bazaar...");
        long start = System.currentTimeMillis();

        try {
            this.bazaar = new SkyblockBazaar();
        } catch (Bazaar.BazaarIOException | Bazaar.BazaarItemNotFoundException ex) {
            this.sendMessage("&cFailed to initialize bazaar: &8" + ex.getMessage());
        }

        this.sendMessage("Successfully initialized bazaar [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeSignGui() {
        this.signManager = new SignManager(this);
    }

    public void initializeAuctionHouse() {
        this.sendMessage("Initializing auction house...");
        long start = System.currentTimeMillis();

        this.auctionHouse = new AuctionHouse();

        this.sendMessage("Successfully initialized auction house [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerMinions() {
        this.sendMessage("Registering minions...");
        long start = System.currentTimeMillis();

        this.minionHandler = new MinionHandler();

        this.sendMessage("Successfully registered minions [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeFairySouls() {
        this.sendMessage("Registering fairy souls...");
        long start = System.currentTimeMillis();

        this.fairySoulHandler = new FairySoulHandler();

        this.sendMessage("Successfully registered " + ChatColor.GREEN + this.fairySoulHandler.getSouls().size() + ChatColor.WHITE + " fairy souls ["  + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerBlockHandler() {
        this.sendMessage("Registering blocks...");
        long start = System.currentTimeMillis();

        this.regenerativeBlockHandler = new RegenerativeBlockHandler();

        this.sendMessage("Successfully registered block handler [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerSlayers() {
        this.sendMessage("Registering slayers...");
        long start = System.currentTimeMillis();

        this.slayerHandler = new SlayerHandler();

        this.sendMessage("Successfully registered slayers [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerBags() {
        this.sendMessage("Registering bags...");
        long start = System.currentTimeMillis();

        this.bagManager = new BagManager();

        this.bagManager.registerBag(
                new Bag(
                        "accessory_bag",
                        "Accessory Bag",
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYxYTkxOGMwYzQ5YmE4" +
                                "ZDA1M2U1MjJjYjkxYWJjNzQ2ODkzNjdiNGQ4YWEwNmJmYzFiYTkxNTQ3MzA5ODVmZiJ9fX0=",
                        "&7A special bag which can hold\n&7Talismans, Rings, Artifacts and\n&7Orbs within it. All will still\n&7work while in this bag!",
                        53,
                        (stack -> {
                            ItemBase base;

                            try {
                                base = new ItemBase(stack);
                            } catch (Exception ex) {
                                return false;
                            }

                            return base.getRarity().toUpperCase().contains("ACCESSORY");
                        }),
                        (player, inventory) -> player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.HORSE_ARMOR, 10, 0),
                        (player, itemStack) -> {
                            Accessory.onEquip(itemStack, player);

                            if (!skyblockItemHandler.isRegistered(itemStack)) return;
                            SkyblockItem item = skyblockItemHandler.getRegistered(itemStack);

                            if (item instanceof Accessory) {
                                Accessory accessory = (Accessory) item;
                                accessory.onEquip(player);
                            }
                        },
                        (player, itemStack) -> {
                            Accessory.onUnEquip(itemStack, player);

                            if (!skyblockItemHandler.isRegistered(itemStack)) return;
                            SkyblockItem item = skyblockItemHandler.getRegistered(itemStack);

                            if (item instanceof Accessory) {
                                Accessory accessory = (Accessory) item;
                                accessory.onUnEquip(player);
                            }
                        }
                )
        );

        this.bagManager.registerBag(
                new Bag(
                        "quiver",
                        "Quiver",
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNiM2FjZGMxMWNhNzQ3YmY3MTBlNTlmNGM4ZTliM2Q5NDlmZGQzNjRjNjg2OTgzMWNhODc4ZjA3NjNkMTc4NyJ9fX0=",
                        "&7A masterfully crafted Quiver\n&7which holds any kind of\n&7projectile you can think of!",
                        44,
                        (stack -> stack.getType().equals(Material.ARROW) || stack.getType().equals(Material.PRISMARINE_SHARD) || stack.getType().equals(Material.MAGMA_CREAM) || stack.getType().equals(Material.SLIME_BALL)),
                        (player, inventory) -> player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.HORSE_ARMOR, 10, 0),
                        (player, itemStack) -> {},
                        (player, itemStack) -> {}
                )
        );

        this.sendMessage(
                "Successfully registered " + ChatColor.GREEN +
                        this.bagManager.getBags().size() + ChatColor.WHITE
                        + " bags [" +
                        Util.getTimeDifferenceAndColor(
                                start,
                                System.currentTimeMillis()
                        )
                        + ChatColor.WHITE + "]");
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
                        new Location(getSkyblockWorld(), 20.5, 71, -40.5),
                        (player) -> {
                            SkyblockPlayer.getPlayer(player).setExtraData("personalBankLastUsed", 0L);
                            this.getGuiHandler().show("banker", player);
                        },
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
                        new Location(getSkyblockWorld(), -19.5, 71, -124.5),
                        (player) -> this.getGuiHandler().show("reforge", player),
                        "",
                        ""
                )
        );

        this.npcHandler.registerNPC("auction_master", new NPC(
                "Auction Master",
                true,
                true,
                false,
                null,
                new Location(getSkyblockWorld(), 17.5, 71, -78.5),
                (player -> player.performCommand("sb auction")),
                "ewogICJ0aW1lc3RhbXAiIDogMTU5NjExOTEwOTUyOCwKICAicHJvZmlsZUlkIiA6ICJlM2I0NDVjODQ3ZjU0OGZiOGM4ZmEzZjFmN2VmYmE4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5pRGlnZ2VyVGVzdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hMTgzYzNlOGJhOGI2ZTIwNmQ0YzFhMjQ4M2UzYjNhYmE3MTY2YjM5NjczODA4YmU5MmJlMzIwMzI3MGIyNDhjIgogICAgfQogIH0KfQ==",
                "e4r8fhl+jz4F1KxHQCggSnYAm5h+2BIDBumRdNY+qKdC82PeyFUrhSpn9rCwJ2QT1jYMQaZ61I8MUOsNOP+8Qj7xcD7CkhJM9NxwTK2ba948BpR1w3ZIPpj7OPLmAIMZl0Ux8OFXmiK355gYdKzRyr2+37fvRAj1KkO6ov2dG8y1uybVUyKgedUB5jWHjM3idKENIjCf5at9tktW+sjyAtT3TDqkoF78Fe7rDzhSaVYGW81YaNJ3C08FBDZST7vxlDhMG4ADwmQdnlhFdGcRCbh4FPeV8T35eEBXHtynZ40iJctiJAjCIGLCWZI4UeG/Hh+vtmdrZBofosNsR8RuujvQ5SsHZqd1rXOiZ2PC3j0EmrOiZKoyBd9yO3MG8EBwx57qco+LFAurUg5JBMZn4TI4oI+pYh2y8aN68X1JEO4M6kMMCDTgijztFK969f37cyx4xjFdrZA91CIIRUqyDKt4x9dxkQ72vdfGplkiHZyEeUIWlaFCQIky3zsUa63tvVRNQ455/rBNTQthUIsq1Aq5Qewtps32IBryUGvHliZ1E9OH8CBaXX26P67Dx+JJwHxpKpMRdeohlk4MojpjWD+wjH3cIp79lzfGozll7605EfUwkSRKnRYZnFZF7+8Q46CJcmBWz51eXOTrGesbpbFlRQMp6N4deeKuU7KIkrE="
        ));

        this.npcHandler.registerNPC("bazaar", new NPC(
                "Bazaar",
                true,
                true,
                false,
                null,
                new Location(getSkyblockWorld(), 32.0, 71.0, -79.0),
                (player -> player.performCommand("sb bazaar")),
                "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJpZCIgOiAiYmE0ODUzODFjNzI5NDhiY2E0NzY1NjJjNzRlZmE0NTkiLAogICAgICAidHlwZSIgOiAiU0tJTiIsCiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIzMmUzODIwODk3NDI5MTU3NjE5YjBlZTA5OWZlYzA2MjhmNjAyZmZmMTJiNjk1ZGU1NGFlZjExZDkyM2FkNyIsCiAgICAgICJwcm9maWxlSWQiIDogIjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwKICAgICAgInRleHR1cmVJZCIgOiAiYzIzMmUzODIwODk3NDI5MTU3NjE5YjBlZTA5OWZlYzA2MjhmNjAyZmZmMTJiNjk1ZGU1NGFlZjExZDkyM2FkNyIKICAgIH0KICB9LAogICJza2luIiA6IHsKICAgICJpZCIgOiAiYmE0ODUzODFjNzI5NDhiY2E0NzY1NjJjNzRlZmE0NTkiLAogICAgInR5cGUiIDogIlNLSU4iLAogICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMjMyZTM4MjA4OTc0MjkxNTc2MTliMGVlMDk5ZmVjMDYyOGY2MDJmZmYxMmI2OTVkZTU0YWVmMTFkOTIzYWQ3IiwKICAgICJwcm9maWxlSWQiIDogIjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwKICAgICJ0ZXh0dXJlSWQiIDogImMyMzJlMzgyMDg5NzQyOTE1NzYxOWIwZWUwOTlmZWMwNjI4ZjYwMmZmZjEyYjY5NWRlNTRhZWYxMWQ5MjNhZDciCiAgfSwKICAiY2FwZSIgOiBudWxsCn0=",
                "B+1Zq16nwoRYl4Ptbsc0vk6juz0fXnSj96JELezDpgrf1rIypSJ68uKaCKvX0XcVCH7eom/9CZTgpgUPoSH+QjG8I9017YKt3SVjKPq9KLxbM5kjKqJEx4Png2mPvkC+c5TF7Qw7FxjKA4AdSs/7XCeHXSZDgebTKhh6D4WH+XARBsLmNmG/EFo5zP7gUn+EkiGUaOjRPizYuRwOg1zNFkoPsrDGIovnW4itbu2BjdJ80yiTRn4bwGMm93NC/pCG7jbgsxO+YP1GkZnLUaAiIIQcKumC1nrlk3GTFAnHi+JFX562EKNUUMAtWrS/0v7QyeDYfDDtXLB8gNrzXHmaAKkvC1AU5/dk/YLZGq4lY6ukkZVOe+XLNbdXE4QNdA8ewPgPKUBwYEO6UW4jDOV+nN1WRana+rTREJUycFcxs2u6zinKV7lRtMbZ6CzpL+gvPlzhldvovvhX273Fdm7vpoPIRT6vVyO50dWH4qpM7n38Mgyxulot63NvLHzHG7Db8Z7ruZenVLE5cdjBAYHKHOjoOxzR81nPlGp7x3fdlF1usWSx5WRjpdw8Eyim7xrc2iNHRzyE1Lbywz7BTeT5ADHqsFkshrkG2bA11MnnZUyfjdPt4VNoUNX53KgWv14vmgtu8quu2xCW8JQ0J3HANoDgqPINKzCqn6oGdDz3Gy8="
        ));

        this.npcHandler.registerNPC(
                "tia_the_fairy",
                new NPC(ChatColor.LIGHT_PURPLE + "Tia the Fairy",
                        true,
                        true,
                        false,
                        null,
                        new Location(getSkyblockWorld(), 129.5, 66, 137.5),
                        (player) -> new TiaGUI(player).show(player),
                        "ewogICJ0aW1lc3RhbXAiIDogMTYxMzg0ODAyNjE2NywKICAicHJvZmlsZUlkIiA6ICJjMDI0Y2M0YTQwMzc0YWFjYTk2ZTE2Y2MwODM1ZDE4MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJHZW9yZ2VOb0ZvdW5kIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdlNTk3YjE3NTk1OTAyN2U2NDM5MzZjYTJiOGQ3YmZiMjFiMWRjNjgzYTRhNTdmYjBjMDk5YTdkNGE5Njk1MTAiCiAgICB9CiAgfQp9",
                        "TgEp0zMP+3e+782xvYsMcTYtkBfTq6XZpW1Z0mVb0BaDWjjVmQQer64ykJ8lthJj0Z+BjQhotwc8gIMuzxfBlaAPi7TDnODAm73wWRNs4n/qXj4a+++gkk4NeS1KswLzZDgQ0Nkp1kyM3lOja87zgcUCkpFXrzJbcsUX2N+rABmQIT8swmDRFmwoGvK4r0Cjf8nmLDj3+1fX4Kk+0o1ynLDDhI8c1nq4cqPRaRoTqNy0xYUeX22UaSo5tzlxAKrnabQi/I+P1Z33AqjvO6AdclXAfPIBsD7himNluqvKJjyWwpN0tb48703JMCixhs2Wq1j0cmEjVAqZKSLc+3jNkCp46V6NRIvcJ8xi/dijBR5SPgU8Kb7YUaVT6FUFJsAAVpNOBlJmnI+0L9Esqp9SMhMy8SNO/vo8Gk1zF2BENzuKBD6w5zQlWNIQt4E7MRG1fnh0VZMiS8s+dz9NuCC5oGMFIBNz67J2z6VQR+BhXGCSwDgw9gsKDxYSxpzASa6iFUv1gQpi8x+eQMn4VM16d0mwVDNnd6h1HdCmxextKzkf9mkwBaycz9AOcun8GWOqvhZDv2nyzmUAzFBU0mO1Ys6nYSHQEwXBXqURho5L0Fvu3Wb15YqwATsO//Mg+6L+f/kb5l2B1/Z1I/wzxzOeDYtME2TsTMAaK00ob/6e0Hg="));

        this.npcHandler.registerNPC(
                "maddox_the_slayer",
                new NPC(ChatColor.DARK_PURPLE + "Maddox the Slayer",
                        true,
                        true,
                        false,
                        null,
                        new Location(getSkyblockWorld(), -87.0, 66.0, -70.0),
                        (player) -> player.performCommand("sb batphone"),
                        "ewogICJ0aW1lc3RhbXAiIDogMTU5NjQ5MTIwODg4NCwKICAicHJvZmlsZUlkIiA6ICJkNjBmMzQ3MzZhMTI0N2EyOWI4MmNjNzE1YjAwNDhkYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCSl9EYW5pZWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMzNmQ3Y2M5NWNiZjY2ODlmNWU4Yzk1NDI5NGVjOGQxZWZjNDk0YTQwMzEzMjViYjQyN2JjODFkNTZhNDg0ZCIKICAgIH0KICB9Cn0=",
                        "NeIGEVhTQsg+GfcmtVhCCXdWX6tQpI/iUjPixUKaxea5q8xTpCKFSGqnIhSgG0CjPpxw9UKwC1yr4gIDsM5zPGjnIsD3PDP4F6Jaicx0YsiJGr861zxQDSlxpkcbGXrHRuNq92TT4/zojNMk6qPGtGeFApro7dXxU5Fq7HpyikHR2S4iaTZAF2L65rXqdogmQIBcTI5UVO2cZ3xNSr3j9y/nKGUx0SwVaIryt1sMHj2cO5Lknb9eiiG+vfw/LTlgwOmc9PXHhQB045SoBgGondcBZYBWVGCP9dTCNrvDBp963rzEkJMOfLfL+M2P+BT318BCBQzQ6JGJuILqhdY/Ph7qZJW2P9g8At9chbfnBdwMnHvjTshGN3XMzVg8BdxFAKydJMSocfF4j9KvPCtP1Hilk0pylqRAPe1cn0JpTZ1e/xzorzgqHdo0kXmf8gzLXHXDz8fYanZpQQCemwL3aOHy6nvAFFk/+j6kGLEaetTZgw8WAMJiyAxcpN/elfG9fxoX+pXMFtM9ItRA2Sf6EHdRKJTc4gB+yclkuCd3MgCiRDZU5NwpH8AhTmFZsjd0nHzHLXvpNPmSLAZiYi7EqG9SySEu7pJ4PXHZ0F80jKknNqh0CnnnqH4iKdMIUau33ENPKTLiuxwqxj9bv6ZtsCUZXn/mHWeCOiB6IBPjaR0="));

        this.npcHandler.registerNPC("guy",
                new NPC("Guy",
                        true,
                        true,
                        false,
                        null,
                        new Location(getSkyblockWorld(), 51.5, 79, -13.5),
                        (p) -> {
                            SkyblockPlayer player = SkyblockPlayer.getPlayer(p);

                            if (player.getIntValue("bank.personal.cooldown") == -1) {
                                Util.sendDelayedMessages(p, "Guy", (pl) -> new GuyGui(p).show(p),
                                        "Heard of the " + ChatColor.GREEN + "Personal Bank" + ChatColor.WHITE + "?",
                                        "It will let you access your bank remotely.",
                                        "Unlock it through " + ChatColor.GREEN + "Emerald" + ChatColor.WHITE + " collection.");
                            } else {
                                new GuyGui(p).show(p);
                            }
                        },
                        "ewogICJ0aW1lc3RhbXAiIDogMTU5OTQ1NzU3MjI1NCwKICAicHJvZmlsZUlkIiA6ICI2MTI4MTA4MjU5M2Q0OGQ2OWIzMmI3YjlkMzIxMGUxMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJuaWNyb25pYzcyMTk2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2U5ZTIzYmU3ZjA0NTU2ZmEzMzM1MWE0Yzc3MWEzZjA1ZjRhNmQyN2RlNDEzYTM2ZDAyMzBjNjFmNzE2OTg3OTkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                        "lu9SuKpv/U8XqaZTkleKzPDg8S1pcqA7LSWiWimR9x0BnkpK5CkyLwkWA1AMKCibQZSMPjoFFySNMVRcIhylv3yN0V6/Y6moJi1/SmRIeJJL/FovCUykzTSvbWsqJXfRyoi+5mUt6REj6bvJQruNtCedIHQD5a0Mrw3d8LbvZ0OlGPUbaAv1O7dW1O2uxmxCDSWMOL8PN+6fb/zYgA/XeJvSj97LafK4YAeb1YV362CeMkhmMP0uE5wj11+BnexEN+WaBzbRIUlBuSMB+Pw+7RoS4Nk7kxxKSNAR/pzlSqFHLkTlL88ljrLeyEooccpETSuqLh55/wsWSdesEDpSNjmfRYVX9EXOk783VRz3Btb+MItjiqmos5Mgmjelnx34utIPkAFbLyn/AUvWaNImxhWw/iDFYod+C/QNbUqR/H9ahIHzZXun4+6tKhVBgaCfLqaqF+V9Js8miapUpW16EEnElTNJ843+/HFgqex18q2vCTUX0tixtzHrFmwhhbBnT02DSvbvIxm9ucyNMwTpYhJ33I433pB67i1iQxiNBxaTTVSn2bGs4AKLgOjkTg3TsixEix02fCOzFl8bau/JlZMDmk7/2SAI74VRnreBVTEHjIAb7SRRXNy+zOxQJLzyMB+TwpGBBIUbNpCgjKu0aqu+Ld/FOO37dvBke8bv7Uw="));

        this.npcHandler.registerNPC("taylor",
                new NPC("Taylor",
                        true,
                        true,
                        false,
                        null,
                        new Location(Skyblock.getSkyblockWorld(), -28, 71, -107),
                        (p) -> Util.sendDelayedMessages(p, "Taylor", "Hello!", "You look dashing today!"),
                        "ewogICJ0aW1lc3RhbXAiIDogMTU5MDg4MzYxNjg5NCwKICAicHJvZmlsZUlkIiA6ICI3ZGEyYWIzYTkzY2E0OGVlODMwNDhhZmMzYjgwZTY4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJHb2xkYXBmZWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMThjYTBjODliZWY4YzRlN2U1MjFiMTEzODIxZmZkZjAwZTdiNWViNTUyYTM3OGY5MDM3Y2JlYzY1NWRkMjdjOCIKICAgIH0KICB9Cn0=",
                        "r6GGjK7Rb0X3mhJQFyMYn+5OU0JvzBYnxti3hGQfomrDMrI5NdWe/+huGw22UqSFt7pMoZH+04J0z0gFFFFmdY6p0gbcls/9W1qIxgjSFYbDM5y/v1xu3+o9sX5P4WY9WVLOV/RYilfTNIRHCu6+rb/X+x+4ftttaf+zyVvF/bMQq+EwgcRo0UNkXIOrzvH0CKNbEIF9D/9mHk2kBgElq6ucs5ohVSVkU9R4XWTbzs2v4LYmSumRSzKd4eB6L66R3CbCqt7nk8DLnsEmexcxR8u9k6BR1P2Itf2yGZPf1hdconNZKqKdv/sKgtPZo5wcIk5h5cGfezN1b2wHXbKBXV/EN2ZTvC5seNJlXDHy0vGhlThwqHPvd24E+aSHnLu+Gfujw72NR10VsL5cnxmLjmGQQh12ohus5ZO45nE8+UZEBq4gr1wdgMfTRF8XYYRXclKuFcPwKMFMjsGtvV4jVxEunuZitygHOzcjUF7dKYbBv8yFtP0UxWA9r2Phcf1JwqHWf0lSE9l2Qi+AKoz3HCBhKdxPvuIPET/VKCB4xQkrjm8pCQYyD9FOLz0/t6yLf4KOHEhSfcWM2ObzACAxH3Euz8KCFdYUQShVejAvahA2sJM/twqZdvDgVed4Apmhx/LAJjhGRTzIvg0D7HzZVyfArEEgQMi8ZDttE/5Lyk4="
                ));

        this.npcHandler.registerNPC("melody",
                new NPC("Melody",
                        true,
                        true,
                        false,
                        null,
                        new Location(Skyblock.getSkyblockWorld(), -398, 110, 35),
                        (p) -> {
                            Util.sendDelayedMessages(p, "Melody", "Hello!", "Want to learn the harp?", "I'll teach you! ❤", "Pick a song to get started!");
                            p.performCommand("sb harp");
                        },
                        "eyJ0aW1lc3RhbXAiOjE1ODcwMTU4MTQ5NTIsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJNaW5lU2tpbl9vcmciLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzRkMjM4Y2NjODE4YWIwYzBmZGViZDA3ZDRhNTZjMzUzZmY1ZWI1NDFjODVkMzg2MzYyMTYxNzgxZDFkZDc0Y2MifX19",
                        "wUgPl5j42AXlnQnT2z1TCVg7rBcxi3dxcB5tJt6Hb+N9V3MlgUH1mtcSE88wClQxb1i9qlqqIk+R2UPaNf74xwxRqLELufuZ/JOCstsKrjQPY3t8nm3Xx6b101h8sm+5MpemFajF5j4ZC73mMk99HsEdGwWNGkV+oFl5+0lmyxO5mFcxPO15jrj2ZsjUH4rQQJRdaMNC6B1EBBsPQaVVG0nQV5/Mk2r4R1iTTXOSSn9ygtgkjlCMlLyrrE+1SdoD5015iDouXsqxpTXkngrtZLTEyTmo4GvqR5M+iMgEAj8aD4np0AEU4WgZ7dAMwCiYFFSXXigQxjwVxzQUX/6ZrYTdTNYnHSoCf6p2hc2vkabMS8aopJ4w5/e7Z4KM1rv3yn+umn5Hb42a8rcbOL7QWxuE5naFS9TZHn0WPl9CtLi2Yqv1YKy6hiVhv47r2++jOLYRoWCcA47aBi6vP/XRl20SUUSoK3+mnblnqF3wCUR0Z7xi2NLXDVzXz49CitDKhCN3Ve6nbkVgab1SXBkyl0qkY50k8v0nXtOd9wWmFSOSHiGxFB/pctwf+vx16BiydPRIluOhlFklBw6lMgzsG4NFhPfne4eV4YYt0HmkC86MsPpvzRgbnGWwBgPl7G9f3owOU4ilvMd9ea4+41EcCsB3sE23qfzR+7+17G7jRlI="
                ));

        this.npcHandler.registerNPC("seymour",
                new NPC("Seymour",
                        true,
                        true,
                        false,
                        null,
                        new Location(Skyblock.getSkyblockWorld(), -30.5, 66, -108.5),
                        (p) -> {
                            Util.sendDelayedMessage(p, "Seymour", "Looking to buy something fancy?", 0);
                            new SeymourGui(p).show(p);
                        },
                        "ewogICJ0aW1lc3RhbXAiIDogMTU5OTQ2MDQ5OTc1NCwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzhhYWM0ZTYzMTM5OGMwZjQ3ZmU4NTYxNWU3ZjQ5MjE1NTAwMDQwYmI5MDgxOWQ1NjhlODI0YWNmOTRiZmE0ZSIKICAgIH0KICB9Cn0=",
                        "hGGbYl4OuItrGiGXYYh6mCy1a5xknDipoYay1DFARt03+YJ6ebFagIMH7JgEY3CFed8WIxFcNivfpZ3q47e2KhNugR+6/X4KBrHtyz9fWVte2HGW7p2ShiYipcUL+8wBjvJ2ssEsWTUeGgqnBgo/sA3UdsWhB6E9iP34x4nm5lPfnKT2Jl9QyhsqXSOQYmidDUY5z0kGhmy0IXRoh92vF/lrzmdpS4TamfogRLGV1BivxZ8C71ImVczEm/JHWTGCdjwFBTdoZzkUOJ9IE+tbBUlOWWFMvjW+TY4Y3pBM5lzY3TMTpvG+rHZ0042E2SNfp2RmHaEAqMNb9JI57qfXKZ8zJB1/8gU+pFjuuXRsWuV0tWKLIUGSH3nIho/BidPBoe6YUsWCe9ySSrFprocKU96Ct6z5l8bsoJ5xtiOGSn/5JdUexc4IUF9ICFh7Xeu8rvGufH7s1BIyLgUBQQSvVpj31VharFkV0IVnwG/4c/YBYaaUUH07CW0woj577fd5nCVEs8pfJ7KNrChtna0LzDZQuELzDwmQO5mdOxWEwGurPvPx1uFm3tCDVBRUrj+CCVCQqIflg9s3nVTRSPZhl3ZlNW+L8/wVdjuXtGTXGWT9ou+nfGRT8c0ENrsVE3dkWe4o2BaokIIdCJ1isO+GS6oMNP6I07EGgxUZFe2kk8A="
                ));

        /*
        Admin houses
         */

        this.npcHandler.registerNPC("sylent_",
                new NPC(
                        ChatColor.RED + "Sylent_",
                        true,
                        true,
                        false,
                        null,
                        new Location(Skyblock.getSkyblockWorld(), -67.5, 69, -93.5),
                        (p) -> Util.sendDelayedMessages(p, ChatColor.RED + "Sylent_", "You can hold as many talismans as you want in your inventory. They will always work."),
                        "ewogICJ0aW1lc3RhbXAiIDogMTY3MzAzNTIyNDg4MSwKICAicHJvZmlsZUlkIiA6ICI1MWU4OTE1ZGJlM2E0ZDU4OTQ2OTNhMTZmM2M2MGM3MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTeWxlbnRfIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIzNGU2ZmVjZTVhNTY0MzNiYWFlYmE3ODc4NjYyYjIzNjJiNGZmYjdkNTIzOWVhZDgzZmZjOTUyMjFmOGViYzUiCiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIzNDBjMGUwM2RkMjRhMTFiMTVhOGIzM2MyYTdlOWUzMmFiYjIwNTFiMjQ4MWQwYmE3ZGVmZDYzNWNhN2E5MzMiCiAgICB9CiAgfQp9",
                        "NniVgyJmfWI5OK/7t9q4hWalHgVMO/VR5HqTod6DY+00qkCZgPkEGBxWbAdxvfE4yrVK/TaPRsMS7cJcSP3k74WYEPIPnXthNfNbzsGPz17Ns3fsc/w4GaLp688zo0OG8TRKRJA2ECcoD1B0kaAuRLKsO7NyBBj8XLL+pHv5QwNeMXg53x2OxsoTf25/BdcJJ9bNVv84V+bCAs1gspnbYBWaODcKj3qUUkUPXrm7nHeafi7snXCIRN1L088Qv3Lu7HTor3sojn9rH1FaFCLGIuVimvW+HWGRdkq3soXJrvFPyIZzstKQtXGUjOSG6V1cNPYXCVO/A6VtW1VmU2Lio9/Q7xvjLF9U3fcD4rXHZz6YUy5iZz6YbznjMAdRzYiFgg7lpRd8Au4Gcykf6873IX0YXucwcB4GiPUkRVnoCLI8j2UbdbHy8MO0ydvP1u1QamhWbRjorSJfUBbk6o2NYeYIfuZ+Gcki3xgWtrc1R0Q6FXN+K5Cir2GvXlQXjKrz2W0tAb8kkl3/4ITRZwoJN22i0CqoVq/FawFJNsW7lG/W7t29uxsaySj34gyDYsQag19DWAwUaazq207JNGVMcJCji/55qT4OxItFrjg2EvfpdJ+DbWLY2BlOABXhGKEjuYCFsABZcO9tI7gwW/+rkLIlGOqHfprtKGC/sHG63PE="
                ));

        final int[] liftOperators = {0};

        Consumer<Integer> spawnLiftOperator = (y) -> {
            this.npcHandler.registerNPC(
                    "lift_operator_" + liftOperators[0],
                    new NPC(ChatColor.AQUA + "Lift Operator",
                            true,
                            true,
                            false,
                            null,
                            new Location(Bukkit.getWorld("deep_caverns"), 45.5, y, (y == 101 ? 17.5 : 15.5)),
                            (player) -> new LiftOperatorGui(player).show(player),
                            "ewogICJ0aW1lc3RhbXAiIDogMTU5Mzk5ODcxNjIyOSwKICAicHJvZmlsZUlkIiA6ICI2OTBkMDM2OGM2NTE0OGM5ODZjMzEwN2FjMmRjNjFlYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ5emZyXzciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRhODdjODAwNWJlM2QzMWZkNDMyM2NiMWE0NGI5ZjY5ZmQxOTgyYWY1YjljODdhMzU5NTUzNjY2ZWFjODUyYSIKICAgIH0KICB9Cn0=",
                            "wbcgvF/NVdgPE80LE/KTpGVnHkvZ8L8lmdoflEjK3yfiOL4gXzdoP5hYoCG1nBh6wppNi4a7rNZf0RV0fnUgy18NQdrqVGh6crIpQ+ya6zSeegfD7jVyAGPRCcJmtu14jUIhZeWd22AOOq9H1QR/bF9xtRO4zkDrAA11qW9c49f6n6wYQpOcFDjSdBcg4hUFyEh7XlmbdhwphM6waJnA6FlPXm5gLfLr7n5Ug5xznoqviF/d7OlEDbgg37csjtDnkY99QnRdxjCET1Bvl2g1ZTQmOVplDmhuTrV8hndvJT4Gn1kcx531u17fyLsg6B7SLn8ojxLzTVJM4d47JqXkWfBz1bXby6owPgKM95dGf+IAAUopYJ3KLL76huakeSYN1koG6t17veFwVkFhJUqtSZKsSLyAGSyINUa0zMjz9VLkYQkqWw2RhXQLKLQs7qKzXcGdrtT52QZcuA9zRFskkPYukPEswektVlxwJuuMicRHk5BZlIicOgMjaHqR6HwAChzJPDQkPKIbXjrWEVtQCIPNbGkbIlz2+/owNc8vvuzwHrtjlh1gBw5cfM9bz83Kmr6KLAnDknQ0SeRAe5mF6+Vf4y07YI8yARCeTAcPd5SPmpX0nW7VRcCzOH3deGNgS3pzcusKsISfF+qAo50qRMEOCcosz6juMiborf8fu60=")
            );

            liftOperators[0]++;
        };

        spawnLiftOperator.accept(150);
        spawnLiftOperator.accept(121);
        spawnLiftOperator.accept(101);
        spawnLiftOperator.accept(66);
        spawnLiftOperator.accept(38);
        spawnLiftOperator.accept(13);

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
        this.sendMessage("Initializing recipes...");
        long start = System.currentTimeMillis();

        this.recipeHandler = new RecipeHandler();

        this.recipeHandler.init();

        this.sendMessage("Successfully initialized " + ChatColor.GREEN + this.recipeHandler.getRecipes().size() + ChatColor.WHITE + " recipes [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerLocations() {
        this.sendMessage("Registering locations...");
        long start = System.currentTimeMillis();

        this.locationManager = new SkyblockLocationManager(this);

        this.sendMessage("Successfully registered &a" + this.locationManager.getLocations().size() + " &flocations [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeAlreadyOnlinePlayers() {
        this.sendMessage("Reloading already online players...");

        for (ArmorStand stand : getSkyblockWorld().getEntitiesByClass(ArmorStand.class)) {
            NBTEntity entity = new NBTEntity(stand);

            if (entity.getBoolean("isPet")) stand.remove();
        }

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

        this.saveDefaultConfig();

        this.serverData = new ServerData(this);

        this.random = new Random();

        this.sendMessage("Sucessfully initialized server data [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeSpongeReplacers() {
        this.sendMessage("Initializing sponge replacers...");
        long start = System.currentTimeMillis();

        this.spongeReplacerHandler = new SpongeReplacerHandler();

        this.spongeReplacerHandler.registerReplacer(new SpongeReplacer("Gold Mine", new SpongeBlock(Material.STONE, 10), new SpongeBlock(Material.IRON_ORE, 3), new SpongeBlock(Material.GOLD_ORE, 2)));
        this.spongeReplacerHandler.registerReplacer(new SpongeReplacer("The End", new SpongeBlock(Material.OBSIDIAN, 1)));

        this.spongeReplacerHandler.startGeneration();

        this.sendMessage("Successfully initialized sponge replacers [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
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
        this.guiHandler.registerGuiCommand("skills", "sb skills");

        this.sendMessage("Successfully registered guis [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerEnchantments() {
        this.sendMessage("Registering enchantments...");

        long start = System.currentTimeMillis();

        this.enchantmentHandler = new SkyblockEnchantmentHandler(this);

        this.enchantmentHandler.registerEnchantment(new EnderSlayerEnchantment());
        this.enchantmentHandler.registerEnchantment(new GrowthEnchantment());
        this.enchantmentHandler.registerEnchantment(new ProtectionEnchantment());
        this.enchantmentHandler.registerEnchantment(new SharpnessEnchantment());
        this.enchantmentHandler.registerEnchantment(new CleaveEnchantment());
        this.enchantmentHandler.registerEnchantment(new FirstStrikeEnchantment());
        this.enchantmentHandler.registerEnchantment(new LethalityEnchantment());
        this.enchantmentHandler.registerEnchantment(new ThunderlordEnchantment());
        this.enchantmentHandler.registerEnchantment(new VenomousEnchantment());
        this.enchantmentHandler.registerEnchantment(new GiantKillerEnchantment());
        this.enchantmentHandler.registerEnchantment(new FireAspectEnchantment());
        this.enchantmentHandler.registerEnchantment(new TelekinesisEnchantment());
        this.enchantmentHandler.registerEnchantment(new CriticalEnchantment());
        this.enchantmentHandler.registerEnchantment(new BaneOfArthropodsEnchantment());
        this.enchantmentHandler.registerEnchantment(new LifeStealEnchantment());
        this.enchantmentHandler.registerEnchantment(new CubismEnchantment());
        this.enchantmentHandler.registerEnchantment(new DragonHunterEnchantment());
        this.enchantmentHandler.registerEnchantment(new FireProtectionEnchantment());
        this.enchantmentHandler.registerEnchantment(new SmiteEnchantment());
        this.enchantmentHandler.registerEnchantment(new EfficiencyEnchantment());
        this.enchantmentHandler.registerEnchantment(new AimingEnchantment());

        this.sendMessage("Successfully registered &a" + this.enchantmentHandler.getEnchantments().size() + " &fenchantments [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerMobs() {
        this.sendMessage("Registering mobs...");
        long start = System.currentTimeMillis();

        this.entityHandler = new SkyblockEntityHandler();
        this.entitySpawnerHandler = new EntitySpawnerHandler();

        this.sendMessage("Successfully registered mobs [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void registerCollections() {
        this.sendMessage("Registering collections...");
        long start = System.currentTimeMillis();

        if (!Collection.INITIALIZED) {
            try {
                Collection.initializeCollections(this);
            } catch (Collection.CollectionInitializationException ex) {
                this.sendMessage("&cFailed to initialize collections: " + ex.getMessage());
                this.getServer().getPluginManager().disablePlugin(this);
            }
        }

        this.sendMessage("Successfully registered " + ChatColor.GREEN + Collection.getCollections().size() + ChatColor.WHITE + " collections [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

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
        registerListener(new PetListener());
        registerListener(new MinionListener());
        registerListener(new PotionListener());
        registerListener(new EntityListener());
        registerListener(new Util.UL());
        registerListener(new DragonSequence());

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
                new SkyblockMenuCommand(),
                new PlayerDataCommand(),
                new EnchantCommand(),
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
                new BatphoneCommand(),
                new WithdrawCommand(),
                new ReloadCommand(),
                new ItemNBTCommand(),
                new AuctionCommand(),
                new CreateSpawnerCommand(),
                new SkillsCommand(),
                new CreateCrystalCommand(),
                new BazaarCommand(),
                new RegenerateCommand(),
                new EffectCommand(),
                new CreatePotionCommand(),
                new SettingsCommand(),
                new HubCommand(),
                new IslandCommand(),
                new EffectsCommand(),
                new MaxMyItemCommand(),
                new CoinsCommand(),
                new GameModeCommand(),
                new SkillXpCommand(),
                new FairySoulCommand(),
                new HarpCommand(),
                new LoopCommand(),
                new WipeCommand(),
                new HealCommand(),
                new SlayerbossCommand(),
                new SlayerbossCommand(),
                new LaunchCommand()
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
            world.setGameRuleValue("randomTickSpeed", "0");
            world.setGameRuleValue("announceAdvancements", "false");
        }

        this.sendMessage("Successfully initialized game rules [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void initializeNEUItems() {
        this.sendMessage("Initializing items...");
        long start = System.currentTimeMillis();
        this.itemHandler = new ItemHandler(this);
        this.itemHandler.init();

        this.skyblockItemHandler = new SkyblockItemHandler(this);
        this.minionItemHandler = new MinionItemHandler(this);

        this.sendMessage("Successfully initialized items [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public static World getSkyblockWorld() {
        return Bukkit.getWorld(Skyblock.getPlugin().getConfig().getString("general.skyblock_world"));
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

    public static Skyblock getPlugin() {
        return Skyblock.getPlugin(Skyblock.class);
    }

    public void addRemoveable(Entity entity) {
        this.removeables.add(entity);

    }
}
