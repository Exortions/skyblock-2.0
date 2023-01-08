package com.skyblock.skyblock.features.enchantment;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.auction.AuctionCategory;
import com.skyblock.skyblock.utilities.Pair;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemTool;
import org.apache.commons.lang3.Range;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EnchantingTableGUI extends CraftInventoryCustom implements Listener {

    private final SkyblockPlayer player;
    private final HashMap<EnchantingTableSelectionTier, List<ItemEnchantment>> enchantments = new HashMap<>();
    private int bookshelves;
    private final HashMap<EnchantingTableSelectionTier, Integer> levels = new HashMap<>();
    public EnchantingTableGUI(Player player, Location origin) {
        super(null, 54, "Enchanting Table");

        this.player = SkyblockPlayer.getPlayer(player);

        Util.fillEmpty(this);

        setItem(0, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 4).toItemStack());
        setItem(8, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 4).toItemStack());
        setItem(45, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 4).toItemStack());
        setItem(53, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 4).toItemStack());

        for (int i = 2; i < 7; i++) {
            setItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 14).toItemStack());
        }

        setItem(12, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 14).toItemStack());
        setItem(14, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, (short) 14).toItemStack());

        this.resetItem();

        setItem(49, Util.buildCloseButton());

        for (Block block : Util.blocksFromTwoPoints(origin.clone().add(2, 2, 2), origin.clone().add(-2, -2, -2))) {
            if (block.getType().equals(Material.BOOKSHELF)) bookshelves++;
        }

        bookshelves = Math.min(bookshelves, 32);

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin());
    }

    public void resetItem() {
        setItem(13, new ItemStack(Material.AIR));

        this.enchantments.clear();

        setItem(29, new ItemBuilder(ChatColor.RED + "Enchant Item", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7Place an item in the open slot\n&7to enchant it!")).toItemStack());
        setItem(31, new ItemBuilder(ChatColor.RED + "Enchant Item", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7Place an item in the open slot\n&7to enchant it!")).toItemStack());
        setItem(33, new ItemBuilder(ChatColor.RED + "Enchant Item", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7Place an item in the open slot\n&7to enchant it!")).toItemStack());
    }

    public void invalidStack() {
        this.enchantments.clear();

        setItem(29, new ItemBuilder(ChatColor.RED + "Invalid Stack Size", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7You can only enchant 1 item\n&7at a time!")).toItemStack());
        setItem(31, new ItemBuilder(ChatColor.RED + "Invalid Stack Size", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7You can only enchant 1 item\n&7at a time!")).toItemStack());
        setItem(33, new ItemBuilder(ChatColor.RED + "Invalid Stack Size", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7You can only enchant 1 item\n&7at a time!")).toItemStack());
    }

    public void cannotEnchant() {
        this.enchantments.clear();

        setItem(29, new ItemBuilder(ChatColor.RED + "Cannot Enchant Item", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7This item cannot be enchanted!")).toItemStack());
        setItem(31, new ItemBuilder(ChatColor.RED + "Cannot Enchant Item", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7This item cannot be enchanted!")).toItemStack());
        setItem(33, new ItemBuilder(ChatColor.RED + "Cannot Enchant Item", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7This item cannot be enchanted!")).toItemStack());
    }

    public void alreadyEnchanted() {
        this.enchantments.clear();

        setItem(29, new ItemBuilder(ChatColor.RED + "Already enchanted", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7This item already has an\n&7enchantment applied to it!")).toItemStack());
        setItem(31, new ItemBuilder(ChatColor.RED + "Already enchanted", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7This item already has an\n&7enchantment applied to it!")).toItemStack());
        setItem(33, new ItemBuilder(ChatColor.RED + "Already enchanted", Material.INK_SACK, (short) 8).setLore(Util.buildLore("&7This item already has an\n&7enchantment applied to it!")).toItemStack());
    }

    private void canEnchant() {
        setItem(29, getEnchantingTableSelectionItem(EnchantingTableSelectionTier.NINE));
        setItem(31, getEnchantingTableSelectionItem(EnchantingTableSelectionTier.EIGHTEEN));
        setItem(33, getEnchantingTableSelectionItem(EnchantingTableSelectionTier.SIXTY));
    }

    @Getter
    @AllArgsConstructor
    enum EnchantingTableSelectionTier {
        NINE(29),
        EIGHTEEN(31),
        SIXTY(33);

        private final int slot;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals(getName())) return;

        HandlerList.unregisterAll(this);
        if (getItem(13) != null) e.getPlayer().getInventory().addItem(getItem(13));
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        if (e.getClickedInventory().equals(e.getWhoClicked().getInventory()) &&
                e.getWhoClicked().getOpenInventory().getTitle().equals(getName())) {
            Util.delay(() -> {
                Bukkit.getPluginManager().callEvent(new InventoryClickEvent(e.getView(), e.getSlotType(), 13, e.getClick(), e.getAction()));
            }, 1);
        }

        if (!e.getClickedInventory().equals(this)) return;

        e.setCancelled(true);

        if (Arrays.asList(29, 31, 33).contains(e.getSlot())) {
            if (e.getCurrentItem() == null && !e.getCurrentItem().getType().equals(Material.EXP_BOTTLE)) return;
            ItemBase base = new ItemBase(getItem(13));

            if (e.getSlot() == 29) {
                this.apply(levels.get(EnchantingTableSelectionTier.NINE), this.enchantments.get(EnchantingTableSelectionTier.NINE), base);
            } else if (e.getSlot() == 31) {
                this.apply(levels.get(EnchantingTableSelectionTier.EIGHTEEN), this.enchantments.get(EnchantingTableSelectionTier.EIGHTEEN), base);
            } else if (e.getSlot() == 33) {
                this.apply(levels.get(EnchantingTableSelectionTier.SIXTY), this.enchantments.get(EnchantingTableSelectionTier.SIXTY), base);
            }
        }

        if (e.getSlot() != 13) return;

        e.setCancelled(false);

        Util.delay(() -> {
            ItemStack item = getItem(13);

            if (item == null) {
                resetItem();
                return;
            }

            if (isEnchantable(item)) {
                if (item.getAmount() > 1) invalidStack();
                else {
                    ItemBase base;

                    try {
                        base = new ItemBase(item);
                    } catch (IllegalArgumentException ex) {
                        cannotEnchant();
                        return;
                    }

                    if (base.getEnchantments().size() > 0) {
                        alreadyEnchanted();
                        return;
                    }

                    double baseLevel = Util.random(1,8) + Math.floor(bookshelves / 2f) + Util.random(0,bookshelves);
                    int bottom = (int) Math.floor(Math.max(baseLevel / 3, 1));
                    int mid = (int) Math.floor((baseLevel * 2) / 3 + 1);
                    int top = (int) Math.floor(Math.max(baseLevel, bookshelves * 2));

                    levels.put(EnchantingTableSelectionTier.SIXTY, top);
                    levels.put(EnchantingTableSelectionTier.EIGHTEEN, mid);
                    levels.put(EnchantingTableSelectionTier.NINE, bottom);

                    List<ItemEnchantment> nine = this.enchant(base, EnchantingTableSelectionTier.NINE, bottom);
                    List<ItemEnchantment> eighteen = this.enchant(base, EnchantingTableSelectionTier.EIGHTEEN, mid);
                    List<ItemEnchantment> sixty = this.enchant(base, EnchantingTableSelectionTier.SIXTY, top);


                    this.enchantments.put(EnchantingTableSelectionTier.NINE, nine);
                    this.enchantments.put(EnchantingTableSelectionTier.EIGHTEEN, eighteen);
                    this.enchantments.put(EnchantingTableSelectionTier.SIXTY, sixty);
                    
                    canEnchant();
                }
            } else {
                cannotEnchant();
            }
        }, 1);
    }

    private boolean isEnchantable(ItemStack item) {
        Item stack = CraftItemStack.asNMSCopy(item).getItem();

        if (item.getAmount() > 1) return false;

        return AuctionCategory.ARMOR.getCanPut().test(item) || AuctionCategory.WEAPON.getCanPut().test(item) || stack instanceof ItemTool;
    }

    public ItemStack getEnchantingTableSelectionItem(EnchantingTableSelectionTier tier) {
        return new ItemBuilder(ChatColor.GREEN + "Enchant Item &8- " + ChatColor.GOLD + levels.get(tier) + " Levels", Material.EXP_BOTTLE, levels.get(tier)).setLore(Util.buildLore("&7Guarantees at least:\n&7 * " + this.enchantments.get(tier).get(0).getBaseEnchantment().getDisplayName() + " " + Util.toRoman(this.enchantments.get(tier).get(0).getLevel()) + "\n\n&eClick to enchant!")).toItemStack();
    }

    public void apply(int levels, List<ItemEnchantment> enchantments, ItemBase base) {
        if (this.player.getBukkitPlayer().getLevel() < levels && !this.player.getBukkitPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            this.player.getBukkitPlayer().sendMessage(ChatColor.RED + "You do not have enough levels to enchant this item!");
            return;
        }

        if (!this.player.getBukkitPlayer().getGameMode().equals(GameMode.CREATIVE)) this.player.getBukkitPlayer().setLevel(this.player.getBukkitPlayer().getLevel() - levels);
        this.player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.LEVEL_UP, 10, 1);

        for (ItemEnchantment enchantment : enchantments) {
            base.addEnchantment(enchantment.getBaseEnchantment().getName(), enchantment.getLevel());
        }

        base.createStack();

        setItem(13, base.getStack());

        alreadyEnchanted();
    }

    public List<ItemEnchantment> enchant(ItemBase item, EnchantingTableSelectionTier tier, double l) {
        double mod = l + Util.random(0, getEnchantability(item.getStack()) / 4) + Util.random(0, getEnchantability(item.getStack()) / 4) + 1;
        HashMap<Pair<String, Integer>, Long> possible = new HashMap<>();
        for (SkyblockEnchantmentHandler.ModdedLevel level : Skyblock.getPlugin().getEnchantmentHandler().getLevels()) {
            List<Range<Integer>> ranges = new ArrayList<>(level.getRanges().values());

            for (Range<Integer> range : ranges) {
                if (range.getMinimum() <= mod && range.getMaximum() >= mod) {
                    possible.put(Pair.of(level.getName(), new ArrayList<>(level.getRanges().values()).indexOf(range) + 1), level.getWeight());
                    break;
                }
            }
        }

        int sum = 0;

        for (Long weight : possible.values()) {
            sum += weight;
        }

        List<Pair<String, Integer>> enchants = new ArrayList<>();

        enchants.add(random(possible, sum, item));

        for (Map.Entry<Pair<String, Integer>, Long> entry : possible.entrySet()) {
            if (enchants.contains(entry.getKey())) continue;

            if (Util.random(0, 100) < (mod + 1) / 50) {
                Pair<String, Integer> rand = random(possible, sum, item);
                if (!enchants.contains(rand)) enchants.add(rand);
                mod = mod / 2;
            }
        }

        SkyblockEnchantmentHandler handler = Skyblock.getPlugin().getEnchantmentHandler();

        List<ItemEnchantment> itemEnchantments = new ArrayList<>();

        for (Pair<String, Integer> enchant : enchants) {
            if (enchant == null) continue;
            if (enchant.first() == null) continue;
            SkyblockEnchantment ench = handler.getEnchantment(enchant.first().toLowerCase().replaceAll("_", ""));
            if (ench == null) continue;
            itemEnchantments.add(new ItemEnchantment(ench, enchant.getSecond()));
        }

        return itemEnchantments;
    }

    private Pair<String, Integer> random(HashMap<Pair<String, Integer>, Long> possible, int sum, ItemBase item) {
        SkyblockEnchantmentHandler handler = Skyblock.getPlugin().getEnchantmentHandler();
        List<SkyblockEnchantment> enchantments = handler.getEnchantments(ChatColor.stripColor(item.getRarity().replace(item.getRarityEnum().name().toUpperCase() + " ", "")).toLowerCase());

        int rand = Util.random(0, sum);
        for (Map.Entry<Pair<String, Integer>, Long> entry : possible.entrySet()) {
            rand -= entry.getValue();
            SkyblockEnchantment ench = handler.getEnchantment(entry.getKey().first().toLowerCase().replaceAll("_", ""));
            if (rand < 0 && ench != null && enchantments.contains(ench)) {
                return entry.getKey();
            }
        }

        return null;
    }

    private int getEnchantability(ItemStack item) {
        String mat = item.getType().name();

        if (mat.contains("WOOD")) return 15;
        if (mat.contains("LEATHER")) return 15;
        if (mat.contains("STONE")) return 5;
        if (mat.contains("CHAIN")) return 12;
        if (mat.contains("IRON")) return 14;
        if (mat.contains("GOLD")) return 25;
        if (mat.contains("DIAMOND")) return 10;

        return 0;
    }
}
