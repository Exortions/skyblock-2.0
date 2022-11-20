package com.skyblock.skyblock.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.ReforgeType;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;

@UtilityClass
public class Util {

    public List<String> listOf(String... strings) {
        return Arrays.asList(strings);
    }

    private final static TreeMap<Integer, String> romanMap = new TreeMap<>();

    static {

        romanMap.put(1000, "M");
        romanMap.put(900, "CM");
        romanMap.put(500, "D");
        romanMap.put(400, "CD");
        romanMap.put(100, "C");
        romanMap.put(90, "XC");
        romanMap.put(50, "L");
        romanMap.put(40, "XL");
        romanMap.put(10, "X");
        romanMap.put(9, "IX");
        romanMap.put(5, "V");
        romanMap.put(4, "IV");
        romanMap.put(1, "I");

    }

    public String toRoman(int number) {
        int l = romanMap.floorKey(number);
        if (number == l) {
            return romanMap.get(number);
        }
        return romanMap.get(l) + toRoman(number - l);
    }

    public String[] buildLore(String lore) {
        return ChatColor.translateAlternateColorCodes('&', lore).split("\n");
    }

    public List<String> buildLoreList(String lore) {
        return Arrays.asList(ChatColor.translateAlternateColorCodes('&', lore).split("\n"));
    }

    public ItemStack buildCloseButton() {
        return new ItemBuilder(ChatColor.RED + "Close", Material.BARRIER).toItemStack();
    }

    public ItemStack buildBackButton() {
        return new ItemBuilder(ChatColor.GREEN + "Go Back", Material.ARROW).addLore(ChatColor.GRAY + "To SkyBlock Menu").toItemStack();
    }

    public void fillEmpty(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, 1, (short) 15).toItemStack());
    }

    public void fillBorder(Inventory inventory) {
        for (int i = 0; i < 9; i++) inventory.setItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, 1, (short) 15).toItemStack());
        for (int i = 45; i < 54; i++) inventory.setItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, 1, (short) 15).toItemStack());
        for (int i = 9; i < 45; i += 9) inventory.setItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, 1, (short) 15).toItemStack());
        for (int i = 17; i < 45; i += 9) inventory.setItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, 1, (short) 15).toItemStack());
    }

    public boolean notNull(ItemStack item) {
        return item != null && !item.getType().equals(Material.AIR);
    }

    public boolean isSkyblockItem(ItemStack item) {
        if (!notNull(item)) return false;

        return new NBTItem(item).getBoolean("skyblockItem");
    }

    private final static String EMPTY = "PLACEHOLDER STRING";

    public ItemStack getEmptyItemBase() {
        return new ItemBase(Material.DIRT, EMPTY, ReforgeType.NONE, 1, listOf(EMPTY, EMPTY), Collections.emptyList(), false, false, EMPTY, listOf(EMPTY, EMPTY), EMPTY, 0, "0s", EMPTY, EMPTY, 0, 0, 0, 0, 0, 0, 0, 0, 0, true).getStack();
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

    public String commaify(int i) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        formatter.setGroupingUsed(true);
        return formatter.format(i);
    }

    public ItemStack IDtoSkull(ItemStack head, String id) {
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(new String(org.apache.commons.codec.binary.Base64.decodeBase64(id))).getAsJsonObject();
        String skinUrl = o.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = org.apache.commons.codec.binary.Base64.encodeBase64(("{textures:{SKIN:{url:\"" + skinUrl + "\"}}}").getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
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

}
