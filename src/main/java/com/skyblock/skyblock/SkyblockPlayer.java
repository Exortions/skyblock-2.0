package com.skyblock.skyblock;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.bags.Bag;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.items.ArmorSet;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.features.merchants.Merchant;
import com.skyblock.skyblock.features.scoreboard.HubScoreboard;
import com.skyblock.skyblock.features.scoreboard.Scoreboard;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
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

    private static int EVERY_SECOND = 20;
    private static int EVERY_THREE_SECONDS = 60;
    public static HashMap<UUID, SkyblockPlayer> playerRegistry = new HashMap<>();

    public static SkyblockPlayer getPlayer(Player player) {
        return playerRegistry.get(player.getUniqueId());
    }

    public static void registerPlayer(UUID uuid) {
        playerRegistry.put(uuid, new SkyblockPlayer(uuid));
    }

    private List<BiFunction<SkyblockPlayer, Entity, Integer>> predicateDamageModifiers;
    private int damageModifier;
    private Player bukkitPlayer;
    private HashMap<SkyblockStat, Integer> stats;
    private HashMap<String, Boolean> cooldowns;
    private HashMap<String, Object> extraData;
    private FileConfiguration config;
    private ArmorSet armorSet;
    private File configFile;
    private Scoreboard board;
    private ItemStack hand;
    private String actionBar;
    private int tick;

    public SkyblockPlayer(UUID uuid) {
        predicateDamageModifiers = new ArrayList<>();
        damageModifier = 0;
        bukkitPlayer = Bukkit.getPlayer(uuid);
        cooldowns = new HashMap<>();
        extraData = new HashMap<>();
        stats = new HashMap<>();
        tick = 0;
        hand = Util.getEmptyItemBase();
        armorSet = null;

        this.extraData.put("fullSetBonus", false);
        this.extraData.put("fullSetBonusType", null);

        this.extraData.put("isEndStoneSwordActive", false);
        this.extraData.put("endStoneSwordDamage", 0);

        initConfig();
    }

    public void tick() {
        if (tick == 0) {
            loadStats();
            resetActionBar();
            board = new HubScoreboard(getBukkitPlayer());
        }

        if (tick % EVERY_SECOND == 0) {
            board.updateScoreboard();

            ActionBarAPI.sendActionBar(getBukkitPlayer(), actionBar);

            if (getStat(SkyblockStat.MANA) < getStat(SkyblockStat.MAX_MANA) - ((getStat(SkyblockStat.MAX_MANA) + 100)/50)) {
                setStat(SkyblockStat.MANA, getStat(SkyblockStat.MANA) + ((getStat(SkyblockStat.MAX_MANA) + 100)/50));
            }else{
                setStat(SkyblockStat.MANA, getStat(SkyblockStat.MAX_MANA));
            }
        }

        if (tick % EVERY_THREE_SECONDS == 0) {
            if (getStat(SkyblockStat.HEALTH) < getStat(SkyblockStat.MAX_HEALTH) - (int) (1.5 + getStat(SkyblockStat.MAX_HEALTH) / 100)) {
                updateHealth((int) (1.5 + getStat(SkyblockStat.MAX_HEALTH)/100));
            }else{
                setStat(SkyblockStat.HEALTH, getStat(SkyblockStat.MAX_HEALTH));
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

        Object young = getExtraData("young_dragon_bonus");
        int speedCap = 400;
        if (young != null) {
           speedCap = (boolean) young ? 500 : 400;
        }

        if (getStat(SkyblockStat.SPEED) > speedCap) setStat(SkyblockStat.SPEED, speedCap);

        if (bukkitPlayer.getLocation().getY() <= -11) kill(EntityDamageEvent.DamageCause.VOID, null);

        bukkitPlayer.setWalkSpeed(Math.min((float) (getStat(SkyblockStat.SPEED) / 500.0), 1.0f));
        bukkitPlayer.setMaxHealth(Math.round(Math.min(40.0, 20.0 + ((getStat(SkyblockStat.MAX_HEALTH) - 100.0) / 25.0))));
        bukkitPlayer.setHealth(Math.max(1, bukkitPlayer.getMaxHealth() * ((double) getStat(SkyblockStat.HEALTH) / (double) getStat(SkyblockStat.MAX_HEALTH))));

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
        addStat(SkyblockStat.MANA, mult * base.getIntelligence());
        addStat(SkyblockStat.MAX_MANA, mult * base.getIntelligence());
        addStat(SkyblockStat.SPEED, mult * base.getSpeed());
        addStat(SkyblockStat.STRENGTH, mult * base.getStrength());
    }

    private void updateHealth(int i) {
        int hp = getStat(SkyblockStat.HEALTH);
        int mhp = getStat(SkyblockStat.MAX_HEALTH);

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

    // hearts = max * (health / max)
    public void damage(double damage, EntityDamageEvent.DamageCause cause, Entity attacker) {
        double d = (damage - (damage * ((getStat(SkyblockStat.DEFENSE) / (getStat(SkyblockStat.DEFENSE) + 100F)))));

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

        if (isOnIsland()) return;

        int sub = (int) getValue("stats.purse") / 2;
        bukkitPlayer.sendMessage(ChatColor.RED + "You died and lost " + Util.formatInt(sub) + " coins!");
        bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ZOMBIE_METAL, 1f, 2f);

        bukkitPlayer.setVelocity(new Vector(0, 0, 0));
        bukkitPlayer.setFallDistance(0.0f);
        bukkitPlayer.teleport(new Location(bukkitPlayer.getWorld(), -2 , 70,  -84,  -180, 0));

        setValue("stats.purse", sub);
    }

    public boolean isOnIsland() {
        return bukkitPlayer.getWorld().getName().startsWith(IslandManager.ISLAND_PREFIX);
    }

    public int getStat(SkyblockStat stat) { return stats.get(stat); }

    public void setStat(SkyblockStat stat, int val) {
        stats.put(stat, val);

        setValue("stats." + stat.name().toLowerCase(), val);

        resetActionBar();
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

    public void addStat(SkyblockStat stat, int val) {
        setStat(stat, getStat(stat) + val);
    }

    public void subtractStat(SkyblockStat stat, int val) {
        setStat(stat, getStat(stat) - val);
    }

    public Object getValue(String path) {
        return config.get(path);
    }

    public void setValue(String path, Object item) {
        try {
            config.set(path, item);
            config.save(configFile);
            config = YamlConfiguration.loadConfiguration(configFile);

            forEachStat((s) -> stats.put(s, (int) getValue("stats." + s.name().toLowerCase())));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void forEachStat(Consumer<SkyblockStat> cons) {
        for (SkyblockStat stat : SkyblockStat.values()) {
            cons.accept(stat);
        }
    }

    private void loadStats() {
        forEachStat((s) -> setStat(s, (int) getValue("stats." + s.name().toLowerCase())));
    }

    private void initConfig() {
        File folder = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + "Players");
        if (!folder.exists())  folder.mkdirs();
        configFile = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + "Players" + File.separator + getBukkitPlayer().getUniqueId() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                forEachStat((s) -> config.set("stats." + s.name().toLowerCase(), 0));

                config.set("stats." + SkyblockStat.MAX_HEALTH.name().toLowerCase(), 100);
                config.set("stats." + SkyblockStat.HEALTH.name().toLowerCase(), 100);
                config.set("stats." + SkyblockStat.MAX_MANA.name().toLowerCase(), 100);
                config.set("stats." + SkyblockStat.MANA.name().toLowerCase(), 100);
                config.set("stats." + SkyblockStat.SPEED.name().toLowerCase(), 100);
                config.set("stats." + SkyblockStat.CRIT_CHANCE.name().toLowerCase(), 30);
                config.set("stats." + SkyblockStat.CRIT_DAMAGE.name().toLowerCase(), 50);

                config.set("stats.purse", 0);

                for (Collection collection : Collection.getCollections()) {
                    config.set("collection." + collection.getName().toLowerCase() + ".level", 0);
                    config.set("collection." + collection.getName().toLowerCase() + ".exp", 0);
                    config.set("collection." + collection.getName().toLowerCase() + ".unlocked", false);
                }

                config.set("bank.balance", 0);
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

                config.save(configFile);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public SkyblockLocation getCurrentLocation() {
        return Skyblock.getPlugin(Skyblock.class).getLocationManager().getLocation(bukkitPlayer.getLocation());
    }

    public boolean isNotOnPrivateIsland() {
        return !bukkitPlayer.getWorld().getName().equals(IslandManager.getIsland(bukkitPlayer).getName());
    }

    public void addTransaction(int amount, String by) {
        List<String> transactions = (List<String>) getValue("bank.recent_transactions");

        if (transactions.size() >= 10) transactions.remove(0);

        transactions.add("" + amount + ";" + System.currentTimeMillis() + ";" + by);

        setValue("bank.recent_transactions", transactions);
    }

    public boolean deposit(int amount, boolean self) {
        int balance = (int) getValue("bank.balance");
        int purse = (int) getValue("stats.purse");

        if (purse < amount) return false;

        setValue("bank.balance", balance + amount);
        setValue("stats.purse", purse - amount);

        this.addTransaction(amount, self ? getBukkitPlayer().getDisplayName() : "Bank Interest");

        return true;
    }

    public boolean withdraw(int amount) {
        int balance = (int) getValue("bank.balance");
        int purse = (int) getValue("stats.purse");

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

    public void addCoins(int coins) {
        setValue("stats.purse", getCoins() + coins);
    }

    public int getCoins() {
        return (int) getValue("stats.purse");
    }

    public void addPredicateDamageModifier(BiFunction<SkyblockPlayer, Entity, Integer> damageModifier) {
        this.predicateDamageModifiers.add(damageModifier);
    }

    public void removePredicateDamageModifier(int index) {
        this.predicateDamageModifiers.remove(index);
    }

}
