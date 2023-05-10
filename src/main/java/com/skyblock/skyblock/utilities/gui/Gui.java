package com.skyblock.skyblock.utilities.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.auction.gui.AuctionHouseGUI;
import com.skyblock.skyblock.features.bazaar.gui.BazaarCategoryGui;
import com.skyblock.skyblock.features.crafting.gui.RecipeBookGUI;
import com.skyblock.skyblock.features.time.gui.CalendarEventsGUI;
import com.skyblock.skyblock.utilities.Util;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Getter
public class Gui implements Listener {

    private static final HashMap<Gui, Boolean> REGISTERED_LISTENERS = new HashMap<>();

    public final HashMap<ItemStack, Runnable> specificClickEvents;
    public final HashMap<String, Runnable> clickEvents;
    public final HashMap<Integer, ItemStack> items;
    public final HashMap<Player, Boolean> opened;
    public final List<ItemStack> addableItems;
    public final int slots;
    public String name;

    public Gui(String name, int slots, HashMap<String, Runnable> clickEvents) {
        this(name, slots, clickEvents, new HashMap<>());
    }

    private static class AbstractCommandGui extends Gui {
        private final String command;

        @SuppressWarnings("unused")
        public AbstractCommandGui(String command, Player opener) {
            super("Command", 9, new HashMap<>());

            this.command = command;
        }
    }

    private static final class SkyblockMenuAbstractGui extends AbstractCommandGui {
        public SkyblockMenuAbstractGui(Player opener) {
            super("sb menu", opener);
        }
    }

    private static final HashMap<String, Class<? extends Gui>> BACK_BUTTONS = new HashMap<String, Class<? extends Gui>>() {{
        put("To Auction House", AuctionHouseGUI.class);
        put("To Bazaar", BazaarCategoryGui.class);
        put("To SkyBlock Menu", SkyblockMenuAbstractGui.class);
        put("To Recipe Book", RecipeBookGUI.class);
        put("To Calendar and Events", CalendarEventsGUI.class);
    }};

    public Gui(String name, int slots, HashMap<String, Runnable> clickEvents, HashMap<ItemStack, Runnable> specificClickEvents) {
        this.name = name;
        this.slots = slots;

        this.clickEvents = clickEvents;
        this.specificClickEvents = specificClickEvents;

        this.items = new HashMap<>();
        this.addableItems = new ArrayList<>();
        this.opened = new HashMap<>();
    }

    public void show(Player player) {
        if (this instanceof AbstractCommandGui) {
            player.performCommand(((AbstractCommandGui) this).command);
            return;
        }

        Inventory inventory = player.getServer().createInventory(null, slots, name);

        for (int i = 0; i < slots; i++) {
            if (items.containsKey(i)) {
                if (Objects.equals(this.getName(), "Skyblock Menu") && items.get(i).getType().equals(Material.SKULL_ITEM) && items.get(i).getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Your SkyBlock Profile")) {
                    ItemStack stack = items.get(i);

                    SkullMeta meta = (SkullMeta) stack.getItemMeta();

                    meta.setOwner(player.getName());

                    stack.setItemMeta(meta);

                    inventory.setItem(i, stack);

                    continue;
                }

                inventory.setItem(i, items.get(i));
            }
        }

        for (ItemStack stack : this.addableItems) inventory.addItem(stack);

        player.openInventory(inventory);

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin());

        opened.put(player, true);
    }

    public void hide(Player player) {
        player.closeInventory();
    }

    public void addItem(int slot, ItemStack stack) {
        this.items.put(slot, stack);
    }

    public void addItem(ItemStack stack) {
        this.addableItems.add(stack);
    }

    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    protected boolean getSpecificClickSound() { return true; }

    public void fillEmpty(ItemStack stack) {
        for (int i = 0; i < this.slots; i++) {
            if (!this.items.containsKey(i)) this.items.put(i, stack);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equals(name) && opened.containsKey((Player) event.getWhoClicked())) {
            event.setCancelled(true);
            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer((Player) event.getWhoClicked());

            onInventoryClick(event);

            if (event.getCurrentItem() == null) return;
            if (!event.getCurrentItem().hasItemMeta()) return;
            if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(
                    Util.buildCloseButton().getItemMeta().getDisplayName())) {
                event.getWhoClicked().closeInventory();
                return;
            }

            List<String> lore = event.getCurrentItem().getItemMeta().getLore();

            if (lore != null && lore.size() > 0 && BACK_BUTTONS.containsKey(ChatColor.stripColor(lore.get(0)))) {
                Class<? extends Gui> clazz = BACK_BUTTONS.get(ChatColor.stripColor(lore.get(0)));

                try {
                    clazz.getConstructor(Player.class).newInstance(event.getWhoClicked()).show((Player) event.getWhoClicked());
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                return;
            }

            if (specificClickEvents.containsKey(event.getCurrentItem())) {
                specificClickEvents.get(event.getCurrentItem()).run();
                if (skyblockPlayer.getBoolValue("settings.menuSounds") && getSpecificClickSound()) {
                    skyblockPlayer.getBukkitPlayer().playSound(skyblockPlayer.getBukkitPlayer().getLocation(), Sound.CLICK, 1, 1);
                }
                return;
            }

            if (clickEvents.containsKey(event.getCurrentItem().getItemMeta().getDisplayName())) {
                clickEvents.get(event.getCurrentItem().getItemMeta().getDisplayName()).run();
                if (skyblockPlayer.getBoolValue("settings.menuSounds")) {
                    skyblockPlayer.getBukkitPlayer().playSound(skyblockPlayer.getBukkitPlayer().getLocation(), Sound.CLICK, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getName().equals(name) && opened.containsKey((Player) e.getPlayer())) {
            onClose((Player) e.getPlayer());

            HandlerList.unregisterAll(this);
            opened.remove((Player) e.getPlayer());
        }
    }

    public void onClose(Player p) { }
    public void onInventoryClick(InventoryClickEvent e) { }
}
