package com.skyblock.skyblock.features.time.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.time.SkyblockTimeManager;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MonthCalendarGUI extends Gui {

    private static final HashMap<String, Integer> pages = new HashMap<String, Integer>() {{
        put("spring", 1);
        put("summer", 2);
        put("fall", 3);
        put("winter", 4);
    }};

    public MonthCalendarGUI(String month, int year, Player player) {
        super(WordUtils.capitalize(month), 54, new HashMap<>());

        Util.fillBorder(this);

        int page = pages.get(month);

        for (int i = 1; i < 8; i++) getItems().remove(i);

        addItem(49, Util.buildCloseButton());
        addItem(48, Util.buildBackButton("&7To Calendar and Events"));

        SkyblockTimeManager timeManager = Skyblock.getPlugin().getTimeManager();

        if (page != 4) addItem(53, new ItemBuilder(ChatColor.GREEN + "Next Page", Material.ARROW).addLore(ChatColor.YELLOW + "Page " + timeManager.getNextSeason(month)).toItemStack());
        if (page != 1) addItem(45, new ItemBuilder(ChatColor.GREEN + "Previous Page", Material.ARROW).addLore(ChatColor.YELLOW + "Page " + timeManager.getLastSeason(month)).toItemStack());

        getClickEvents().put(ChatColor.GREEN + "Next Page", () -> {
            new MonthCalendarGUI(timeManager.getNextSeason(month), year, player).show(player);
        });

        getClickEvents().put(ChatColor.GREEN + "Previous Page", () -> {
            new MonthCalendarGUI(timeManager.getLastSeason(month), year, player).show(player);
        });

        int i = 0;
        for (ItemStack item : getEvents(month)) {
            while (getItems().containsKey(i)) i++;

            addItem(i, item);
        }
    }

    public List<ItemStack> getEvents(String season) {
        List<ItemStack> items = new ArrayList<>();

        for (int day = 1; day < 32; day++) {
            ItemBuilder item = new ItemBuilder(ChatColor.GREEN + "Day " + day, Material.PAPER);
            item.setAmount(day);

            // Dark Auction
            if (day % 3 == 0) {
                item.setMaterial(Material.NETHER_BRICK_ITEM);
                item.addLore("&712:00am - 12:41am: " + ChatColor.DARK_PURPLE + "Dark Auction");
                item.addEnchantmentGlint();
            }

            if (season.equals("fall") && day >= 29) {
                addEvent(item, Material.JACK_O_LANTERN, ChatColor.GOLD + "Spooky Festival", 3);
            } else if (season.equals("winter") && (day == 25 || day == 26)) {
                addEvent(item, Material.SNOW_BALL, ChatColor.RED + "Season of Jerry", 2);
            } else if ((season.equals("summer") && (day == 2 || day == 3)) || (season.equals("winter") && (day == 2 || day == 3))) {
                addEvent(item, Material.SKULL_ITEM, ChatColor.GREEN + "Travelling Zoo", 2);
                item.setDamage((short) SkullType.PLAYER.ordinal());
                item.setSkullID("ewogICJ0aW1lc3RhbXAiIDogMTU4ODU4NDM4MTU0OSwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ5ZmU1OTVjNmEwOGFkZWM4YjlkYWIwOTg2ODUzMjcxYzZiODdkODk3ZDczMThiMWJhZGFkMmMzNGJkNWEwZSIKICAgIH0KICB9Cn0=");
            } else if (season.equals("winter") && (day == 31)) {
                item.setMaterial(Material.CAKE);
                item.addLore("&7All day: " + ChatColor.LIGHT_PURPLE + "New Year Cake Celebration &7(" + ChatColor.YELLOW + 2 + "d&7)");
                item.addEnchantmentGlint();
            } else if (season.equals("winter") && (day == 1)) {
                item.setMaterial(Material.SNOW_BLOCK);
                item.addLore("&712:00am: " + ChatColor.WHITE + "Jerry's Workshop Opens &7(" + ChatColor.YELLOW + 3 + "d&7)");
                item.addEnchantmentGlint();
            }

            if (item.toItemStack().getType().equals(Material.PAPER)) item.addLore("&7No events");

            items.add(item.toItemStack());
        }

        return items;
    }

    private void addEvent(ItemBuilder item, Material material, String name, int days) {
        item.setMaterial(material);
        item.addEnchantmentGlint();
        item.addLore("&7All Day: " + name + " &7(" + ChatColor.YELLOW + days + "&7)");
    }
}
