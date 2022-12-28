package com.skyblock.skyblock.commands.menu;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.TrueAlias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBuilder;

import de.tr7zw.nbtapi.NBTItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@RequiresPlayer
@Usage(usage = "/sb settings")
@Description(description = "Opens the Settings Menu")
public class SettingsCommand implements Command, TrueAlias<SettingsCommand> {
    public static final String MENU_NAME = "Skyblock Settings";

    private static final class ListeningCommandListener implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer((Player) e.getWhoClicked());
            if (e.getClickedInventory() == null
                    || !Objects.equals(e.getClickedInventory().getTitle(), MENU_NAME)
                    || e.getCurrentItem() == null) return;
            e.setCancelled(true);
            NBTItem i = new NBTItem(e.getCurrentItem());
            String newMenu = "main";
            if (i.hasKey("toggle")) {
                String path = i.getString("toggle");

                System.out.println("Toggling " + path);

                skyblockPlayer.setValue(path, !((boolean) skyblockPlayer.getValue(path)));
                newMenu = activeMenu;
            } else if (i.hasKey("menu")) {
                newMenu = i.getString("menu");
            }
            skyblockPlayer.getBukkitPlayer().openInventory(makeMenu(skyblockPlayer, newMenu));
        }
    }

    public SettingsCommand() {
        Bukkit.getPluginManager().registerEvents(new ListeningCommandListener(), Skyblock.getPlugin());
    }

    public static String activeMenu;

    public static void makeToggleItem(Inventory inventory, SkyblockPlayer player, ItemStack item, String name, ArrayList<String> lore, int slot, String option) {
        ItemBuilder icon = new ItemBuilder(item)
                .setDisplayName(name).addLore(lore)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        inventory.setItem(slot, icon.toItemStack());
        ItemBuilder button = new ItemBuilder(new ItemStack(Material.INK_SACK))
                .setDisplayName(name)
                .addLore((boolean) player.getValue(option) ? ChatColor.GRAY + "Click to disable!" : ChatColor.GRAY + "Click to enable!")
                .setDamage((boolean) player.getValue(option) ? 10 : 8);

        ItemStack stack = button.toItemStack();
        NBTItem nbt = new NBTItem(stack);
        nbt.setString("toggle", option);

        inventory.setItem(slot + 9, nbt.getItem());
    }

    public static void makeMenuItem(Inventory inventory, ItemStack item, String name, ArrayList<String> lore, int slot, String menu) {
        ItemBuilder icon = new ItemBuilder(item)
                .setDisplayName(name).addLore(lore)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        ItemStack stack = icon.toItemStack();
        NBTItem nbt = new NBTItem(stack);
        nbt.setString("menu", menu);

        inventory.setItem(slot, nbt.getItem());
    }

    public static Inventory makeMenu(SkyblockPlayer player, String name) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Skyblock Settings");
        Util.fillEmpty(inventory);
        inventory.setItem(49, Util.buildCloseButton());

        activeMenu = name;

        if ("main".equals(name)) {
            makeMenuItem(inventory, new ItemStack(Material.SKULL_ITEM), ChatColor.GREEN + "Personal",
                    new ArrayList<>(Arrays.asList(ChatColor.GRAY + "General settings related to your",
                            ChatColor.GRAY + "experience.",
                            "",
                            ChatColor.GRAY + "Includes:",
                            ChatColor.GRAY + "nothing",
                            ChatColor.YELLOW + "Click for settings!")),
                    11, "personal");

            makeMenuItem(inventory, new ItemStack(Material.SIGN), ChatColor.GREEN + "Comms",
                    new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Tweak notifications and invites",
                            ChatColor.GRAY + "you get from other players.",
                            "",
                            ChatColor.GRAY + "Includes:",
                            ChatColor.GRAY + "nothing",
                            ChatColor.YELLOW + "Click for settings!")),
                    13, "comms");

            makeMenuItem(inventory, new ItemStack(Material.DIODE), ChatColor.GREEN + "Island Settings",
                    new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Edit Skyblock settings regarding",
                            ChatColor.GRAY + "your island.",
                            "",
                            ChatColor.RED + "Warp to your island to edit!")),
                    15, "island");

            makeToggleItem(inventory, player, new ItemStack(Material.DIAMOND_SWORD), ChatColor.GREEN + "Double Tap to Drop",
                    new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Double tap the drop button to",
                            ChatColor.GRAY + "drop certain items.")),
                    30, "settings.doubleTapDrop");

            makeToggleItem(inventory, player, new ItemStack(Material.SKULL_ITEM), ChatColor.GREEN + "Profile Viewer",
                    new ArrayList<>(Arrays.asList(ChatColor.GRAY + "View Player profiles on",
                            ChatColor.GRAY + "right-click")),
                    32, "settings.rightClickProfiles");
        }
        return inventory;
    }

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        player.openInventory(makeMenu(skyblockPlayer, "main"));
    }
}
