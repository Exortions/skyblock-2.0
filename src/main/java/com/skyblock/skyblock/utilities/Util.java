package com.skyblock.skyblock.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.Reforge;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import lombok.experimental.UtilityClass;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class Util {

    public List<String> listOf(String... strings) {
        return Arrays.asList(strings);
    }

    private final static TreeMap<Integer, String> romanMap = new TreeMap<Integer, String>(){{
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
        for (int i = 0; i < gui.getSlots(); i++) gui.addItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 15).toItemStack());
    }

    public void fillEmpty(Inventory inventory, Material material, int data) {
        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
    }

    public void fillBorder(Inventory inventory) {
        fillBorder(inventory, Material.STAINED_GLASS_PANE, 15);
    }

    public void fillBorder(Inventory inventory, Material material, int data) {
        for (int i = 0; i < 9; i++) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 45; i < 54; i++) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 9; i < 45; i += 9) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 17; i < 45; i += 9) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
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

    public void fillSides(Inventory inventory) {
        fillSides(inventory, Material.STAINED_GLASS_PANE, 15);
    }

    public void fillSides(Inventory inventory, Material material, int data) {
        for (int i = 9; i < 45; i += 9) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 17; i < 45; i += 9) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());

        inventory.setItem(0, new ItemBuilder(" ", material, (short) data).toItemStack());
        inventory.setItem(8, new ItemBuilder(" ", material, (short) data).toItemStack());
        inventory.setItem(45, new ItemBuilder(" ", material, (short) data).toItemStack());
        inventory.setItem(53, new ItemBuilder(" ", material, (short) data).toItemStack());
    }

    public void fillBottom(Inventory inventory, Material material, int data) {
        for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
    }

    public void fillSides45Slots(Inventory inventory, Material material, int data) {
        for (int i = 9; i < 36; i += 9) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());
        for (int i = 17; i < 36; i += 9) inventory.setItem(i, new ItemBuilder(" ", material, (short) data).toItemStack());

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

    private final static String EMPTY = "PLACEHOLDER STRING";

    public ItemStack getEmptyItemBase() {
        return new ItemBase(Material.DIRT, EMPTY, Reforge.NONE, 1, listOf(EMPTY, EMPTY), Collections.emptyList(), false, false, EMPTY, listOf(EMPTY, EMPTY), EMPTY, 0, "0s", EMPTY, EMPTY, 0, 0, 0, 0, 0, 0, 0, 0, 0, true).getStack();
    }

    public void sendAbility(SkyblockPlayer player, String abilityName, int mana) {
        player.subtractMana(mana);
        player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "Used " + ChatColor.GOLD + abilityName + ChatColor.GREEN +
                "! " + ChatColor.AQUA + "(" + mana + " Mana)");
    }

    public void setDamageIndicator(final Location loc, final String displayname, boolean format) {
        double randomX = Math.random();
        double randomY = Math.random();
        double randomZ = Math.random();
        randomX -= 0.5;
        randomY += 0.25;
        randomZ -= 0.5;

        final ArmorStand as = (ArmorStand)loc.getWorld().spawnEntity(loc.add(randomX, randomY, randomZ), EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);

        if (format) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            formatter.setGroupingUsed(true);

            String noColor = ChatColor.stripColor(displayname);
            String formatted = formatter.format(Integer.parseInt(noColor));

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

    public ItemStack IDtoSkull(ItemStack head, String id) {
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
        return getColorBasedOnSize((end - start), 1, 2, 3) + "" + (end - start) + "ms";
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

        if (!meta.getLore().get(meta.getLore().size() - 1).contains("Right-Click for more trading options!")) return stack;

        List<String> lore = meta.getLore();
        for (int i = 1; i < 7; i++) lore.remove(lore.size() - 1);

        meta.setLore(lore);

        stack.setItemMeta(meta);
        return stack;
    }

    public String abbreviate(int num) {
        if (num < 1000) return num + "";
        int exp = (int) (Math.log(num) / Math.log(1000));
        return String.format("%.1f%c", num / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1)).replaceAll("\\.0", "");
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

    public int assertPositive(int num) {
        return num < 0 ? num * -1 : num;
    }

    public List<Object> spawnSkyblockNpc(Location location, String name, String skinValue, String skinSignature, boolean skin, boolean look, boolean villager, Villager.Profession profession) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(villager ? EntityType.VILLAGER : EntityType.PLAYER, "");

        npc.spawn(location);

        npc.getEntity().setCustomNameVisible(false);

        npc.getEntity().setMetadata("merchant", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), true));
        npc.getEntity().setMetadata("merchantName", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), name));

        if (villager) ((Villager) npc.getEntity()).setProfession(profession);

        ArmorStand stand = npc.getEntity().getWorld().spawn(npc.getEntity().getLocation().add(0, !villager ? 1.95 : 2.15, 0), ArmorStand.class);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(name);

        NBTEntity nbtas = new NBTEntity(stand);
        nbtas.setBoolean("Invisible", true);
        nbtas.setBoolean("Gravity", false);
        nbtas.setBoolean("CustomNameVisible", true);
        nbtas.setBoolean("Marker", true);
        nbtas.setBoolean("Invulnerable", true);

        stand.teleport(npc.getEntity().getLocation().add(0, !villager ? 1.95 : 2.15, 0));
        stand.setMetadata("merchant", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), true));
        stand.setMetadata("merchantName", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), name));
        stand.setMetadata("NPC", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), true));

        ArmorStand click = npc.getEntity().getWorld().spawn(npc.getEntity().getLocation().add(0, !villager ? 1.7 : 1.9, 0), ArmorStand.class);
        click.setCustomName(ChatColor.YELLOW + "" + ChatColor.BOLD + "CLICK");
        click.setGravity(false);
        click.setVisible(false);
        click.setCustomNameVisible(true);

        NBTEntity nbtEntity = new NBTEntity(click);
        nbtEntity.setBoolean("Invisible", true);
        nbtEntity.setBoolean("Gravity", false);
        nbtEntity.setBoolean("CustomNameVisible", true);
        nbtEntity.setBoolean("Marker", true);
        nbtEntity.setBoolean("Invulnerable", true);

        click.teleport(npc.getEntity().getLocation().add(0, !villager ? 1.9 : 2, 0));

        if (skin) {
            SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
            skinTrait.setSkinPersistent("banker", skinSignature, skinValue);

            npc.addTrait(skinTrait);
        }

        npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);

        if (look) {
            LookClose lookClose = npc.getOrAddTrait(LookClose.class);
            lookClose.lookClose(true);

            npc.addTrait(lookClose);
        }

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

    private static final NavigableMap<Long, String> suffixes = new TreeMap<> ();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value);

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public void delay(Runnable run, int ticks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                run.run();
            }
        }.runTaskLater(Skyblock.getPlugin(Skyblock.class), ticks);
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

    public String formatTime(long millis) {
        return String.format("%02dm%02ds", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public ItemStack colorLeatherArmor(ItemStack stack, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        stack.setItemMeta(meta);

        return stack;
    }

}
