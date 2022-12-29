package com.skyblock.skyblock.features.auction;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.Rarity;
import de.tr7zw.nbtapi.NBTItem;
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

    private final File folder;

    public AuctionHouse() {
        folder = new File(PATH);

        if (!folder.exists()) folder.mkdirs();

        init();
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
            if (ah.getTopBidder() != null && ah.getTopBidder().getName().equals(player.getName())) auctions.add(ah);
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

    public List<Auction> getAuctions(AuctionCategory category, AuctionSettings.AuctionSort sort, AuctionSettings.BinFilter binFilter, Rarity teir, int page, String search, boolean timeSensitive) {
        List<Auction> auctions = new ArrayList<>();

        int start = (page - 1) * 24;
        int end = page * 24;

        if (page == -1) {
            start = 0;
            end = AUCTION_CACHE.size();
        }

        for (int i = start; i < end; i++) {
            if (i == AUCTION_CACHE.size()) break;

            Auction auction = new ArrayList<>(AUCTION_CACHE.values()).get(i);

            if (auctions.contains(auction)) continue;
            if (!category.getCanPut().test(auction.getItem())) continue;
            if (teir != null && !Rarity.valueOf(ChatColor.stripColor(new NBTItem(auction.getItem()).getString("rarity")).split(" ")[0]).equals(teir)) continue;
            if (binFilter.equals(AuctionSettings.BinFilter.BIN) && !auction.isBIN()) continue;
            if (binFilter.equals(AuctionSettings.BinFilter.AUCTIONS) && auction.isBIN()) continue;
            if (!ChatColor.stripColor(auction.getItem().getItemMeta().getDisplayName()).toLowerCase().contains(search.toLowerCase()) && !search.equals("")) continue;
            if ((auction.isSold() || auction.getTimeLeft() == 0) && timeSensitive) continue;

            auctions.add(auction);
            if (page != 0) i = auctions.size() - 1;
        }

        auctions.sort(new Comparator<Auction>() {
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

        return auctions;
    }

    public Auction createAuction(ItemStack item, Player seller, long price, long time, boolean isBIN) {
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

    public void deleteAuction(Auction auction) {
        AUCTION_CACHE.remove(auction.getUuid());
        new File(folder.getPath() + File.separator + auction.getUuid() + ".yml").delete();
    }

    public void saveToDisk() {
        for (Auction auction : AUCTION_CACHE.values()) {
            try {
                File file = new File(folder.getPath() + File.separator + auction.getUuid() + ".yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                config.set("item", auction.getItem());
                config.set("seller", auction.getSeller().getName());
                config.set("topBidder", auction.getTopBidder() != null ? auction.getTopBidder().getName() : "");
                config.set("price", auction.getPrice());
                config.set("timeLeft", auction.getTimeLeft());
                config.set("isBIN", auction.isBIN());
                config.set("sold", auction.isSold());

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
    public void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Auction auction : AUCTION_CACHE.values()) {
                    auction.tickPassed();
                }
            }
        }.runTaskTimerAsynchronously(Skyblock.getPlugin(), 5L, 1);

        if (AUCTION_CACHE.isEmpty()) {
            for (File file : folder.listFiles()) {
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

                AUCTION_CACHE.put(auction.getUuid(), auction);
            }
        }

        if (folder.exists()) return;

        try {
            folder.mkdirs();
        } catch (Exception ignored) { }
    }
}