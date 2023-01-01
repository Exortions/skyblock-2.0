package com.skyblock.skyblock.features.auction.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionCategory;
import com.skyblock.skyblock.features.auction.AuctionHouse;
import com.skyblock.skyblock.features.auction.AuctionSettings;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import com.skyblock.skyblock.utilities.sign.SignGui;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AuctionBrowserGUI extends Gui {

    public AuctionBrowserGUI(Player player) {
        this(player, 1, "");
    }

    public AuctionBrowserGUI(Player player, int page, String search) {
        super("Auctions Browser", 54, new HashMap<String, Runnable>() {{
            AuctionSettings settings = SkyblockPlayer.getPlayer(player).getAuctionSettings();

            put(ChatColor.GREEN + "Sort", () -> {
                settings.incrementSort();
                new AuctionBrowserGUI(player, page, search).show(player);
            });

            put(ChatColor.GREEN + "Item Tier", () -> {
                settings.incrementRarity();
                new AuctionBrowserGUI(player, page, search).show(player);
            });

            put(ChatColor.YELLOW + "BIN Filter", () -> {
                settings.incrementBin();
                new AuctionBrowserGUI(player, page, search).show(player);
            });

            put(ChatColor.GREEN + "Next Page", () -> {
                new AuctionBrowserGUI(player, page + 1, search).show(player);
            });

            put(ChatColor.GREEN + "Previous Page", () -> {
                new AuctionBrowserGUI(player, page - 1, search).show(player);
            });

            put(ChatColor.GREEN + "Search", () -> {
                SignGui sign = new SignGui(Skyblock.getPlugin().getSignManager(), e -> {
                    String[] lines = e.getLines();
                    StringBuilder input = new StringBuilder("");

                    for (String line : lines) {
                        input.append(line);
                    }

                    Util.delay(() -> {
                        new AuctionBrowserGUI(player, 1, input.toString()).show(player);
                    }, 1);
                });

                sign.open(player);
            });

            put(ChatColor.GREEN + "Go Back", () -> {
               new AuctionHouseGUI(player).show(player);
            });

            put(ChatColor.GOLD + "Weapons", () -> {
                settings.setCategory(AuctionCategory.WEAPON);
                Util.delay(() -> {
                    new AuctionBrowserGUI(player).show(player);
                }, 1);
            });

            put(ChatColor.AQUA + "Armor", () -> {
                settings.setCategory(AuctionCategory.ARMOR);
                Util.delay(() -> {
                    new AuctionBrowserGUI(player).show(player);
                }, 1);
            });

            put(ChatColor.DARK_GREEN + "Accessories", () -> {
                settings.setCategory(AuctionCategory.ACCESSORY);
                Util.delay(() -> {
                    new AuctionBrowserGUI(player).show(player);
                }, 1);
            });

            put(ChatColor.RED + "Consumables", () -> {
                settings.setCategory(AuctionCategory.CONSUMABLES);
                Util.delay(() -> {
                    new AuctionBrowserGUI(player).show(player);
                }, 1);
            });

            put(ChatColor.YELLOW + "Blocks", () -> {
                settings.setCategory(AuctionCategory.BLOCKS);
                Util.delay(() -> {
                    new AuctionBrowserGUI(player).show(player);
                }, 1);
            });

            put(ChatColor.LIGHT_PURPLE + "Tools & Misc", () -> {
                settings.setCategory(AuctionCategory.MISC);
                Util.delay(() -> {
                    new AuctionBrowserGUI(player).show(player);
                }, 1);
            });
        }}, new HashMap<ItemStack, Runnable>() {{
            AuctionHouse ah = Skyblock.getPlugin().getAuctionHouse();
            AuctionSettings settings = SkyblockPlayer.getPlayer(player).getAuctionSettings();

            AuctionCategory category = settings.getCategory();
            AuctionSettings.AuctionSort sort = settings.getSort();
            AuctionSettings.BinFilter bin = settings.getBinFilter();
            Rarity tier = settings.getTier();

            int j = 0;
            for (int i = 0; i < 54; i++) {
                if (j == ah.getAuctions(category, sort, bin, tier, page, search, true).size()) break;

                Auction auction = ah.getAuctions(category, sort, bin, tier, page, search, true).get(j);

                put(auction.getDisplayItem(false, auction.isOwn(player)), () -> {
                    new AuctionInspectGUI(auction, player).show(player);
                });

                j++;
            }
        }});

        AuctionHouse ah = Skyblock.getPlugin().getAuctionHouse();
        AuctionSettings settings = SkyblockPlayer.getPlayer(player).getAuctionSettings();

        AuctionCategory category = settings.getCategory();
        AuctionSettings.AuctionSort sort = settings.getSort();
        AuctionSettings.BinFilter bin = settings.getBinFilter();
        Rarity tier = settings.getTier();

        Util.fillBorder(this, Material.STAINED_GLASS_PANE, category.getColor());

        List<String> sortList = new ArrayList<>(Arrays.asList("Highest Bid", "Lowest Bid", "Ending soon", "Most Bids"));
        List<String> rarityList = new ArrayList<>();

        rarityList.add("No filter");
        rarityList.addAll(Arrays.asList(Rarity.stringValues()));
        if (tier == null) {
            rarityList.set(0, ChatColor.DARK_GRAY + "No filter");

            rarityList.forEach((r) -> {
                if (!r.equals(ChatColor.DARK_GRAY + "No filter")) {
                    rarityList.set(rarityList.indexOf(r), ChatColor.GRAY + ChatColor.stripColor(r));
                }
            });
        } else {
            rarityList.forEach((r) -> {
                if (WordUtils.capitalize(tier.name().toLowerCase().replace("_", " ")).equals(ChatColor.stripColor(r))) {
                    rarityList.set(rarityList.indexOf(r), r);
                } else {
                    rarityList.set(rarityList.indexOf(r), ChatColor.GRAY + ChatColor.stripColor(r));
                }
            });
        }

        sortList.forEach((s) -> {
            if (s.split(" ")[0].equalsIgnoreCase(sort.name())) {
                sortList.set(sortList.indexOf(s), ChatColor.AQUA + s);
            } else {
                sortList.set(sortList.indexOf(s), ChatColor.GRAY + s);
            }
        });

        HashMap<AuctionSettings.BinFilter, Material> filterMaterials = new HashMap<AuctionSettings.BinFilter, Material>() {{
            put(AuctionSettings.BinFilter.ALL, Material.POWERED_RAIL);
            put(AuctionSettings.BinFilter.BIN, Material.GOLD_INGOT);
            put(AuctionSettings.BinFilter.AUCTIONS, Material.GOLD_BLOCK);
        }};

        List<String> binFilter = Arrays.asList("Show All", "Bin Only", "Auctions Only");

        binFilter.set(0, (bin.equals(AuctionSettings.BinFilter.ALL) ? ChatColor.DARK_AQUA : ChatColor.GRAY) + binFilter.get(0));

        binFilter.forEach((s) -> {
            if (!s.startsWith("Show")) {
                if (s.split(" ")[0].equalsIgnoreCase(bin.name())) {
                    binFilter.set(binFilter.indexOf(s), ChatColor.DARK_AQUA + s);
                } else {
                    binFilter.set(binFilter.indexOf(s), ChatColor.GRAY + s);
                }
            }
        });

        int maxPage = ((int) Math.ceil(ah.getAuctions(category, sort, bin, tier, -1, search, true).size() / 24F));

        addItem(0, AuctionCategory.WEAPON.getDisplay());
        addItem(9, AuctionCategory.ARMOR.getDisplay());
        addItem(18, AuctionCategory.ACCESSORY.getDisplay());
        addItem(27, AuctionCategory.CONSUMABLES.getDisplay());
        addItem(36, AuctionCategory.BLOCKS.getDisplay());
        addItem(45, AuctionCategory.MISC.getDisplay());
        addItem(46, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, category.getColor()).toItemStack());
        addItem(47, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, category.getColor()).toItemStack());
        addItem(10, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, category.getColor()).toItemStack());
        addItem(19, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, category.getColor()).toItemStack());
        addItem(28, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, category.getColor()).toItemStack());
        addItem(37, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, category.getColor()).toItemStack());
        addItem(48, new ItemBuilder(ChatColor.GREEN + "Search", Material.SIGN).addLore("&7Find items by name, type, lore", "&7or enchants.", " ", ChatColor.YELLOW + "Click to search").toItemStack());
        addItem(49, Util.buildBackButton("&7To Auction House"));
        addItem(50, new ItemBuilder(ChatColor.GREEN + "Sort", Material.HOPPER).addLore(" ").addLore(sortList).addLore(" ", ChatColor.YELLOW + "Click to switch!").toItemStack());
        addItem(51, new ItemBuilder(ChatColor.GREEN + "Item Tier", Material.EYE_OF_ENDER).addLore(" ").addLore(rarityList).addLore(" ", ChatColor.YELLOW + "Click to switch rarity").toItemStack());
        addItem(52, new ItemBuilder(ChatColor.YELLOW + "BIN Filter", filterMaterials.get(bin)).addLore(" ").addLore(binFilter).addLore(" ", ChatColor.YELLOW + "Click to switch!").toItemStack());
        if (maxPage != page) addItem(53, new ItemBuilder(ChatColor.GREEN + "Next Page", Material.ARROW).addLore("&7(" + page + "/" + maxPage + ")", " ", ChatColor.AQUA + "Right-click to skip", ChatColor.YELLOW + "Click to turn page").toItemStack());
        if (page != 1) addItem(46, new ItemBuilder(ChatColor.GREEN + "Previous Page", Material.ARROW).addLore("&7(" + page + "/" + maxPage + ")", " ", ChatColor.AQUA + "Right-click to skip", ChatColor.YELLOW + "Click to turn page").toItemStack());

        int j = 0;
        for (int i = 0; i < 54; i++) {
            if (getItems().containsKey(i)) continue;

            if (j == getAuctions(category, sort, bin, tier, page, search).size()) break;

            Auction auction = getAuctions(category, sort, bin, tier, page, search).get(j);

            addItem(i, auction.getDisplayItem(false, auction.isOwn(player)));
            j++;
        }
    }

    private List<Auction> getAuctions(AuctionCategory category, AuctionSettings.AuctionSort sort, AuctionSettings.BinFilter filter, Rarity rarity, int page, String search) {
        return Skyblock.getPlugin().getAuctionHouse().getAuctions(category, sort, filter, rarity, page, search, true);
    }
}
