package com.skyblock.skyblock.features.auction;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.features.auction.bot.AuctionBot;
import com.skyblock.skyblock.features.auction.display.Display;
import com.skyblock.skyblock.features.auction.display.DisplayHandler;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AuctionHouse {

    private static final String PATH = Skyblock.getPlugin().getDataFolder() + File.separator + "auctions";
    public static final HashMap<UUID, Auction> AUCTION_CACHE = new HashMap<>();
    public static final HashMap<AuctionCategory, List<Auction>> CATEGORY_CACHE = new HashMap<AuctionCategory, List<Auction>>() {{
        for (AuctionCategory c : AuctionCategory.values()) put(c, new ArrayList<>());
    }};
    public static final List<UUID> FAKE = new ArrayList<>();

    private final File folder;

    private final DisplayHandler handler;

    @Getter @Setter
    private boolean botFinished = false;

    public AuctionHouse() {
        folder = new File(PATH);

        if (!folder.exists()) folder.mkdirs();

        init();

        Util.delay(() -> new BukkitRunnable() {
            @Override
            public void run() {
                if (!Skyblock.getPlugin().getConfig().getBoolean("auction_bot.enabled")) return;

                float TOTAL_AUCTIONS = 2000;
                int pages = (int) Math.floor(TOTAL_AUCTIONS / 1000);

                Skyblock.getPlugin().sendMessage("Starting auction bot...");

                AuctionBot bot = new AuctionBot();

                for (int i = 0; i < pages; i++) {
                    int finalI = i;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            bot.getAuctionsAPI(finalI + 1);
                        }
                    }.runTaskAsynchronously(Skyblock.getPlugin());
                }
            }
        }.runTaskAsynchronously(Skyblock.getPlugin()), 20);

        this.handler = new DisplayHandler(
                Skyblock.getPlugin(),
                this,
                new Display(20, 71, -79, 1),
                new Display(23, 71, -79, 2),
                new Display(17, 71, -86, 3),
                new Display(19, 71, -86, 4),
                new Display(21, 71, -86, 5),
                new Display(23, 71, -86, 6),
                new Display(25, 71, -86, 7),
                new Display(26, 71, -81, 8),
                new Display(26, 71, -79, 9),
                new Display(26, 71, -77, 10),
                new Display(25, 71, -72, 11),
                new Display(21, 71, -72, 12),
                new Display(19, 71, -72, 13),
                new Display(17, 71, -72, 14),
                new Display(16, 78, -74, 15),
                new Display(19, 78, -74, 16),
                new Display(21, 78, -74, 17),
                new Display(23, 78, -74, 18),
                new Display(26, 78, -74, 19),
                new Display(26, 78, -79, 20),
                new Display(23, 78, -79, 21),
                new Display(21, 78, -79, 22),
                new Display(19, 78, -79, 23),
                new Display(16, 78, -79, 24),
                new Display(16, 78, -84, 25),
                new Display(19, 78, -84, 26),
                new Display(21, 78, -84, 27),
                new Display(23, 78, -84, 28),
                new Display(26, 78, -84, 29),
                new Display(25, 78, -87, 30),
                new Display(23, 78, -87, 31),
                new Display(21, 78, -87, 32),
                new Display(19, 78, -87, 33),
                new Display(17, 78, -87, 34)
        );
    }

    public List<Auction> getBiddedAuctions(Player player) {
        List<Auction> auctions = new ArrayList<>();

        for (Auction ah : getAuctions()) {
            ah.getBidHistory().forEach((bid) -> {
                if (bid.getBidder().equals(player) && !auctions.contains(ah)) auctions.add(ah);
            });
        }

        return auctions;
    }

    public List<Auction> getToppedAuctions(Player player) {
        List<Auction> auctions = new ArrayList<>();

        for (Auction ah : getAuctions()) {
            if (ah.getTopBidder() != null)
                if (ah.getTopBidder().getName().equals(player.getName())) auctions.add(ah);
        }

        return auctions;
    }

    public List<Auction> getOwnedAuctions(Player player) {
        List<Auction> auctions = new ArrayList<>();

        for (Auction ah : getAuctions()) {
            if (ah.getSeller().getName().equals(player.getName())) auctions.add(ah);
        }

        return auctions;
    }

    public List<Auction> getAuctions() {
        return getAuctions(AuctionCategory.ALL, AuctionSettings.AuctionSort.HIGHEST, AuctionSettings.BinFilter.ALL, null, -1, "", false);
    }

    public List<Auction> getAuctions(AuctionCategory category, AuctionSettings.AuctionSort sort, AuctionSettings.BinFilter binFilter, Rarity tier, int page, String search, boolean timeSensitive) {
        List<Auction> auctions = new ArrayList<>();
        List<Auction> clone = new ArrayList<>(CATEGORY_CACHE.get(category));

        int start = (page - 1) * 24;
        int end = page * 24;

        if (page == -1) {
            start = 0;
            end = clone.size();
        }

        if (end > clone.size()) end = clone.size();

        clone.sort(new Comparator<Auction>() {
            @Override
            public int compare(Auction o1, Auction o2) {
                switch (sort) {
                    case HIGHEST:
                        return (int) (o2.getPrice() - o1.getPrice());
                    case LOWEST:
                        return (int) (o1.getPrice() - o2.getPrice());
                    case ENDING:
                        return (int) (o1.getTimeLeft() - o2.getTimeLeft());
                    case MOST:
                        return (o2.getBidHistory().size() - o1.getBidHistory().size());
                }

                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });

        int j = start;
        for (int i = start; i < clone.size(); i++) {
            Auction auction = clone.get(i);

            if ((auction.isSold() || auction.isExpired()) && timeSensitive) continue;
            if (!search.equals("") && !ChatColor.stripColor(auction.getItem().getItemMeta().getDisplayName()).toLowerCase().contains(search.toLowerCase())) continue;
            if (binFilter.equals(AuctionSettings.BinFilter.BIN) && !auction.isBIN()) continue;
            if (binFilter.equals(AuctionSettings.BinFilter.AUCTIONS) && auction.isBIN()) continue;
            if (tier != null && !Rarity.valueOf(ChatColor.stripColor(new NBTItem(auction.getItem()).getString("rarity")).split(" ")[0]).equals(tier)) continue;
            if (!category.getCanPut().test(auction.getItem())) continue;

            j++;

            auctions.add(auction);

            if (j == end) break;
        }

        return auctions;
    }

    public Auction createFakeAuction(ItemStack item, OfflinePlayer seller, long price, long time, boolean isBIN, UUID id) {
        Auction auction = new Auction(item, seller, null, price, time, isBIN, false, id, new ArrayList<>(), new ArrayList<>());
        auction.setFake(true);

        AUCTION_CACHE.put(id, auction);
        FAKE.add(id);

        return AUCTION_CACHE.get(id);
    }

    public Auction createAuction(ItemStack item, OfflinePlayer seller, long price, long time, boolean isBIN) {
        File file = new File(folder.getPath() + File.separator + UUID.randomUUID() + ".yml");

        try {
            file.createNewFile();

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("item", item);
            config.set("seller", seller.getName());
            config.set("topBidder", "");
            config.set("price", price);
            config.set("timeLeft", time);
            config.set("isBIN", isBIN);
            config.set("sold", false);
            config.set("participants", new ArrayList<>(Collections.singletonList(seller)));
            config.createSection("bidHistory");

            config.save(file);

            AUCTION_CACHE.put(UUID.fromString(file.getName().replace(".yml", "")), new Auction(item, seller, null, price, time, isBIN, false, UUID.fromString(file.getName().replace(".yml", "")), new ArrayList<>(), new ArrayList<>(Collections.singletonList(seller))));

            return AUCTION_CACHE.get(UUID.fromString(file.getName().replace(".yml", "")));
        } catch (IOException ignored) { }

        return null;
    }

    public Auction createAuction(ItemStack item, Player seller, long price, long time, boolean isBIN) {
        return createAuction(item, Bukkit.getOfflinePlayer(seller.getUniqueId()), price, time, isBIN);
    }

    public void deleteAuction(Auction auction) {
        AUCTION_CACHE.remove(auction.getUuid());
        new File(folder.getPath() + File.separator + auction.getUuid() + ".yml").delete();
    }

    public void saveToDisk() {
        for (Auction auction : AUCTION_CACHE.values()) {
            if (FAKE.contains(auction.getUuid())) continue;
            if (auction.isFake()) continue;

            try {
                File file = new File(folder.getPath() + File.separator + auction.getUuid() + ".yml");

                if (!file.exists()) file.createNewFile();

                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                config.set("item", auction.getItem());
                config.set("seller", auction.getSeller().getName());
                config.set("topBidder", auction.getTopBidder() != null ? auction.getTopBidder().getName() : "");
                config.set("price", auction.getPrice());
                config.set("timeLeft", auction.getTimeLeft());
                config.set("isBIN", auction.isBIN());
                config.set("sold", auction.isSold());
                config.set("participants", auction.getParticipants());

                auction.getBidHistory().forEach((bid) -> {
                    if (bid.getAuction() == null) bid.setAuction(auction);
                    config.set("bidHistory." + bid.getTimeStamp(), bid.serialize());
                });

                config.save(file);
            } catch (IOException| NullPointerException e) {
                Skyblock.getPlugin().sendMessage("Could not save auction: " + auction);
            }
        }
    }

    public void refreshDisplays() {
        handler.updateDisplays();
    }

    public void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (Auction auction : AUCTION_CACHE.values()) {
                        auction.tickPassed();
                    }
                } catch (ConcurrentModificationException ignored) {
                    // AH hasn't been initialized yet so just ignore
                }
            }
        }.runTaskTimerAsynchronously(Skyblock.getPlugin(), 5L, 1);

        if (AUCTION_CACHE.isEmpty()) {
            for (File file : folder.listFiles()) {
                // fucking yml
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                        OfflinePlayer top = config.getString("topBidder").equals("") ? null : Bukkit.getOfflinePlayer(config.getString("topBidder"));

                        List<AuctionBid> history = new ArrayList<>();

                        for (String s1 : config.getConfigurationSection("bidHistory").getKeys(false)) {
                            Map<String, Object> map = new HashMap<>();

                            for (String s2 : config.getConfigurationSection("bidHistory." + s1).getKeys(false)) {
                                map.put(s2, config.get("bidHistory." + s1 + "." + s2));
                            }

                            history.add(AuctionBid.deserialize(map));
                        }

                        Auction auction = new Auction(config.getItemStack("item"), Bukkit.getOfflinePlayer(config.getString("seller")), top,
                                config.getLong("price"), config.getLong("timeLeft"), config.getBoolean("isBIN"),
                                config.getBoolean("sold"), UUID.fromString(file.getName().replace(".yml", "")), history, (List<OfflinePlayer>) config.getList("participants"));

                        for (AuctionCategory cat : AuctionCategory.values()) if (cat.getCanPut().test(auction.getItem())) CATEGORY_CACHE.get(cat).add(auction);

                        AUCTION_CACHE.put(auction.getUuid(), auction);
                    }
                }.runTaskAsynchronously(Skyblock.getPlugin());
            }
        }

        if (folder.exists()) return;

        try {
            folder.mkdirs();
        } catch (Exception ignored) { }
    }
}