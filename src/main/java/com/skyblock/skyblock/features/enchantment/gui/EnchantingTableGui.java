package com.skyblock.skyblock.features.enchantment.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.ItemEnchantment;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantment;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantmentHandler;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class EnchantingTableGui extends Gui {

    private final SkyblockPlayer player;

    private final HashMap<EnchantingTableSelectionTier, List<ItemEnchantment>> enchantments = new HashMap<>();

    private boolean internalRecentlyClicked = false;
    private boolean recentlyClicked = false;

    public EnchantingTableGui(Player player) {
        super("Enchant Item", 54, new HashMap<String, Runnable>() {{
            put(ChatColor.RED + "Close", player::closeInventory);
        }});

        this.player = SkyblockPlayer.getPlayer(player);

        Util.fillEmpty(this);

        this.addItem(0, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 4).toItemStack());
        this.addItem(8, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 4).toItemStack());
        this.addItem(45, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 4).toItemStack());
        this.addItem(53, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 4).toItemStack());

        for (int i = 2; i < 7; i++) {
            this.addItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 14).toItemStack());
        }

        this.addItem(12, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 14).toItemStack());
        this.addItem(14, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 14).toItemStack());

        this.resetItem();

        this.addItem(49, Util.buildCloseButton());
    }

    @Getter
    @AllArgsConstructor
    enum EnchantingTableSelectionTier {
        NINE(29, 9),
        EIGHTEEN(31, 18),
        SIXTY(33, 60)
        ;

        private final int slot;
        private final int levels;
    }

    public void resetItem() {
        this.addItem(13, new ItemStack(Material.AIR));

        this.enchantments.clear();

        this.addItem(29, new ItemBuilder(ChatColor.RED + "Enchant Item", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7Place an item in the open slot\n&7to enchant it!")).toItemStack());
        this.addItem(31, new ItemBuilder(ChatColor.RED + "Enchant Item", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7Place an item in the open slot\n&7to enchant it!")).toItemStack());
        this.addItem(33, new ItemBuilder(ChatColor.RED + "Enchant Item", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7Place an item in the open slot\n&7to enchant it!")).toItemStack());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().getName().equalsIgnoreCase(this.getName())) return;

        if (this.getItem(13) != null && !this.getItem(13).getType().equals(Material.AIR)) {
            this.player.getBukkitPlayer().getInventory().addItem(this.getItem(13));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getClickedInventory().equals(this.player.getBukkitPlayer().getOpenInventory().getTopInventory()) || this.internalRecentlyClicked) return;

        this.internalRecentlyClicked = true;

        Util.delay(() -> this.internalRecentlyClicked = false, 2);

        if (event.getSlot() == 13) {
            ItemStack clone = event.getCurrentItem().clone();
            this.player.getBukkitPlayer().getInventory().addItem(clone);

            this.resetItem();

            this.show(this.player.getBukkitPlayer());
        }

        if (event.getCurrentItem().getType().equals(Material.EXP_BOTTLE) && (this.getItem(13) != null && !this.getItem(13).getType().equals(Material.AIR))) {
            ItemBase base = new ItemBase(this.getItem(13));

            if (event.getSlot() == 29) {
                this.apply(EnchantingTableSelectionTier.NINE.getLevels(), this.enchantments.get(EnchantingTableSelectionTier.NINE), base);
            } else if (event.getSlot() == 31) {
                this.apply(EnchantingTableSelectionTier.EIGHTEEN.getLevels(), this.enchantments.get(EnchantingTableSelectionTier.EIGHTEEN), base);
            } else if (event.getSlot() == 33) {
                this.apply(EnchantingTableSelectionTier.SIXTY.getLevels(), this.enchantments.get(EnchantingTableSelectionTier.SIXTY), base);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAddItem(InventoryClickEvent event) {
        if (!event.getClickedInventory().equals(this.player.getBukkitPlayer().getInventory()) || this.recentlyClicked) return;

        if (!this.player.getBukkitPlayer().getOpenInventory().getTopInventory().getName().equalsIgnoreCase(this.getName())) return;

        event.setCancelled(true);

        this.recentlyClicked = true;

        player.getBukkitPlayer().sendMessage("test");

        Util.delay(() -> this.recentlyClicked = false, 2);

        ItemBase base;

        try {
            base = new ItemBase(event.getCurrentItem());
        } catch (Exception ex) {
            this.player.getBukkitPlayer().sendMessage(ChatColor.RED + "You cannot enchant this item!");
            return;
        }

        player.getBukkitPlayer().sendMessage("test2");

        ItemStack clone = event.getCurrentItem().clone();

        this.addItem(13, clone);
        this.player.getBukkitPlayer().getInventory().removeItem(clone);

        player.getBukkitPlayer().sendMessage("test3");

        List<ItemEnchantment> nine = this.enchant(base, EnchantingTableSelectionTier.NINE);
        List<ItemEnchantment> eighteen = this.enchant(base, EnchantingTableSelectionTier.EIGHTEEN);
        List<ItemEnchantment> sixty = this.enchant(base, EnchantingTableSelectionTier.SIXTY);

        player.getBukkitPlayer().sendMessage("test4");

        this.enchantments.put(EnchantingTableSelectionTier.NINE, nine);
        this.enchantments.put(EnchantingTableSelectionTier.EIGHTEEN, eighteen);
        this.enchantments.put(EnchantingTableSelectionTier.SIXTY, sixty);

        player.getBukkitPlayer().sendMessage("test5");

        for (EnchantingTableSelectionTier tier : EnchantingTableSelectionTier.values()) {
            this.addItem(tier.getSlot(), this.getEnchantingTableSelectionItem(base, tier));
        }

        player.getBukkitPlayer().sendMessage("test6");

        this.show(this.player.getBukkitPlayer());
    }

    public ItemStack getEnchantingTableSelectionItem(ItemBase item, EnchantingTableSelectionTier tier) {
        if (item.getEnchantments().size() > 0) return new ItemBuilder(ChatColor.RED + "Already enchanted", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7This item already has an\n&7enchantment applied to it!")).toItemStack();

        return new ItemBuilder(ChatColor.GREEN + "Enchant Item &8- " + ChatColor.GOLD + tier.getLevels() + " Levels", Material.EXP_BOTTLE).setLore(Util.buildLore("&7Guarantees at least:\n&7 * " + this.enchantments.get(tier).get(0).getBaseEnchantment().getDisplayName() + " " + Util.toRoman(this.enchantments.get(tier).get(0).getLevel()) + "\n\n&eClick to enchant!")).toItemStack();
    }

    public void apply(int levels, List<ItemEnchantment> enchantments, ItemBase base) {
        if (this.player.getBukkitPlayer().getLevel() < levels) {
            this.player.getBukkitPlayer().sendMessage(ChatColor.RED + "You do not have enough levels to enchant this item!");
            return;
        }

        this.player.getBukkitPlayer().setLevel(this.player.getBukkitPlayer().getLevel() - levels);

        for (ItemEnchantment enchantment : enchantments) {
            base.addEnchantment(enchantment.getBaseEnchantment().getName(), enchantment.getLevel());
        }

        base.createStack();

        this.addItem(13, base.getStack());
    }

    public List<ItemEnchantment> enchant(ItemBase item, EnchantingTableSelectionTier tier) {
        Random random = Skyblock.getPlugin().getRandom();
        SkyblockEnchantmentHandler handler = Skyblock.getPlugin().getEnchantmentHandler();
        List<ItemEnchantment> enchantments = new ArrayList<>();

        List<SkyblockEnchantment> possible = handler.getEnchantments(ChatColor.stripColor(item.getRarity().replace(item.getRarityEnum().name().toUpperCase() + " ", "")).toLowerCase());

        if (tier.equals(EnchantingTableSelectionTier.NINE)) {
            enchantments.add(new ItemEnchantment(possible.get(random.nextInt(possible.size())), 1));
        } else if (tier.equals(EnchantingTableSelectionTier.EIGHTEEN)) {
            for (int i = 0; i < random.nextInt(2) + 1; i++) enchantments.add(new ItemEnchantment(possible.get(random.nextInt(possible.size())), 1));
        } else if (tier.equals(EnchantingTableSelectionTier.SIXTY)) {
            for (int i = 0; i < random.nextInt(3) + 1; i++) {
                SkyblockEnchantment base = possible.get(random.nextInt(possible.size()));

                enchantments.add(new ItemEnchantment(base, base.getMaxLevel()));
            }
        }

        return enchantments;
    }

}
