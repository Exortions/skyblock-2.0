package com.skyblock.skyblock.utilities;

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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

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
        player.getBukkitPlayer().sendMessage("");
    }

    public void setDamageIndicator(final Location loc, final String displayname) {
        double randomX = Math.random();
        double randomY = Math.random();
        double randomZ = Math.random();
        randomX -= 0.5;
        randomY += 0.25;
        randomZ -= 0.5;

        ArmorStand as = (ArmorStand)loc.getWorld().spawnEntity(loc.add(randomX, randomY, randomZ), EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);

        DecimalFormat formatter = new DecimalFormat("#,###");
        formatter.setGroupingUsed(true);

        String noColor = ChatColor.stripColor(displayname);
        String formatted = formatter.format(Integer.parseInt(noColor));

        as.setCustomName(displayname.replaceAll(noColor, formatted));

        final NBTEntity nbtas = new NBTEntity(as);
        nbtas.setBoolean("Invisible", true);
        nbtas.setBoolean("Gravity", false);
        nbtas.setBoolean("CustomNameVisible", true);
        nbtas.setBoolean("Marker", true);
        nbtas.setBoolean("Invulnerable", true);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Skyblock.getPlugin(Skyblock.class), () -> {
            if (!as.isDead()) {
                as.remove();
            }
        }, 20L);
    }

    public String addCritTexture(String str) {
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
}
