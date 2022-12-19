package com.skyblock.skyblock;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.auction.AuctionCategory;
import com.skyblock.skyblock.features.auction.AuctionSettings;
import com.skyblock.skyblock.features.auction.gui.AuctionCreationGUI;
import com.skyblock.skyblock.features.bags.Bag;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.items.ArmorSet;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.features.merchants.Merchant;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.features.scoreboard.HubScoreboard;
import com.skyblock.skyblock.features.scoreboard.Scoreboard;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.features.slayer.SlayerHandler;
import com.skyblock.skyblock.features.slayer.SlayerQuest;
import com.skyblock.skyblock.features.slayer.SlayerType;
import com.skyblock.skyblock.utilities.BossBar;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTEntity;
import lombok.Data;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Data
public class SkyblockPlayer {

    private HashMap<String, Object> dataCache;

    private List<BiFunction<SkyblockPlayer, Entity, Integer>> predicateDamageModifiers;
    private AuctionCreationGUI.AuctionProgress progress;
    private HashMap<SkyblockStat, Double> stats;
    private HashMap<String, Boolean> cooldowns;
    private HashMap<String, Object> extraData;
    private AuctionSettings auctionSettings;
    private FileConfiguration config;
    private ArmorStand petDisplay;
    private BossBar bossBar;
    private Player bukkitPlayer;
    private int damageModifier;
    private ArmorSet armorSet;
    private Block brokenBlock;
    private Scoreboard board;
    private String actionBar;
    private File configFile;
    private ItemStack hand;
    private Pet pet;
    private int tick;

    private static int EVERY_SECOND = 20;
    private static int EVERY_THREE_SECONDS = 60;
    public static HashMap<UUID, SkyblockPlayer> playerRegistry = new HashMap<>();

    public static SkyblockPlayer getPlayer(Player player) {
        return playerRegistry.get(player.getUniqueId());
    }

    public static SkyblockPlayer getPlayer(UUID uuid) {
        return playerRegistry.get(uuid);
    }

    public static void registerPlayer(UUID uuid, PlayerJoinEvent event, Consumer<SkyblockPlayer> after) {
        playerRegistry.put(uuid, new SkyblockPlayer(uuid));

        SkyblockPlayer player = playerRegistry.get(uuid);

        after.accept(player);
    }

    public SkyblockPlayer(UUID uuid) {
        this.dataCache = new HashMap<>();
        this.predicateDamageModifiers = new ArrayList<>();
        this.bukkitPlayer = Bukkit.getPlayer(uuid);
        this.hand = Util.getEmptyItemBase();
        this.cooldowns = new HashMap<>();
        this.extraData = new HashMap<>();
        this.stats = new HashMap<>();
        this.petDisplay = null;
        this.damageModifier = 0;
        this.progress = null;
        this.armorSet = null;
        this.bossBar = null;
        this.pet = null;
        this.tick = 0;

        this.extraData.put("fullSetBonus", false);
        this.extraData.put("fullSetBonusType", null);

        this.extraData.put("isEndStoneSwordActive", false);
        this.extraData.put("endStoneSwordDamage", 0);

        this.extraData.put("last_location", null);

        initConfig();
    }

    public void tick() {
        if (tick == 0) {
            loadStats();
            resetActionBar();
            board = new HubScoreboard(getBukkitPlayer());

            if (getValue("pets.equip") != null) {
                pet = Pet.getPet((ItemStack) getValue("pets.equip"));

                if (pet != null) pet.setActive(true);
            }

            if (getValue("auction.auctionSettings") == null) {
                auctionSettings = new AuctionSettings(AuctionCategory.WEAPON, AuctionSettings.AuctionSort.HIGHEST, null, AuctionSettings.BinFilter.ALL, false);
            } else {
                Map<String, Object> map = new HashMap<>();

                for (String s : config.getConfigurationSection("auction.auctionSettings").getKeys(false)) {
                    map.put(s, config.getString("auction.auctionSettings." + s));
                }

                auctionSettings = AuctionSettings.deserialize(map);
            }

            auctionSettings.setPlayer(this);

            this.bossBar = new BossBar(bukkitPlayer);
        }

        if (tick % EVERY_SECOND == 0) {
            board.updateScoreboard();

            ActionBarAPI.sendActionBar(getBukkitPlayer(), actionBar);

            if (getStatNoMult(SkyblockStat.MANA) < getStatNoMult(SkyblockStat.MAX_MANA) - ((getStatNoMult(SkyblockStat.MAX_MANA) + 100)/50)) {
                setStat(SkyblockStat.MANA, getStatNoMult(SkyblockStat.MANA) + ((getStatNoMult(SkyblockStat.MAX_MANA) + 100F)/50));
            } else if (getStatNoMult(SkyblockStat.MANA) < getStatNoMult(SkyblockStat.MAX_MANA)) {
                setStat(SkyblockStat.MANA, getStatNoMult(SkyblockStat.MAX_MANA));
            }

            if (getQuestLine() != null) {
                Objective objective = getQuestLine().getObjective(this);

                if (objective != null) {
                    bossBar.setMessage(ChatColor.WHITE + "Objective: " + ChatColor.YELLOW + objective.getDisplay() + " " + objective.getSuffix(this));
                    bossBar.update();
                } else {
                    bossBar.reset();
                }
            } else {
                bossBar.reset();
            }
        }

        if (tick % EVERY_THREE_SECONDS == 0) {
            if (getStatNoMult(SkyblockStat.HEALTH) < getStatNoMult(SkyblockStat.MAX_HEALTH) - (int) (1.5 + getStatNoMult(SkyblockStat.MAX_HEALTH) / 100)) {
                updateHealth((int) (1.5 + getStatNoMult(SkyblockStat.MAX_HEALTH)/100));
            }else{
                setStat(SkyblockStat.HEALTH, getStatNoMult(SkyblockStat.MAX_HEALTH));
                getBukkitPlayer().setHealth(getBukkitPlayer().getMaxHealth());
            }
        }

        if (!hand.equals(bukkitPlayer.getItemInHand())) {
            ItemStack itemStack = bukkitPlayer.getItemInHand();

            if (itemStack.getType().equals(Material.AIR)){
                updateStats(Util.getEmptyItemBase(), hand);
            }else{
                updateStats(itemStack, hand);
            }

            hand = itemStack;
        }

        if (pet != null) {
            if (petDisplay == null) {
                Location loc = bukkitPlayer.getLocation();
                Vector dir = loc.getDirection();
                dir.normalize();
                dir.multiply(-2);
                loc.add(dir);

                petDisplay = bukkitPlayer.getWorld().spawn(loc,  ArmorStand.class);

                petDisplay.setHelmet(Util.IDtoSkull(new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()), pet.getSkull()));
                petDisplay.setVisible(false);
                petDisplay.setGravity(false);
                petDisplay.setSmall(true);

                int level = Pet.getLevel(pet.getXp(), pet.getRarity());

                petDisplay.setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + level + ChatColor.DARK_GRAY + "] " +
                        pet.getRarity().getColor() + bukkitPlayer.getName() + "'s " + pet.getName());

                petDisplay.setCustomNameVisible(true);

                NBTEntity nbtEntity = new NBTEntity(petDisplay);
                nbtEntity.setBoolean("isPet", true);

                Skyblock.getPlugin().addRemoveable(petDisplay);
            }

            Location loc = bukkitPlayer.getLocation();
            Vector dir = loc.getDirection();
            dir.normalize();
            dir.multiply(-2);
            dir.setZ(dir.getZ() + 1);
            loc.add(dir);

            petDisplay.setRemoveWhenFarAway(false);
            petDisplay.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY() + 0.75, loc.getZ(), petDisplay.getLocation().getYaw(), petDisplay.getLocation().getPitch()));

            if ((boolean) getValue("pets.hidePets")) {
                petDisplay.teleport(new Location(loc.getWorld(), Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
            } else {
                petDisplay.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY() + 0.75, loc.getZ(), petDisplay.getLocation().getYaw(), petDisplay.getLocation().getPitch()));
            }
        }

        Object young = getExtraData("young_dragon_bonus");
        int speedCap = 400;
        if (young != null) {
           speedCap = (boolean) young ? 500 : 400;
        }

        if (getStat(SkyblockStat.SPEED) > speedCap) setStat(SkyblockStat.SPEED, speedCap);

        if (bukkitPlayer.getLocation().getY() <= -11) {
            kill(EntityDamageEvent.DamageCause.VOID, null);

            if (isOnIsland()) {
                bukkitPlayer.performCommand("warp home");
            }
        }

        bukkitPlayer.setWalkSpeed(Math.min((float) (getStat(SkyblockStat.SPEED) / 500.0), 1.0f));
        bukkitPlayer.setMaxHealth(Math.round(Math.min(40.0, 20.0 + ((getStat(SkyblockStat.MAX_HEALTH) - 100.0) / 25.0))));
        bukkitPlayer.setHealth(Math.min(Math.max(1, bukkitPlayer.getMaxHealth() * ((double) getStat(SkyblockStat.HEALTH) / (double) getStat(SkyblockStat.MAX_HEALTH))), bukkitPlayer.getMaxHealth()));

        tick++;
    }

    public void resetActionBar() {
        actionBar = ChatColor.RED + "" + getStat(SkyblockStat.HEALTH) + "/" + getStat(SkyblockStat.MAX_HEALTH) + "❤   " +
                (getStat(SkyblockStat.DEFENSE) > 0 ? ChatColor.GREEN + "" + getStat(SkyblockStat.DEFENSE) + "❈ Defense   " : "")
                +  ChatColor.AQUA + "" + getStat(SkyblockStat.MANA) + "/" + getStat(SkyblockStat.MAX_MANA) + "✎ Mana";
    }

    public void updateStats(ItemStack newItem, ItemStack oldItem) {
        if (Util.isNotSkyblockItem(newItem)) newItem = Util.getEmptyItemBase();
        if (Util.isNotSkyblockItem(oldItem)) oldItem = Util.getEmptyItemBase();

        try {
            if (Util.notNull(newItem) && Util.notNull(oldItem)) {
                ItemBase newBase = new ItemBase(newItem);
                ItemBase oldBase = new ItemBase(oldItem);

                changeStats(false, newBase);
                changeStats(true, oldBase);
            } else if (Util.notNull(newItem)) {
                ItemBase newBase = new ItemBase(newItem);

                changeStats(false, newBase);
            } else if (Util.notNull(oldItem)) {
                ItemBase oldBase = new ItemBase(oldItem);

                changeStats(true, oldBase);
            }
        } catch (IllegalArgumentException ignored) {}
    }

    public void changeStats(boolean subtract, ItemBase base) {
        int mult = subtract ? -1 : 1;

        addStat(SkyblockStat.HEALTH, mult * base.getHealth());
        addStat(SkyblockStat.MAX_HEALTH, mult * base.getHealth());
        addStat(SkyblockStat.CRIT_CHANCE, mult * base.getCritChance());
        addStat(SkyblockStat.CRIT_DAMAGE, mult * base.getCritDamage());
        addStat(SkyblockStat.ATTACK_SPEED, mult * base.getAttackSpeed());
        addStat(SkyblockStat.DAMAGE, mult * base.getDamage());
        addStat(SkyblockStat.DEFENSE, mult * base.getDefense());
        addStat(SkyblockStat.MAX_MANA, mult * base.getIntelligence());
        addStat(SkyblockStat.SPEED, mult * base.getSpeed());
        addStat(SkyblockStat.STRENGTH, mult * base.getStrength());
    }

    private void updateHealth(int i) {
        double hp = getStat(SkyblockStat.HEALTH);
        double mhp = getStat(SkyblockStat.MAX_HEALTH);

        if (bukkitPlayer.getHealth() <= bukkitPlayer.getMaxHealth()){
            bukkitPlayer.setHealth(Math.min(bukkitPlayer.getMaxHealth(), bukkitPlayer.getHealth() + i/(mhp/bukkitPlayer.getMaxHealth())));
        }
        setStat(SkyblockStat.HEALTH, hp + i);
    }

    public boolean checkMana(int mana) {
        boolean b = getStat(SkyblockStat.MANA) - mana < 0;
        if (b) bukkitPlayer.sendMessage(ChatColor.RED + "Not enough mana!");
        return !b;
    }

    public void subtractMana(int mana) {
        subtractStat(SkyblockStat.MANA, mana);
    }

    public void damage(double damage, EntityDamageEvent.DamageCause cause, Entity attacker) {
        damage(damage, cause, attacker, false);
    }
    public void damage(double damage, EntityDamageEvent.DamageCause cause, Entity attacker, boolean trueDamage) {
        double d = trueDamage ? damage : (damage - (damage * ((getStat(SkyblockStat.DEFENSE) / (getStat(SkyblockStat.DEFENSE) + 100F)))));

        if ((getStat(SkyblockStat.HEALTH) - d) <= 0) {
            kill(cause, attacker);
            return;
        }

        Util.setDamageIndicator(bukkitPlayer.getLocation(), ChatColor.GRAY + "" + Math.round(d), true);
        setStat(SkyblockStat.HEALTH, (int) (getStat(SkyblockStat.HEALTH) - d));
    }

    public void kill(EntityDamageEvent.DamageCause cause, Entity killer) {
        bukkitPlayer.setHealth(bukkitPlayer.getMaxHealth());
        setStat(SkyblockStat.HEALTH, getStat(SkyblockStat.MAX_HEALTH));

        String entityName = "";

        if (killer != null) {
            SkyblockEntity sentity = Skyblock.getPlugin(Skyblock.class).getEntityHandler().getEntity(killer);

            if (sentity != null) entityName = sentity.getEntityData().entityName;
        }

        String message;
        String out;
        switch (cause) {
            case VOID:
                message = "You fell into the void.";
                out = "%s fell into the void.";
                break;
            case FALL:
                message = "You fell to your death.";
                out = "%s fell to their death.";
                break;
            case ENTITY_ATTACK:
                message = "You were killed by " + entityName + ChatColor.GRAY + ".";
                out = "%s was killed by " + entityName + ChatColor.GRAY + ".";
                break;
            case FIRE:
            case LAVA:
                message = "You burned to death.";
                out = "%s burned to death.";
                break;
            default:
                message = "You died.";
                out = "%s died.";
                break;
        }

        for (Player player : bukkitPlayer.getWorld().getPlayers()) {
            if (player != bukkitPlayer) {
                player.sendMessage(ChatColor.RED + " ☠ " + ChatColor.GRAY + String.format(out, bukkitPlayer.getName()));
            }
        }

        bukkitPlayer.sendMessage(ChatColor.RED + " ☠ " + ChatColor.GRAY + message);

        if (hasActiveSlayer()) {
            SlayerQuest quest = getActiveSlayer().getQuest();
            quest.fail();
        }

        if (isOnIsland()) return;

        double sub = getDouble("stats.purse") / 2;
        bukkitPlayer.sendMessage(ChatColor.RED + "You died and lost " + Util.formatDouble(sub) + " coins!");

        bukkitPlayer.setVelocity(new Vector(0, 0, 0));
        bukkitPlayer.setFallDistance(0.0f);
        bukkitPlayer.performCommand("warp hub");
        bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ZOMBIE_METAL, 1f, 2f);

        setValue("stats.purse", sub);
    }

    public boolean isOnIsland() {
        return bukkitPlayer.getWorld().getName().startsWith(IslandManager.ISLAND_PREFIX);
    }
    public int getStat(SkyblockStat stat) { return (int) Math.round(getPreciseStat(stat)); }
    public double getPreciseStat(SkyblockStat stat) { return stats.get(stat) * getDouble("stats.multipliers." + stat.name().toLowerCase()); }

    public void setStat(SkyblockStat stat, double val) {
        setStat(stat, val, true);
    }
    public void setStat(SkyblockStat stat, double val, boolean event) {
        stats.put(stat, val);

        if (event){
            if (pet != null) pet.onStatChange(this, stat, val);
            if (armorSet != null) armorSet.onStatChange(bukkitPlayer, stat);
        }

        setValue("stats." + stat.name().toLowerCase(), val);

        resetActionBar();
    }
    public void addStatMultiplier(SkyblockStat stat, double mult) {
        setStatMultiplier(stat, getStatMultiplier(stat) + mult);
    }

    public void subtractStatMultiplier(SkyblockStat stat, double mult) {
        setStatMultiplier(stat, getStatMultiplier(stat) + (-1 * mult));
    }

    public void setStatMultiplier(SkyblockStat stat, double mult) {
        setValue("stats.multipliers." + stat.name().toLowerCase(), mult);
    }

    public double getStatMultiplier(SkyblockStat stat) {
        return getDouble("stats.multipliers." + stat.name().toLowerCase());
    }

    public Object getExtraData(String id) {
        return extraData.get(id);
    }

    public void setExtraData(String id, Object obj) {
        extraData.put(id, obj);
    }

    public boolean getCooldown(String id) {
        if (!cooldowns.containsKey(id)) {
            cooldowns.put(id, true);
        }

        return cooldowns.get(id);
    }


    public boolean isOnCooldown(String id) {
        return !getCooldown(id);
    }

    public void setCooldown(String id, int secondsDelay) {
        cooldowns.put(id, false);

        delay(() -> cooldowns.put(id, true), secondsDelay);
    }

    public void delay(Runnable runnable, int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(Skyblock.getPlugin(Skyblock.class), delay * 20L);
    }

    public boolean crit() {
        return new Random().nextInt(100) <= getStat(SkyblockStat.CRIT_CHANCE);
    }

    public double getStatNoMult(SkyblockStat stat) {
        return stats.get(stat);
    }

    public void addStat(SkyblockStat stat, double val) {
        setStat(stat, getStatNoMult(stat) + val);
    }

    public void subtractStat(SkyblockStat stat, double val) {
        setStat(stat, getStatNoMult(stat) - val);
    }

    public Object getValue(String path) {
        if (tick == 0) return config.get(path);
        if (!dataCache.containsKey(path)) dataCache.put(path, config.get(path));

        return dataCache.get(path);
    }

    public double getDouble(String path) {
        return Double.parseDouble(getValue(path).toString());
    }

    public void setValue(String path, Object item) {
        dataCache.put(path, item);
        forEachStat((s) -> stats.put(s, getDouble("stats." + s.name().toLowerCase())));
    }

    public void saveToDisk() {
        for (Map.Entry<String, Object> entry : dataCache.entrySet()) {
            try {
                config.set(entry.getKey(), entry.getValue());
                config.save(configFile);
                config = YamlConfiguration.loadConfiguration(configFile);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void forEachStat(Consumer<SkyblockStat> cons) {
        for (SkyblockStat stat : SkyblockStat.values()) {
            cons.accept(stat);
        }
    }

    private void loadStats() {
        forEachStat((s) -> {
            setStat(s, getDouble("stats." + s.name().toLowerCase()));
        });
    }

    private void initConfig() {
        File folder = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + "Players");
        if (!folder.exists())  folder.mkdirs();
        configFile = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + "Players" + File.separator + getBukkitPlayer().getUniqueId() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                forEachStat((s) -> {
                    config.set("stats." + s.name().toLowerCase(), 0);
                    config.set("stats.multipliers." + s.name().toLowerCase(), 1.0);
                });

                config.set("stats." + SkyblockStat.MAX_HEALTH.name().toLowerCase(), 100.0);
                config.set("stats." + SkyblockStat.HEALTH.name().toLowerCase(), 100.0);
                config.set("stats." + SkyblockStat.MAX_MANA.name().toLowerCase(), 100.0);
                config.set("stats." + SkyblockStat.MANA.name().toLowerCase(), 100.0);
                config.set("stats." + SkyblockStat.SPEED.name().toLowerCase(), 100.0);
                config.set("stats." + SkyblockStat.CRIT_CHANCE.name().toLowerCase(), 30.0);
                config.set("stats." + SkyblockStat.CRIT_DAMAGE.name().toLowerCase(), 50.0);

                config.set("stats.purse", 0.0);

                for (Collection collection : Collection.getCollections()) {
                    config.set("collection." + collection.getName().toLowerCase() + ".level", 0);
                    config.set("collection." + collection.getName().toLowerCase() + ".exp", 0);
                    config.set("collection." + collection.getName().toLowerCase() + ".unlocked", false);
                }

                config.set("bank.balance", 0.0);
                config.set("bank.interest", 2);
                config.set("bank.recent_transactions", new ArrayList<>());

                for (String skill : Skill.SKILLS) {
                    config.set("skill." + skill.toLowerCase() + ".exp", 0.0);
                }

                for (Merchant merchant : Skyblock.getPlugin(Skyblock.class).getMerchantHandler().getMerchants().values()) {
                    config.set("merchant." + merchant.getName().toLowerCase().replace(" ", "_") + ".interacted", false);
                    config.set("merchant." + merchant.getName().toLowerCase().replace(" ", "_") + ".interacting", false);
                }

                for (Bag bag : Skyblock.getPlugin(Skyblock.class).getBagManager().getBags().values()) {
                    config.set("bag." + bag.getId() + ".unlocked", false);
                    config.set("bag." + bag.getId() + ".slots", 0);

                    config.set("bag." + bag.getId() + ".items", new HashMap<Integer, ItemStack>());
                }

                for (SlayerType type : SlayerType.values()) {
                    config.set("slayer." + type.name().toLowerCase() + ".exp", 0);
                }

                config.set("pets.pets", new ArrayList<ItemStack>());
                config.set("pets.hidePets", false);
                config.set("pets.equip", null);

                config.set("island.last_login", System.currentTimeMillis());
                config.set("island.minion.slots", 1);
                config.set("island.minions", new ArrayList<>());

                config.set("fairySouls.found", new ArrayList<Location>());
                config.set("fairySouls.claimed", 0);

                config.set("auction.auctionSettings", new AuctionSettings(AuctionCategory.WEAPON, AuctionSettings.AuctionSort.HIGHEST, null, AuctionSettings.BinFilter.ALL, false).serialize());
                config.set("auction.auctioningItem", null);

                config.set("quests.completedQuests", new ArrayList<>());
                config.set("quests.completedObjectives", new ArrayList<>());
                config.set("quests.introduceYourself.talkedTo", new ArrayList<>());

                config.set("locations.found", new ArrayList<>());

                config.save(configFile);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public SkyblockLocation getCurrentLocation() {
        return Skyblock.getPlugin(Skyblock.class).getLocationManager().getLocation(bukkitPlayer.getLocation());
    }

    public String getCurrentLocationName() {
        SkyblockLocation currentLocation = getCurrentLocation();

        String loc = currentLocation == null ? "None" : currentLocation.getName();

        if (currentLocation == null) loc = "None";

        if (bukkitPlayer.getWorld().getName().startsWith(IslandManager.ISLAND_PREFIX)) loc = "Private Island";

        return loc;
    }

    public boolean isNotOnPrivateIsland() {
        return !bukkitPlayer.getWorld().getName().equals(IslandManager.getIsland(bukkitPlayer).getName());
    }

    public void addTransaction(double amount, String by) {
        List<String> transactions = (List<String>) getValue("bank.recent_transactions");

        if (transactions.size() >= 10) transactions.remove(0);

        transactions.add("" + amount + ";" + System.currentTimeMillis() + ";" + by);

        setValue("bank.recent_transactions", transactions);
    }

    public boolean deposit(double amount, boolean self) {
        double balance = getDouble("bank.balance");
        double purse = getDouble("stats.purse");

        if (purse < amount) return false;

        setValue("bank.balance", balance + amount);
        setValue("stats.purse", purse - amount);

        this.addTransaction(amount, self ? getBukkitPlayer().getDisplayName() : "Bank Interest");

        return true;
    }

    public boolean withdraw(double amount) {
        double balance = getDouble("bank.balance");
        double purse = getDouble("stats.purse");

        if (balance < amount) return false;

        setValue("bank.balance", balance - amount);
        setValue("stats.purse", purse + amount);

        this.addTransaction(-amount, getBukkitPlayer().getDisplayName());

        return true;
    }

    public boolean hasFullSetBonus() {
        return (boolean) this.getExtraData().get("fullSetBonus");
    }

    public String getFullSetBonusType() {
        return (String) this.getExtraData().get("fullSetBonusType");
    }

    public void addCoins(double coins) {
        setValue("stats.purse", getCoins() + coins);
    }

    public double getCoins() {
        return getDouble("stats.purse");
    }

    public boolean hasActiveSlayer() { return Skyblock.getPlugin().getSlayerHandler().getSlayer(bukkitPlayer) != null; }

    public SlayerHandler.SlayerData getActiveSlayer() {
        return Skyblock.getPlugin().getSlayerHandler().getSlayer(bukkitPlayer);
    }

    public void addPredicateDamageModifier(BiFunction<SkyblockPlayer, Entity, Integer> damageModifier) {
        this.predicateDamageModifiers.add(damageModifier);
    }

    public void removePredicateDamageModifier(int index) {
        this.predicateDamageModifiers.remove(index);
    }

    public boolean checkCoins(double coins) {
        boolean b = getCoins() >= coins;

        if (!b) getBukkitPlayer().sendMessage(ChatColor.RED + "Not enough coins!");

        return b;
    }

    public void subtractCoins(double coins) {
        addCoins(-1 * coins);
    }

    public ArrayList<ItemStack> getPets() {
        return (ArrayList<ItemStack>) getValue("pets.pets");
    }

    public void addPet(ItemStack pet) {
        ArrayList<ItemStack> pets = getPets();
        pets.add(pet);

        setValue("pets.pets", pets);
    }

    public void removePet(ItemStack pet) {
        ArrayList<ItemStack> pets = getPets();
        pets.remove(pet);

        setValue("pets.pets", pets);
    }

    public void setPet(Pet pet) {
        this.pet = pet;

        if (this.petDisplay == null) return;

        this.petDisplay.remove();
        this.petDisplay = null;
    }

    public void dropItem(ItemStack item, Location location) {
        dropItems(Arrays.asList(item), location);
    }

    public void dropItems(List<ItemStack> items) {
        dropItems(items, getBukkitPlayer().getLocation());
    }

    public void dropItems(List<ItemStack> items, Location location) {
        for (ItemStack item : items) {
            getBukkitPlayer().getWorld().dropItemNaturally(location, item);
        }
    }

    public boolean hasExtraData(String value) {
        return getExtraData(value) != null;
    }

    public QuestLine getQuestLine() {
        return Skyblock.getPlugin().getQuestLineHandler().getFromPlayer(this);
    }

    public boolean hasTelekinesis() {
        try {
            ItemBase base = new ItemBase(getBukkitPlayer().getItemInHand());

            return base.hasEnchantment(Skyblock.getPlugin().getEnchantmentHandler().getEnchantment("telekinesis"));
        } catch (Exception ex) {
            return false;
        }
    }

    public void onQuit() {
        if (getPetDisplay() != null) getPetDisplay().remove();
        if (getArmorSet() != null) {
            getArmorSet().stopFullSetBonus(getBukkitPlayer());
        }

        saveToDisk();

        playerRegistry.remove(getBukkitPlayer().getUniqueId());
    }

}
