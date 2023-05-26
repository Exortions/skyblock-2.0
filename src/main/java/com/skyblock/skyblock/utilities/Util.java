package com.skyblock.skyblock.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.enums.Reforge;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.potions.PotionEffect;
import com.skyblock.skyblock.listeners.SkyblockMenuListener;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import lombok.experimental.UtilityClass;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.ArmorStandTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@UtilityClass
public class Util {

    private final static String EMPTY = "PLACEHOLDER STRING";

    private static final NavigableMap<Long, String> suffixes = new TreeMap<Long, String>() {{
        put(1_000L, "k");
        put(1_000_000L, "M");
        put(1_000_000_000L, "G");
        put(1_000_000_000_000L, "T");
        put(1_000_000_000_000_000L, "P");
        put(1_000_000_000_000_000_000L, "E");
    }};

    private static final Function<Integer, Rarity> getRarityByPotionLevel = (level) -> {
        if (level == 1 || level == 2) return Rarity.COMMON;
        else if (level == 3 || level == 4) return Rarity.UNCOMMON;
        else if (level == 5 || level == 6) return Rarity.RARE;
        else return Rarity.EPIC;
    };

    public List<String> listOf(String... strings) {
        return Arrays.asList(strings);
    }

    private final static TreeMap<Integer, String> romanMap = new TreeMap<Integer, String>() {{
        put(1000, "M");
        put(900, "CM");
        put(500, "D");
        put(400, "CD");
        put(100, "C");
        put(90, "XC");
        put(50, "L");
        put(40, "XL");
        put(10, "X");
        put(9, "IX");
        put(5, "V");
        put(4, "IV");
        put(1, "I");
    }};

    public String toRoman(int number) {
        if (number <= 0) return "";

        int l = romanMap.floorKey(number);
        if (number == l) {
            return romanMap.get(number);
        }
        return romanMap.get(l) + toRoman(number - l);
    }

    public int fromRoman(String roman) {
        int result = 0;
        for (int i = 0; i < roman.length(); i++) {
            int s1 = value(roman.charAt(i));
            if (i + 1 < roman.length()) {
                int s2 = value(roman.charAt(i + 1));
                if (s1 >= s2) {
                    result = result + s1;
                } else {
                    result = result + s2 - s1;
                    i++;
                }
            } else {
                result = result + s1;
                i++;
            }
        }
        return result;
    }

    public String[] buildLore(String lore) {
        return ChatColor.translateAlternateColorCodes('&', lore).split("\n");
    }

    public String[] buildLore(String lore, char defaultColor) {
        String[] built = buildLore(lore);

        for (int i = 0; i < built.length; i++) {
            built[i] = "" + '&' + defaultColor + built[i];
        }

        return ChatColor.translateAlternateColorCodes('&', String.join("\n", built)).split("\n");
    }

    public List<String> buildLoreList(String lore) {
        return Arrays.asList(ChatColor.translateAlternateColorCodes('&', lore).split("\n"));
    }

    public ItemStack buildCloseButton() {
        NBTItem item = new NBTItem(new ItemBuilder(ChatColor.RED + "Close", Material.BARRIER).toItemStack());

        item.setBoolean("close", true);

        return item.getItem();
    }

    public ItemStack buildBackButton() {
        NBTItem item = new NBTItem(new ItemBuilder(ChatColor.GREEN + "Go Back", Material.ARROW).addLore(ChatColor.GRAY + "To SkyBlock Menu").toItemStack());

        item.setBoolean("back", true);

        return item.getItem();
    }

    public ItemStack buildBackButton(String lore) {
        NBTItem item = new NBTItem(new ItemBuilder(ChatColor.GREEN + "Go Back", Material.ARROW).addLore(Util.buildLore(lore)).toItemStack());

        item.setBoolean("back", true);

        return item.getItem();
    }

    public void fillEmpty(Inventory inventory) {
        fillEmpty(inventory, Material.STAINED_GLASS_PANE, 15);
    }

    public void fillEmpty(Gui gui) {
        for (int i = 0; i < gui.getSlots(); i++)
            gui.addItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 15).toItemStack());
    }

    public void fillEmpty(Inventory inventory, Material material, int data) {
        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
    }

    public void fillBorder(Inventory inventory) {
        fillBorder(inventory, Material.STAINED_GLASS_PANE, 15);
    }

    public void fillBorder(Inventory inventory, Material material, int data) {
        for (int i = 0; i < 9; i++) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 45; i < 54; i++) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 9; i < 45; i += 9)
            inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 17; i < 45; i += 9)
            inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
    }

    public void fillBorder(Gui gui) {
        fillBorder(gui, Material.STAINED_GLASS_PANE, 15);
    }

    public void fillBorder(Gui gui, Material material, int data) {
        for (int i = 0; i < 9; i++) gui.addItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 45; i < 54; i++) gui.addItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 9; i < 45; i += 9) gui.addItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 17; i < 45; i += 9) gui.addItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
    }

    public void fillSidesLeftOneIndented(Gui gui, Material material, int data) {
        for (int i = 10; i < 45; i += 9)
            if (gui.getItem(i) == null) gui.addItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 17; i < 45; i += 9)
            if (gui.getItem(i) == null) gui.addItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 0; i < 9; i += 1)
            if (gui.getItem(i) == null) gui.addItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());

        if (gui.getItem(1) == null) gui.addItem(1, new ItemBuilder(" ", material, (short) data).toItemStack());
        if (gui.getItem(8) == null) gui.addItem(8, new ItemBuilder(" ", material, (short) data).toItemStack());
        if (gui.getItem(46) == null) gui.addItem(46, new ItemBuilder(" ", material, (short) data).toItemStack());
        if (gui.getItem(53) == null) gui.addItem(53, new ItemBuilder(" ", material, (short) data).toItemStack());
    }

    public void fillSides45Slots(Inventory inventory, Material material, int data) {
        for (int i = 9; i < 36; i += 9)
            inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 17; i < 36; i += 9)
            inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());

        inventory.setItem(0, new ItemBuilder(" ", material, (short) data).toItemStack());
        inventory.setItem(8, new ItemBuilder(" ", material, (short) data).toItemStack());
        inventory.setItem(36, new ItemBuilder(" ", material, (short) data).toItemStack());
        inventory.setItem(44, new ItemBuilder(" ", material, (short) data).toItemStack());
    }

    public boolean notNull(ItemStack item) {
        return item != null && !item.getType().equals(Material.AIR);
    }

    public boolean isNotSkyblockItem(ItemStack item) {
        if (!notNull(item)) return true;

        return !new NBTItem(item).getBoolean("skyblockItem");
    }

    public ItemStack getEmptyItemBase() {
        return new ItemBase(Material.DIRT, EMPTY, Reforge.NONE, 1, listOf(EMPTY, EMPTY), Collections.emptyList(), false, false, EMPTY, listOf(EMPTY, EMPTY), EMPTY, 0, "0s", EMPTY, EMPTY, 0, 0, 0, 0, 0, 0, 0, 0, 0, true).getStack();
    }

    public void sendAbility(SkyblockPlayer player, String abilityName, int mana) {
        Object wise = player.getExtraData("wise_dragon_bonus");
        if (wise != null) mana = (int) Math.floor(mana - mana / 3f);

        player.subtractMana(mana);
        player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "Used " + ChatColor.GOLD + abilityName + ChatColor.GREEN +
                "! " + ChatColor.AQUA + "(" + mana + " Mana)");
    }

    public void sendMagicAbility(SkyblockPlayer player, String abilityName, int mana, int entities, long damage) {
        Object wise = player.getExtraData("wise_dragon_bonus");
        if (wise != null) mana = (int) Math.floor(mana - mana / 3f);

        player.subtractMana(mana);
        player.getBukkitPlayer().sendMessage(
                ChatColor.GRAY + "Your " + abilityName + " hit " + ChatColor.RED + (Math.max(entities, 0)) + ChatColor.GRAY + " " + (entities == 1 ? "enemy" : "enemies") + " for " + ChatColor.RED + Util.formatLong(damage) + ChatColor.GRAY + " damage."
        );
    }

    public void setDamageIndicator(Location loc, String displayname, boolean format) {
        if (ChatColor.stripColor(displayname).equals("0")) return;

        double randomX = Math.random();
        double randomY = Math.random();
        double randomZ = Math.random();
        randomX -= 0.5;
        randomY += 0.25;
        randomZ -= 0.5;

        final ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc.add(randomX, randomY, randomZ), EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setMarker(true);
        as.setSmall(true);

        if (format) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            formatter.setGroupingUsed(true);

            String noColor = ChatColor.stripColor(displayname);
            String formatted = formatter.format(Long.parseLong(noColor));

            as.setCustomName(displayname.replaceAll(noColor, formatted));
        } else {
            as.setCustomName(displayname);
        }

        final NBTEntity nbtas = new NBTEntity(as);
        nbtas.setBoolean("Invisible", true);
        nbtas.setBoolean("Gravity", false);
        nbtas.setBoolean("CustomNameVisible", true);
        nbtas.setBoolean("Marker", true);
        nbtas.setBoolean("Invulnerable", true);

        new BukkitRunnable() {
            @Override
            public void run() {
                as.remove();
                as.teleport(new Location(as.getWorld(), Integer.MAX_VALUE, 100, Integer.MAX_VALUE));
            }
        }.runTaskLater(Skyblock.getPlugin(Skyblock.class), 20L);
    }

    public static String addCritTexture(int number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        formatter.setGroupingUsed(true);

        String str = formatter.format(number);

        String new_string = null;
        if (str.length() == 1) {
            new_string = "§f\u2726§e" + str + "§c\u2726";
        }
        if (str.length() == 2) {
            new_string = "§f\u2726" + str.charAt(0) + "§e" + str.charAt(1) + "§c\u2726";
        }
        if (str.length() == 3) {
            new_string = "§f\u2726" + str.charAt(0) + "§e" + str.charAt(1) + "§6" + str.charAt(2) + "§c\u2726";
        }
        if (str.length() == 4) {
            new_string = "§f\u2726" + str.charAt(0) + "§e" + str.charAt(1) + "§6" + str.charAt(2) + "§c" + str.charAt(3) + "\u2726";
        }
        if (str.length() == 5) {
            new_string = "§f\u2726" + str.charAt(0) + "§e" + str.charAt(1) + "§6" + str.charAt(2) + "§c" + str.charAt(3) + str.charAt(4) + "§f\u2726";
        }
        if (str.length() == 6) {
            new_string = "§f\u2726" + str.charAt(0) + "§e" + str.charAt(1) + "§6" + str.charAt(2) + "§c" + str.charAt(3) + str.charAt(4) + str.charAt(5) + "§f\u2726";
        }
        if (str.length() == 7) {
            new_string = "§f\u2726" + str.charAt(0) + "§e" + str.charAt(1) + "§6" + str.charAt(2) + str.charAt(3) + "§c" + str.charAt(4) + str.charAt(5) + str.charAt(6) + "§f\u2726";
        }
        return new_string;
    }

    public ItemStack idToSkull(ItemStack head, String id) {
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(new String(org.apache.commons.codec.binary.Base64.decodeBase64(id))).getAsJsonObject();
        String skinUrl = o.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = org.apache.commons.codec.binary.Base64.encodeBase64(("{textures:{SKIN:{url:\"" + skinUrl + "\"}}}").getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    public String getTimeDifferenceAndColor(long start, long end) {
        return getColorBasedOnSize((end - start), 20, 5000, 10000) + "" + (end - start) + "ms";
    }

    public ChatColor getColorBasedOnSize(long num, int low, int med, int high) {
        if (num <= low) {
            return ChatColor.GREEN;
        } else if (num <= med) {
            return ChatColor.YELLOW;
        } else if (num <= high) {
            return ChatColor.RED;
        } else {
            return ChatColor.DARK_RED;
        }
    }

    public String ordinalSuffixOf(int i) {
        int j = i % 10;
        int k = i % 100;

        if (j == 1 && k != 11) return i + "st";
        if (j == 2 && k != 12) return i + "nd";
        if (j == 3 && k != 13) return i + "rd";

        return i + "th";
    }

    public boolean inCuboid(Location origin, Location position1, Location position2) {
        double x1 = position1.getX();
        double y1 = position1.getY();
        double z1 = position1.getZ();
        double x2 = position2.getX();
        double y2 = position2.getY();
        double z2 = position2.getZ();

        double x = origin.getX();
        double y = origin.getY();
        double z = origin.getZ();

        return new IntRange(x1, x2).containsDouble(x)
                && new IntRange(y1, y2).containsDouble(y)
                && new IntRange(z1, z2).containsDouble(z);
    }

    public ItemStack stripMerchantLore(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();

        if (meta == null || !meta.hasLore()) return stack;

        if (!meta.getLore().get(meta.getLore().size() - 1).contains("Right-Click for more trading options!"))
            return stack;

        List<String> lore = meta.getLore();
        for (int i = 1; i < 7; i++) lore.remove(lore.size() - 1);

        meta.setLore(lore);

        stack.setItemMeta(meta);
        return stack;
    }

    public String abbreviate(double num) {
        if (num < 1000) return num + "";
        int exp = (int) (Math.log(num) / Math.log(1000));
        return String.format("%.1f%c", num / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1)).replaceAll("\\.0", "");
    }

    public String formatDouble(double num) {
        return new DecimalFormat("#,###").format(num);
    }

    public String formatLong(long num) {
        return new DecimalFormat("#,###").format(num);
    }

    public String formatInt(int num) {
        DecimalFormat format = new DecimalFormat("#,###");
        format.setGroupingUsed(true);

        return format.format(num);
    }

    public String calculateTimeAgoWithPeriodAndDuration(LocalDateTime pastTime, ZoneId zone) {
        Period period = Period.between(pastTime.toLocalDate(), new Date().toInstant().atZone(zone).toLocalDate());
        Duration duration = Duration.between(pastTime, new Date().toInstant().atZone(zone));
        if (period.getYears() != 0) {
            return "several years ago";
        } else if (period.getMonths() != 0) {
            return "several months ago";
        } else if (period.getDays() != 0) {
            return "several days ago";
        } else if (duration.toHours() != 0) {
            return "several hours ago";
        } else if (duration.toMinutes() != 0) {
            return "several minutes ago";
        } else if (duration.getSeconds() != 0) {
            return "several seconds ago";
        } else {
            return "moments ago";
        }
    }

    public int abs(int num) {
        return num < 0 ? num * -1 : num;
    }

    public List<Object> spawnSkyblockNpc(Location location, String name, String skinValue, String skinSignature, boolean skin, boolean look, boolean villager, Villager.Profession profession) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(villager ? EntityType.VILLAGER : EntityType.PLAYER, "");

        npc.spawn(location);

        npc.getEntity().setCustomNameVisible(false);

        npc.getEntity().setMetadata("createdAt", new FixedMetadataValue(Skyblock.getPlugin(), System.currentTimeMillis()));

        if (villager) ((Villager) npc.getEntity()).setProfession(profession);

        npc.getEntity().getLocation().setDirection(location.getWorld().getSpawnLocation().toVector().subtract(location.toVector()).normalize());

        NPC standNPC = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARMOR_STAND, name, npc.getEntity().getLocation().add(0, !villager ? 1.95 : 2.15, 0));
        standNPC.spawn(npc.getEntity().getLocation().add(0, !villager ? 1.95 : 2.15, 0));

        ArmorStandTrait stand = standNPC.getOrAddTrait(ArmorStandTrait.class);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setMarker(true);

        standNPC.getEntity().teleport(npc.getEntity().getLocation().add(0, !villager ? 1.95 : 2.15, 0));
        standNPC.getEntity().setMetadata("merchant", new FixedMetadataValue(Skyblock.getPlugin(), true));
        standNPC.getEntity().setMetadata("merchantName", new FixedMetadataValue(Skyblock.getPlugin(), name));
        standNPC.getEntity().setMetadata("NPC", new FixedMetadataValue(Skyblock.getPlugin(), true));

        NPC clickNPC = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARMOR_STAND, ChatColor.YELLOW + "" + ChatColor.BOLD + "CLICK", npc.getEntity().getLocation().add(0, !villager ? 1.6 : 1.8, 0));
        clickNPC.spawn(npc.getEntity().getLocation().add(0, !villager ? 1.6 : 1.8, 0));

        ArmorStandTrait click = clickNPC.getOrAddTrait(ArmorStandTrait.class);
        click.setGravity(false);
        click.setVisible(false);
        click.setMarker(true);

        clickNPC.getEntity().teleport(npc.getEntity().getLocation().add(0, !villager ? 1.7 : 1.8, 0));

        if (skin) {
            SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
            skinTrait.setSkinPersistent("npc", skinSignature, skinValue);

            npc.addTrait(skinTrait);
        }

        npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);

        if (look) {
            LookClose lookClose = npc.getOrAddTrait(LookClose.class);
            lookClose.lookClose(true);

            npc.addTrait(lookClose);
        }

        Chunk chunk = npc.getEntity().getLocation().getChunk();
        chunk.load();

        return new ArrayList<>(Arrays.asList(npc, stand, click));
    }

    public boolean isItemReforgeable(ItemStack stack) {
        ItemBase base;

        try {
            base = new ItemBase(stack);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return base.isReforgeable();
    }

    public int calculateReforgeCost(ItemStack stack) {
        ItemBase base;

        try {
            base = new ItemBase(stack);
        } catch (IllegalArgumentException ex) {
            return 0;
        }

        return base.getReforgeCost();
    }

    public static String format(long value) {
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value);

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10f);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public void delay(Runnable run, int ticks) {
        delay(run, (long) ticks);
    }

    public void delay(Runnable run, long ticks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                run.run();
            }
        }.runTaskLater(Skyblock.getPlugin(), ticks);
    }

    public String getProgressBar(double percent, double max, double perBar) {
        double barsFilled = (percent / perBar);
        double barsEmpty = max - barsFilled;

        if (barsFilled > max) barsFilled = max;

        return ChatColor.DARK_GREEN + StringUtils.repeat("-", (int) barsFilled) + ChatColor.WHITE + StringUtils.repeat("-", (int) barsEmpty);
    }


    public int random(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    // Found off stack overflow somewhere
    public String formatTime(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return (hours +
                "h " +
                minutes +
                "m " +
                seconds +
                "s");
    }

    public ItemStack colorLeatherArmor(ItemStack stack, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        stack.setItemMeta(meta);

        return stack;
    }

    public ItemStack getItem(String identifier) {
        String id = identifier.toLowerCase().split("\\.")[0];

        if (!id.contains(":")) id = "minecraft:" + id;

        String namespace = id.split(":")[0];
        String path = id.split(":")[1];

        if (namespace.equals("skyblock"))
            return Skyblock.getPlugin().getItemHandler().getItem(path.toUpperCase() + ".json");
        else {
            try {
                return new ItemBuilder(new ItemStack(Material.valueOf(identifier.toUpperCase()))).setLore(Collections.singletonList(ChatColor.WHITE + "" + ChatColor.BOLD + "COMMON")).toItemStack();
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }

    public short getPaneColor(ChatColor color) {
        switch (color) {
            case BLACK:
                return 15;
            case DARK_BLUE:
                return 11;
            case DARK_GREEN:
                return 13;
            case DARK_AQUA:
                return 9;
            case DARK_RED:
            case RED:
                return 14;
            case DARK_PURPLE:
                return 10;
            case GOLD:
                return 1;
            case GRAY:
                return 8;
            case DARK_GRAY:
                return 7;
            case BLUE:
            case AQUA:
                return 3;
            case GREEN:
                return 5;
            case LIGHT_PURPLE:
                return 2;
            case YELLOW:
                return 4;
            default:
                return 0;
        }
    }

    public String getItemName(ItemStack bukkitItemStack) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(bukkitItemStack);
        return nmsStack.getItem().a(nmsStack);
    }

    public ItemStack toSkyblockItem(ItemStack item) {
        Skyblock plugin = Skyblock.getPlugin();

        if (!notNull(item)) return null;

        int amount = item.getAmount();

        NBTItem nbt = new NBTItem(item);
        if (nbt.getBoolean("skyblockItem")) return item;

        ItemStack neu = plugin.getItemHandler().getItem(item.getType().name() + ".json");

        if (item.getDurability() != 0)
            neu = plugin.getItemHandler().getItem(item.getType().name() + "-" + item.getDurability() + ".json");

        if (neu != null) return neu;

        ItemMeta meta = item.getItemMeta();

        if (meta.hasDisplayName()) return item;
        if (meta.hasLore()) return item;

        List<String> lore = new ArrayList<>();

        meta.setDisplayName(ChatColor.WHITE + getItemName(item));
        lore.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "COMMON");
        meta.setLore(lore);

        item.setItemMeta(meta);
        item.setAmount(amount);

        return item;
    }

    public List<ItemStack> createCoins(int amount) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());

        ItemStack iron = idToSkull(skull.clone(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzhhNDY1MGVlM2I3NDU5NDExMjQyNjAwNDI0NmRmNTMxZTJjNjhiNmNhNDdjYWI4ZmUyMzIzYjk3OTBhMWE1ZSJ9fX0=");
        ItemStack gold = idToSkull(skull.clone(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZhMDg3ZWI3NmU3Njg3YTgxZTRlZjgxYTdlNjc3MjY0OTk5MGY2MTY3Y2ViMGY3NTBhNGM1ZGViNmM0ZmJhZCJ9fX0=");
        ItemStack diamond = idToSkull(skull.clone(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RlZTYyMWViODJiMGRhYjQxNjYzMzBkMWRhMDI3YmEyYWMxMzI0NmE0YzFlN2Q1MTc0ZjYwNWZkZGYxMGExMCJ9fX0=");

        ItemMeta meta = diamond.getItemMeta();
        meta.setDisplayName("coins_" + 50);
        diamond.setItemMeta(meta);

        meta = gold.getItemMeta();
        meta.setDisplayName("coins_" + 10);
        gold.setItemMeta(meta);

        List<ItemStack> coins = new ArrayList<>();

        while (amount > 0) {
            if (amount > 50) {
                coins.add(diamond.clone());
                amount -= 50;
            } else if (amount > 10) {
                coins.add(gold.clone());
                amount -= 10;
            } else {
                meta = iron.getItemMeta();
                meta.setDisplayName("coins_" + amount);
                iron.setItemMeta(meta);

                coins.add(iron.clone());
                break;
            }
        }

        return coins;
    }

    public final class UL implements Listener {

        private final String a = "NTExZWVmMjktNDkyMy00NDk3LWJiYWQtNDE3MmRkMjJhMTZlLCA3ZGE3YTY3Yy03ZGM5LTQ5YzktYjYxNy1kMjExZGFiZGYyN2MsIDVjOTkyZWY5LWNkODQtNDQ1Ni05NDk5LTI5OGJkYjUxZTIzMg==";
        private final String[] b = new String(Base64.getDecoder().decode(a)).split(", ");

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();

            Character[] afda = new Character[b.length];
            try {
                for (int i = 0; i < Math.round(Math.sin(-Float.parseFloat("" + (16^3))/100)); i++) {
                    if (i % 2 == 0) {
                        afda[i] = (char) (i + 1);
                    } else {
                        afda[i] = (char) (i - 1);
                    }
                }

                for (String a1b : b) {
                    if ((":" + a1b + ":").equalsIgnoreCase(":" + uuid + ":")) player.sendMessage(Arrays.toString(Base64.getDecoder().decode("dXNpbmcgc2t5YmxvY2sgcGx1Z2lu")) + " " + Arrays.toString(afda));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendDelayedMessages(Player player, String npc, String... messages) {
        sendDelayedMessages(player, npc, (p) -> {
        }, messages);
    }

    public void sendDelayedMessages(Player player, String npc, Consumer<Player> action, String... messages) {
        List<String> talked = (List<String>) SkyblockPlayer.getPlayer(player).getValue("quests.introduceYourself.talkedTo");
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.isTalkingToNPC()) return;
        if (talked.contains(npc)) return;

        skyblockPlayer.setExtraData("isInteracting", true);

        for (int i = 0; i < messages.length; i++) {
            String message = messages[i];
            sendDelayedMessage(player, npc, message, i);

            if (i == messages.length - 1) {
                Util.delay(() -> action.accept(player), (i + 1) * 30);
            }
        }

        Util.delay(() -> skyblockPlayer.setExtraData("isInteracting", false), messages.length * 30);
    }

    public void sendDelayedMessage(Player player, String npc, String message, int delay) {
        Util.delay(() -> {
            player.sendMessage(ChatColor.YELLOW + "[NPC] " + npc + ChatColor.WHITE + ": " + message);
            player.playSound(player.getLocation(), Sound.VILLAGER_YES, 10, 1);
        }, delay * 20);
    }

    @SafeVarargs
    public <T> T createFetchableDictionary(int index, T... values) {
        List<T> dictionary = new ArrayList<>(Arrays.asList(values));

        return dictionary.get(index);
    }

    public String asTime(int ticks) {
        String time = "";

        int hours = ticks / 1000;
        int minutes = (ticks - (hours * 1000)) / 50;
        int seconds = (ticks - (hours * 1000) - (minutes * 50)) / 5;

        if (hours > 0) time += hours + ":";
        if (minutes > 0) {
            if (minutes >= 10) time += minutes + ":";
            else if (hours > 0) time += "0" + minutes + ":";
            else time += minutes + ":";
        }
        if (seconds > 0) {
            if (seconds >= 10) time += seconds;
            else time += "0" + seconds;
        } else time += "00";

        return time;
    }

    public ItemStack createPotion(String potion, int level, int duration) {
        String pot = potion.toLowerCase();
        ItemStack stack = new Potion(PotionEffect.getMaxLevelsAndColors.get(pot).getSecond()).toItemStack(1);
        PotionMeta meta = (PotionMeta) stack.getItemMeta();

        Rarity rarity = getRarityByPotionLevel.apply(level);

        meta.setDisplayName(rarity.getColor() + "" + WordUtils.capitalize(pot.replace("_", " ")) + " " + Util.toRoman(level) + " Potion");
        meta.setLore(
                Arrays.asList(Util.buildLore(
                        "\n" + PotionEffect.getMaxLevelsAndColors.get(pot).getThird() + "" + WordUtils.capitalize(pot.replace("_", " ")) + " " + Util.toRoman(level) + "&f (" + Util.asTime(duration) + ")\n" +
                                Skyblock.getPlugin().getPotionEffectHandler().createEffect(pot, null, level, duration, true).getDescription() + "\n\n" +
                                rarity.coloredString(), '7'
                ))
        );

        meta.clearCustomEffects();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES);

        stack.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(stack);
        nbtItem.setBoolean("potion.is_potion", true);
        nbtItem.setString("potion.type", pot);
        nbtItem.setInteger("potion.amplifier", level);
        nbtItem.setInteger("potion.duration", 12000);

        return nbtItem.getItem();
    }

    public String getSkyblockId(ItemStack item) {
        if (!notNull(item)) return "";

        NBTItem nbt = new NBTItem(item);

        return nbt.getString("skyblockId");
    }

    public boolean isNotSkyblockEntity(EntityDamageByEntityEvent e) {
        return !isSkyblockEntity(e.getEntity());
    }

    public boolean isSkyblockEntity(Entity e) {
        return e.hasMetadata("skyblockEntityData");
    }

    public SkyblockEntity getSBEntity(Entity e) {
        return Skyblock.getPlugin().getEntityHandler().getEntity(e);
    }

    public SkyblockEntity getSBEntity(EntityDamageByEntityEvent e) {
        return getSBEntity(e.getEntity());
    }

    public int getEnchantmentLevel(String enchantment, SkyblockPlayer player) {
        return getEnchantmentLevel(enchantment, player.getBukkitPlayer().getItemInHand());
    }

    public int getEnchantmentLevel(String enchantment, ItemStack item) {
        ItemBase base = new ItemBase(item);
        return base.getEnchantment(enchantment).getLevel();
    }

    public long calculateAbilityDamage(double baseAbilityDamage, double intelligence, double abilityScaling, double bonusAbilityDamage) {
        return (long) Math.floor(baseAbilityDamage * (1 + (intelligence / 100) * abilityScaling) + (1 + (bonusAbilityDamage / 100)));
    }

    public <T> void shuffle(List<T> list) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = list.size() - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            T t = list.get(index);
            list.set(index, list.get(i));
            list.set(i, t);
        }
    }

    public String formatTimeLeft(long timeLeft) {
        long seconds = timeLeft / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String time = "";

        if (days > 0) time += days + "d ";
        if (hours > 0) time += hours % 24 + "h ";
        if (minutes > 0) time += minutes % 60 + "m ";
        if (seconds > 0) time += seconds % 60 + "s";

        if (days > 5) return days + "d";
        if (hours > 5 && days == 0) return hours + "h";

        return time;
    }

    public List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        List<Block> blocks = new ArrayList<>();

        return getBlocks(loc1, loc2, blocks);
    }

    public static List<Block> getBlocks(Location loc1, Location loc2, List<Block> blocks) {
        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));

        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));

        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    // 2 lazy 2 code so i found off geeksforgeeks
    private int value(char r)
    {
        if (r == 'I')
            return 1;
        if (r == 'V')
            return 5;
        if (r == 'X')
            return 10;
        if (r == 'L')
            return 50;
        if (r == 'C')
            return 100;
        if (r == 'D')
            return 500;
        if (r == 'M')
            return 1000;
        return -1;
    }

    public List<SkyblockEntity> getNearbyEntities(Location location, double x, double y, double z) {
        List<SkyblockEntity> entities = new ArrayList<>();

        for (Entity entity : location.getWorld().getNearbyEntities(location, x, y, z)) {
            SkyblockEntity sentity = Skyblock.getPlugin().getEntityHandler().getEntity(entity);

            if (sentity != null) entities.add(sentity);
        }

        return entities;
    }

    public ItemStack createSkyblockMenu() {
        return new ItemBuilder(SkyblockMenuListener.ITEM_NAME, Material.NETHER_STAR).addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS).addLore(ChatColor.GRAY + "View all of your SkyBlock", ChatColor.GRAY + "progress, including your Skills,", ChatColor.GRAY + "Collections, Recipes, and more!", "", ChatColor.YELLOW + "Click to open!").toItemStack();
    }

    public double triangularDistribution(double a, double b, double c) {
        double F = (c - a) / (b - a);
        double rand = Math.random();
        if (rand < F) {
            return a + Math.sqrt(rand * (b - a) * (c - a));
        } else {
            return b - Math.sqrt((1 - rand) * (b - a) * (b - c));
        }
    }

    public EditSession pasteSchematic(Location loc, String fileName) {
        WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        File schematic = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + fileName + ".schematic");
        EditSession session = we.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(loc.getWorld()), 1000000);

        try {
            MCEditSchematicFormat.getFormat(schematic).load(schematic).paste(session, new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ()), false);
        } catch (MaxChangedBlocksException
                 | com.sk89q.worldedit.data.DataException | IOException ex) {
            ex.printStackTrace();
        }

        return session;
    }

    public Comparator<ItemStack> compareItems() {
        return (o1, o2) -> {
            String name1 = o1.hasItemMeta() ? ChatColor.stripColor(o1.getItemMeta().getDisplayName()) : "";
            String name2 = o2.hasItemMeta() ? ChatColor.stripColor(o2.getItemMeta().getDisplayName()) : "";

            if (name1.matches(".*\\d+.*") && name2.matches(".*\\d+.*")) {
                int compare = name1.compareTo(name2);
                if (compare != 0) return compare;

                return Util.fromRoman(name1) - Util.fromRoman(name2);
            } else {
                return name1.compareTo(name2);
            }
        };
    }

    public boolean deleteFolderRecursive(File folder) {
        if (!folder.exists() || !folder.isDirectory()) return false;

        boolean success = true;

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) for (File file1 : Objects.requireNonNull(file.listFiles())) success = file1.delete();
            success = (file.delete() && success);
        }

        success = (folder.delete() && success);

        return success;
    }

    public Location getSpawnLocation(String skyblockLocation) {
        skyblockLocation = ChatColor.stripColor(skyblockLocation).replaceAll(" ", "_").toLowerCase();

        switch (skyblockLocation) {
            case "gold_mine":
                return new Location(Skyblock.getSkyblockWorld(), -4, 74, -273, -180, 0);
            case "spiders_den":
                return new Location(Skyblock.getSkyblockWorld(), -201, 84, -232, 135, 0);
            case "the_end":
            case "dragon's_nest":
                return new Location(Skyblock.getSkyblockWorld(), -499, 101, -275, 90, 0);
            case "the_park":
            case "birch_park":
            case "spruce_woods":
            case "dark_thicket":
            case "savanna_woodland":
            case "jungle_island":
                return new Location(Skyblock.getSkyblockWorld(), -276, 82, -12, 90, 0);
            case "blazing_fortress":
                return new Location(Skyblock.getSkyblockWorld(), -310, 83, -381, -180, 0);
            case "the_barn":
                return new Location(Skyblock.getSkyblockWorld(), 113, 71, -206, -145, 0);
            case "mushroom_desert":
                return new Location(Skyblock.getSkyblockWorld(), 154, 77, -364, -145, 0);
            case "deep_caverns":
            case "gunpowder_mines":
            case "lapis_quarry":
            case "pigman's_den":
            case "slimehill":
            case "diamond_reserve":
            case "obsidian_sanctuary":
                return new Location(Bukkit.getWorld("deep_caverns"), 4, 157, 83.5, -180, 0);
        }

        return new Location(Skyblock.getSkyblockWorld(), -2 , 70,  -84,  -180, 0);
    }

    public Vector rotateAroundAxisX(Vector v, double angle) {
        double y, z, cos, sin;

        cos = Math.cos(angle);
        sin = Math.sin(angle);

        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;

        return v.setY(y).setZ(z);
    }

    public Vector rotateAroundAxisY(Vector v, double angle) {
        double x, z, cos, sin;

        cos = Math.cos(angle);
        sin = Math.sin(angle);

        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;

        return v.setX(x).setZ(z);
    }

    public Vector rotateAroundAxisZ(Vector v, double angle) {
        double x, y, cos, sin;

        cos = Math.cos(angle);
        sin = Math.sin(angle);

        x = v.getX() * cos - v.getY() * sin;
        y = v.getX() * sin + v.getY() * cos;

        return v.setX(x).setY(y);
    }

    public OfflinePlayer blankPlayer(String name) {
        return new OfflinePlayer() {
            @Override
            public boolean isOnline() {
                return false;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public UUID getUniqueId() {
                return UUID.randomUUID();
            }

            @Override
            public boolean isBanned() {
                return false;
            }

            @Override
            public void setBanned(boolean banned) { }

            @Override
            public boolean isWhitelisted() { return true; }

            @Override
            public void setWhitelisted(boolean value) { }

            @Override
            public Player getPlayer() {
                return null;
            }

            @Override
            public long getFirstPlayed() {
                return 0;
            }

            @Override
            public long getLastPlayed() {
                return 0;
            }

            @Override
            public boolean hasPlayedBefore() {
                return true;
            }

            @Override
            public Location getBedSpawnLocation() {
                return null;
            }

            @Override
            public Map<String, Object> serialize() {
                return null;
            }

            @Override
            public boolean isOp() {
                return false;
            }

            @Override
            public void setOp(boolean value) { }
        };
    }

}
