package com.skyblock.skyblock.features.bags;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

@Data
public class Bag implements Listener {

    private final String id;
    private final String name;

    private final String headValue;
    private final String lore;

    private final int skyblockMenuSlot;

    private final BiConsumer<SkyblockPlayer, Inventory> onOpen;
    private final ToIntFunction<SkyblockPlayer> getSlots;
    private final Predicate<ItemStack> validate;
    private final BiConsumer<SkyblockPlayer, ItemStack> onPutItem;
    private final BiConsumer<SkyblockPlayer, ItemStack> onRemoveItem;

    public Bag(String id, String name, String headValue, String lore, int skyblockMenuSlot, Predicate<ItemStack> validate, BiConsumer<SkyblockPlayer, Inventory> onOpen, BiConsumer<SkyblockPlayer, ItemStack> onPutItem, BiConsumer<SkyblockPlayer, ItemStack> onRemoveItem) {
        this.skyblockMenuSlot = skyblockMenuSlot;
        this.onRemoveItem = onRemoveItem;
        this.onPutItem = onPutItem;
        this.headValue = headValue;
        this.validate = validate;
        this.onOpen = onOpen;
        this.lore = lore;
        this.name = name;
        this.id = id;

        this.getSlots = (player) -> (int) player.getValue("bag." + id + ".slots");

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin(Skyblock.class));
    }

    public List<ItemStack> getContents(Player player) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        int limit = this.getSlots.applyAsInt(skyblockPlayer);

        List<ItemStack> contents = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            ItemStack item = (ItemStack) skyblockPlayer.getValue("bag." + this.id + ".items." + i);

            if (item == null || item.getType() == Material.AIR) continue;

            if (this.validate.test(item)) contents.add(item);
        }

        return contents;
    }

    public Inventory show(Player player) {
        int limit = this.getSlots.applyAsInt(SkyblockPlayer.getPlayer(player));

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        int inventorySize = this.getSlots.applyAsInt(SkyblockPlayer.getPlayer(player)) + 9;

        while (inventorySize % 9 != 0) {
            inventorySize++;
        }

        Inventory inventory = Bukkit.createInventory(null, inventorySize, this.name);

        Util.fillEmpty(inventory);

        for (int i = 0; i < limit; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }

        inventory.setItem(inventorySize - 4, Util.buildCloseButton());
        inventory.setItem(inventorySize - 5, Util.buildBackButton());

        for (int i = 0; i < limit; i++) {
            ItemStack item = (ItemStack) skyblockPlayer.getValue("bag." + this.id + ".items." + i);

            inventory.setItem(i, item);
        }

        this.onOpen.accept(skyblockPlayer, inventory);

        return inventory;
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", this.headValue));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");

            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        meta.setDisplayName(ChatColor.GREEN + this.name);
        meta.setLore(Arrays.asList(Util.buildLore(this.lore + "\n\n&eClick to open!")));

        stack.setItemMeta(meta);

        NBTItem item = new NBTItem(stack);

        item.setBoolean("isBag", true);
        item.setString("bagId", this.id);

        return item.getItem();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        if (!event.getClickedInventory().getName().equals(this.name)) return;

        int limit = this.getSlots.applyAsInt(SkyblockPlayer.getPlayer((Player) event.getWhoClicked()));

        event.setCancelled(event.getSlot() >= limit);

        if (event.getSlot() < limit) {
            if (event.getCurrentItem() == null) return;

            if (!this.validate.test(event.getCursor()) && event.getAction() != InventoryAction.PICKUP_ONE && event.getAction() != InventoryAction.PICKUP_ALL) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "You cannot put this item in this bag!");
                event.setCancelled(true);
                return;
            }

            Util.delay(() -> {
                HashMap<Integer, ItemStack> items = new HashMap<>();

                for (int i = 0; i < limit; i++) items.put(i, event.getClickedInventory().getItem(i));

                SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(((Player) event.getWhoClicked()).getPlayer());

                if (Util.notNull(event.getCursor())) {
                    onPutItem.accept(skyblockPlayer, event.getCursor());
                } else {
                    onRemoveItem.accept(skyblockPlayer, event.getCurrentItem());
                }

                for (int slot : items.keySet()) {
                    if (items.get(slot) == null) {
                        skyblockPlayer.setValue("bag." + this.id + ".items." + slot, new ItemStack(Material.AIR));
                        continue;
                    }

                    skyblockPlayer.setValue("bag." + this.id + ".items." + slot, items.get(slot));
                }
            }, 1);
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        NBTItem item = new NBTItem(event.getCurrentItem());

        if (item.getBoolean("close")) event.getWhoClicked().closeInventory();
        if (item.getBoolean("back")) ((Player) event.getWhoClicked()).performCommand("sb menu");
    }

}
