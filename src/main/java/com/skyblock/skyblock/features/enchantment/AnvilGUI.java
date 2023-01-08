package com.skyblock.skyblock.features.enchantment;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.auction.AuctionCategory;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemTool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class AnvilGUI extends CraftInventoryCustom implements Listener {
    public AnvilGUI() {
        super(null, 54, "Anvil");

        Util.fillEmpty(this);

        resetItems();

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin());
    }

    private void resetItems() {
        setItem(29, null);
        setItem(33, null);

        for (int i = 45; i < 54; i++) setItem(i, new ItemBuilder("", Material.STAINED_GLASS_PANE, (short) 14).toItemStack());
        setItem(49, Util.buildCloseButton());

        setItem(11, buildPane(false, false));
        setItem(12, buildPane(false, false));
        setItem(20, buildPane(false, false));

        setItem(14, buildPane(true, false));
        setItem(15, buildPane(true, false));
        setItem(24, buildPane(true, false));

        setItem(13, new ItemBuilder(ChatColor.RED + "Anvil", Material.BARRIER).addLore("&7Place a target item in the left", "&7slot and a sacrifice item in the", "&7right slot to combine them!").toItemStack());
        setItem(22, new ItemBuilder(ChatColor.GREEN + "Combine items", Material.ANVIL).addLore("&7Combine the items in the slots", "&7to the left and right below.").toItemStack());
    }

    private ItemStack buildPane(boolean sacrifice, boolean success) {
        ItemBuilder item = new ItemBuilder(ChatColor.GOLD + "Item to " + (sacrifice ? "Sacrifice" : "Upgrade"), Material.STAINED_GLASS_PANE, (success ? (short) 5 : (short) 14));

        item.addLore("&7The item you want to " + (sacrifice ? "sacrifice" : "upgrade"), "&7should be placed in the slot on", "&7this side.");
        return item.toItemStack();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals(getName())) return;

        HandlerList.unregisterAll(this);
        if (getItem(29) != null) e.getPlayer().getInventory().addItem(getItem(29));
        if (getItem(33) != null) e.getPlayer().getInventory().addItem(getItem(33));
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        if (e.getClickedInventory().equals(e.getWhoClicked().getInventory()) &&
                e.getWhoClicked().getOpenInventory().getTitle().equals(getName())) {
            Util.delay(() -> {
                int slot = 33;
                if (getItem(33) == null) slot = 29;

                Bukkit.getPluginManager().callEvent(new InventoryClickEvent(e.getView(), e.getSlotType(), slot, e.getClick(), e.getAction()));
            }, 1);
        }

        if (!e.getClickedInventory().equals(this)) return;

        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();

        if (e.getSlot() == 22) {
            if (getItem(13).getType().equals(Material.BARRIER)) return;

            player.playSound(player.getLocation(), Sound.ANVIL_USE, 10, 1);

            ItemStack result = getItem(13);
            resetItems();

            setItem(13, result);
            return;
        }

        if (e.getSlot() == 13 && !e.getCurrentItem().getType().equals(Material.BARRIER)) {
            player.getInventory().addItem(e.getCurrentItem());
            resetItems();
            return;
        }

        if (e.getSlot() == 29 || e.getSlot() == 33) {
            e.setCancelled(false);
            Util.delay(() -> {
                boolean sacrifice = e.getSlot() == 33;
                ItemStack item = getItem(e.getSlot());

                if (item == null) {
                    ItemStack other = (sacrifice ? getItem(29) : getItem(33));
                    resetItems();

                    setItem((sacrifice ? 29 : 33), other);
                    Bukkit.getPluginManager().callEvent(new InventoryClickEvent(e.getView(), e.getSlotType(), (sacrifice ? 29 : 33), e.getClick(), e.getAction()));

                    return;
                }

                if (sacrifice) {
                    setItem(14, buildPane(true, true));
                    setItem(15, buildPane(true, true));
                    setItem(24, buildPane(true, true));
                } else {
                    setItem(11, buildPane(false, true));
                    setItem(12, buildPane(false, true));
                    setItem(20, buildPane(false, true));
                }

                if (getItem(29) != null && getItem(33) != null) {
                    for (int i = 45; i < 54; i++) setItem(i, new ItemBuilder("", Material.STAINED_GLASS_PANE, (short) 5).toItemStack());
                    setItem(49, Util.buildCloseButton());
                    setItem(22, new ItemBuilder(ChatColor.GREEN + "Combine items", Material.ANVIL).addLore("&7Combine the items in the slots", "&7to the left and right below.", " ", "&7Cost", ChatColor.DARK_AQUA + "0 Exp Levels", " ", ChatColor.YELLOW + "Click to combine").addEnchantmentGlint().toItemStack());
                    setItem(13, generateCombined());
                }
            }, 1);
        }
    }

    private ItemStack generateCombined() {
        ItemBase upgrade = new ItemBase(getItem(29));
        ItemBase sacrifice = new ItemBase(getItem(33));

        ItemStack up = getItem(29);
        ItemStack sac = getItem(33);

        if (!Util.getSkyblockId(getItem(29)).equals(Util.getSkyblockId(getItem(33))) && !up.getType().equals(Material.ENCHANTED_BOOK) && !sac.getType().equals(Material.ENCHANTED_BOOK)) {
            resetItems();
            setItem(29, up);
            setItem(33, sac);

            return getItem(13);
        }

        ItemBase combined = new ItemBase(getItem(29).clone());

        for (ItemEnchantment enchant : sacrifice.getEnchantments()) {
            String id = enchant.getBaseEnchantment().getName();
            if (combined.getEnchantment(id) == null) {
                combined.setEnchantment(id, enchant.getLevel());
                continue;
            }

            if (upgrade.getEnchantment(id).getLevel() == enchant.getLevel()) {
                combined.setEnchantment(id, enchant.getLevel() + 1);
                continue;
            }

            combined.setEnchantment(id, Math.max(enchant.getLevel(), upgrade.getEnchantment(id).getLevel()));
        }

        return combined.createStack();
    }

    private boolean isEnchantBook(ItemStack item) {
        return item.getItemMeta().getDisplayName().contains("Enchanted Book");
    }

    private boolean isAnvilable(ItemStack item) {
        Item stack = CraftItemStack.asNMSCopy(item).getItem();

        if (item.getAmount() > 1) return false;

        return AuctionCategory.ARMOR.getCanPut().test(item) || AuctionCategory.WEAPON.getCanPut().test(item) || stack instanceof ItemTool;
    }
}
