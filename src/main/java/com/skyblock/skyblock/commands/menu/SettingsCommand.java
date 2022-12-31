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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@RequiresPlayer
@Usage(usage = "/sb settings")
@Description(description = "Opens the Settings Menu")
public class SettingsCommand implements Command, TrueAlias<SettingsCommand> {
    public static final String MENU_NAME = "Settings";
    public static int menuWorkaround = 0;
    public static ArrayList<String> menuHistory = new ArrayList<>(Arrays.asList("main"));

    private static final class SettingsCommandListener implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer((Player) e.getWhoClicked());
            if (e.getClickedInventory() == null
                    || !Objects.equals(e.getClickedInventory().getTitle(), MENU_NAME)
                    || e.getCurrentItem() == null) return;
            e.setCancelled(true);
            NBTItem i = new NBTItem(e.getCurrentItem());
            boolean didSomething = false;
            if (i.hasKey("toggle")) {
                ++menuWorkaround;
                if (menuWorkaround == 2) {
                    menuWorkaround = 0;
                    String path = i.getString("toggle");
                    skyblockPlayer.setValue(path, !((boolean) skyblockPlayer.getValue(path)));
                    didSomething = true;
                }
            } else if (i.hasKey("menu")) {
                menuHistory.add(i.getString("menu"));
                didSomething = true;
            } else if (i.hasKey("back")) {
                menuHistory.remove(menuHistory.size() - 1);
                didSomething = true;
            }

            if (i.hasKey("close")) {
                skyblockPlayer.getBukkitPlayer().closeInventory();
                didSomething = true;
            } else {
                skyblockPlayer.getBukkitPlayer().openInventory(makeMenu(skyblockPlayer, menuHistory.get(menuHistory.size() - 1)));
            }

            if (didSomething && (boolean) skyblockPlayer.getValue("settings.menuSounds")) {
                skyblockPlayer.getBukkitPlayer().playSound(skyblockPlayer.getBukkitPlayer().getLocation(), Sound.CLICK, 1, 1);
            }

        }
    }

    public SettingsCommand() {
        Bukkit.getPluginManager().registerEvents(new SettingsCommandListener(), Skyblock.getPlugin());
    }

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
        Inventory inventory = Bukkit.createInventory(null, 54, MENU_NAME);
        Util.fillEmpty(inventory);
        inventory.setItem(49, Util.buildCloseButton());
        if (menuHistory.size() > 1) {
            ItemBuilder icon = new ItemBuilder(Material.ARROW).setDisplayName(ChatColor.GREEN + "Go Back");
            ItemStack stack = icon.toItemStack();
            NBTItem nbt = new NBTItem(stack);
            nbt.setBoolean("back", true);
            inventory.setItem(48, nbt.getItem());
        }

        switch (name) {
            case "main":
                makeMenuItem(inventory, new ItemStack(Material.SKULL_ITEM), ChatColor.GREEN + "Personal",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "General settings related to your",
                                ChatColor.GRAY + "experience.",
                                "",
                                ChatColor.GRAY + "Includes:",
                                ChatColor.GRAY + "nothing",
                                "",
                                ChatColor.YELLOW + "Click for settings!")),
                        11, "personal");

                makeMenuItem(inventory, new ItemStack(Material.SIGN), ChatColor.GREEN + "Comms",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Tweak notifications and invites",
                                ChatColor.GRAY + "you get from other players.",
                                "",
                                ChatColor.GRAY + "Includes:",
                                ChatColor.GRAY + "nothing",
                                "",
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
                break;

            case "personal":
                makeMenuItem(inventory, new ItemStack(Material.JUKEBOX), ChatColor.GREEN + "Sounds",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Edit details about sounds",
                                ChatColor.GRAY + "that you hear.",
                                "",
                                ChatColor.GRAY + "Includes:",
                                ChatColor.GRAY + "nothing",
                                "",
                                ChatColor.YELLOW + "Click for settings!")),
                        11, "sounds");

                makeMenuItem(inventory, new ItemStack(Material.GLASS), ChatColor.GREEN + "User Interface",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Tweak how information is",
                                ChatColor.GRAY + "displayed to you.",
                                "",
                                ChatColor.GRAY + "Includes:",
                                ChatColor.GRAY + "nothing",
                                "",
                                ChatColor.YELLOW + "Click for settings!")),
                        13, "ui");

                makeMenuItem(inventory, new ItemStack(Material.BOOK_AND_QUILL), ChatColor.GREEN + "Chat Feedback",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Tweak feedback messages you get",
                                ChatColor.GRAY + "from certain items.",
                                "",
                                ChatColor.GRAY + "Includes:",
                                ChatColor.GRAY + "nothing",
                                "",
                                ChatColor.YELLOW + "Click for settings!")),
                        15, "chatFeedback");

                makeToggleItem(inventory, player, new ItemStack(Material.GOLD_INGOT), ChatColor.GREEN + "Player Trading",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Receive trade requests from",
                                ChatColor.GRAY + "other players.")),
                        29, "settings.tradeRequests");

                makeToggleItem(inventory, player, new ItemStack(Material.CHEST), ChatColor.GREEN + "Inventory Full Notifications",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Get a notification when your",
                                ChatColor.GRAY + "inventory is full.")),
                        31, "settings.inventoryFullNotif");

                makeToggleItem(inventory, player, new ItemStack(Material.ARROW), ChatColor.GREEN + "Pickup Arrows with Full Quiver",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Pickup arrows when your quiver",
                                ChatColor.GRAY + "is full.")),
                        33, "settings.arrowPickupFullQuiver");
                break;

            case "sounds":
                makeToggleItem(inventory, player, new ItemStack(Material.GREEN_RECORD), ChatColor.GREEN + "Ability Cooldown Sounds",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Hear a sound when an ability",
                                ChatColor.GRAY + "gets available again.")), //had to create this myself: check - hypixel fucked up
                        20, "settings.abilityCooldownSounds");

                makeToggleItem(inventory, player, new ItemStack(Material.NOTE_BLOCK), ChatColor.GREEN + "Rare Drop Sounds",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Play a jingle when you receive a",
                                ChatColor.GRAY + "rare drop from a monster.")),
                        22, "settings.rareDropSounds");

                makeToggleItem(inventory, player, new ItemStack(Material.SIGN), ChatColor.GREEN + "Menu Sounds",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Play sound effects when opening",
                                ChatColor.GRAY + "menus.")),
                        24, "settings.menuSounds");
                break;
            case "ui":
                makeToggleItem(inventory, player, new ItemStack(Material.EMPTY_MAP), ChatColor.GREEN + "Skill Numerals",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Use Roam Numberals for skill",
                                ChatColor.GRAY + "levels. For example I, II, IV...")), //had to create this myself: check - hypixel fucked up
                        20, "settings.romanSkillNumerals");

                makeToggleItem(inventory, player, new ItemStack(Material.REDSTONE), ChatColor.GREEN + "Dynamic Slayer Sidebar",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "View the required number of",
                                ChatColor.GRAY + "kills based on recent mob kills",
                                ChatColor.GRAY + "in the sidebar during a slayer",
                                ChatColor.GRAY + "quest.")
                        ),
                        22, "settings.dynamicSlayerSidebar");

                makeToggleItem(inventory, player, new ItemStack(Material.SIGN), ChatColor.GREEN + "Zones in Action Bar",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Wether zone names should be",
                                ChatColor.GRAY + "displayed in the Action Bar upon",
                                ChatColor.GRAY + "entering.")),
                        24, "settings.zonesActionBar");
                break;
            case "chatFeedback":
                makeToggleItem(inventory, player, new ItemStack(Material.BLAZE_ROD), ChatColor.GREEN + "Ability Cooldown Chat",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "View the message from ability",
                                ChatColor.GRAY + "cooldowns.")),
                        19, "settings.abilityCooldownChat");

                makeToggleItem(inventory, player, new ItemStack(Material.FISHING_ROD), ChatColor.GREEN + "Sea Creature Chat",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "View the message from catching",
                                ChatColor.GRAY + "Sea Creatures.")),
                        21, "settings.seaCreatureChat");

                makeToggleItem(inventory, player, new ItemStack(Material.BEACON), ChatColor.GREEN + "Ability Chat",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "View the message from ability",
                                ChatColor.GRAY + "damage dealt (certain items",
                                ChatColor.GRAY + "only).")),
                        23, "settings.abilityChat");

                makeToggleItem(inventory, player, new ItemStack(Material.STONE), ChatColor.GREEN + "Compact Chat",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "View the compact message when",
                                ChatColor.GRAY + "mining.")),
                        25, "settings.compactChat");
                break;
            case "comms":
                makeToggleItem(inventory, player, new ItemStack(Material.SIGN), ChatColor.GREEN + "Death Messages",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Show death messages when other",
                                ChatColor.GRAY + "players die.")),
                        11, "settings.deathMessages");

                makeToggleItem(inventory, player, new ItemStack(Material.EMERALD), ChatColor.GREEN + "Guesting Invites",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Receive guesting invitations",
                                ChatColor.GRAY + "from other players.")),
                        14, "settings.guestingInvites");

                makeToggleItem(inventory, player, new ItemStack(Material.DIAMOND), ChatColor.GREEN + "Co-op Invites",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Receive co-op invitations from",
                                ChatColor.GRAY + "other players.")),
                        16, "settings.coopInvites");

                makeToggleItem(inventory, player, new ItemStack(Material.GOLD_BARDING), ChatColor.GREEN + "Bid Notifications",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Receive a message when someone",
                                ChatColor.GRAY + "binds on your auctions.")),
                        28, "settings.bidNotif");

                makeToggleItem(inventory, player, new ItemStack(Material.GOLD_INGOT), ChatColor.GREEN + "Outbid Notifications",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Receive a message when you get",
                                ChatColor.GRAY + "outbid on an auction.")),
                        29, "settings.outbidNotif");

                makeToggleItem(inventory, player, new ItemStack(Material.HOPPER), ChatColor.GREEN + "Fill Notifications",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Receive a message when one of",
                                ChatColor.GRAY + "your orders gets filled in the.",
                                ChatColor.GRAY + "bazaar.")),
                        30, "settings.bazaarFillNotif");

                makeToggleItem(inventory, player, new ItemStack(Material.PAPER), ChatColor.GREEN + "Guesting Notifications",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Receive messages about players",
                                ChatColor.GRAY + "guesting on your island.")),
                        32, "settings.guestingNotif");

                makeToggleItem(inventory, player, new ItemStack(Material.PAPER), ChatColor.GREEN + "Co-op Travel Notifications",
                        new ArrayList<>(Arrays.asList(ChatColor.GRAY + "When playing co-op, receive",
                                ChatColor.GRAY + "notifications on all your",
                                ChatColor.GRAY + "parners' travels")),
                        34, "settings.coopTravelNotif");
                break;
            default:
                break;
        }
        return inventory;
    }

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        player.openInventory(makeMenu(skyblockPlayer, "main"));
    }
}
