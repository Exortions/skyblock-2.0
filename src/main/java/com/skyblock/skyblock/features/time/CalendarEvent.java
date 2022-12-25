package com.skyblock.skyblock.features.time;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public class CalendarEvent {

    public enum EventType {
        TRAVELING_ZOO,
        SPOOKY_FESTIVAL,
        SEASON_OF_JERRY
    }

    private EventType type;
    private Timestamp time;

    public String getDisplayName() {
        return getDisplayItem().getItemMeta().getDisplayName();
    }

    public String getIn() {
        return Util.formatTime(time.getTime() - System.currentTimeMillis());
    }

    public ItemStack getDisplayItem() {
        List<String> lore = Arrays.asList(ChatColor.GRAY + "Starts in: " + ChatColor.YELLOW + Util.formatTime(time.getTime() - System.currentTimeMillis()), ChatColor.GRAY + "Event lasts for: " + ChatColor.YELLOW + "01h 00m 00s" + ChatColor.GRAY + "!", " ");

        switch (type) {
            case TRAVELING_ZOO:
                return Util.idToSkull(new ItemBuilder(ChatColor.GREEN + "Traveling Zoo", Material.SKULL_ITEM, (byte) SkullType.PLAYER.ordinal()).addLore(lore).addLore("&7Oringo the Traveling Zookeeper", "&7is visiting SkyBlock with pets", "&7to trade!").toItemStack(), "ewogICJ0aW1lc3RhbXAiIDogMTU4ODU4NDM4MTU0OSwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ5ZmU1OTVjNmEwOGFkZWM4YjlkYWIwOTg2ODUzMjcxYzZiODdkODk3ZDczMThiMWJhZGFkMmMzNGJkNWEwZSIKICAgIH0KICB9Cn0=");
            case SPOOKY_FESTIVAL:
                return new ItemBuilder(ChatColor.GOLD + "Spooky Festival", Material.JACK_O_LANTERN  ).addLore(lore).addLore("&7Autumn is in full swing and the", "&7air is full of fright. Mob drops", "&7have a chance to contain Candy,", "&7which can be traded with the", "&7Fear Mongerer for rare items!").toItemStack();
            case SEASON_OF_JERRY:
                return new ItemBuilder(ChatColor.RED + "Season of Jerry", Material.SNOW_BALL).addLore(lore).addLore("&7The Jerrys are hard at work", "&7trying to craft enough Gifts for", "&7all of SkyBlock, but an army of", "&7enemies are on the attack! Help", "&7protect Jerry's Workshop so that", "&7everyone can go home with Gifts!").toItemStack();
        }

        return new ItemBuilder().toItemStack();
    }
}
