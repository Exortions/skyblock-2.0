package com.skyblock.skyblock.features.auction.bot;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionBid;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AuctionBot {

    public AuctionBot() {

    }

    public List<Auction> getAuctionsAPI(int page) {
        List<Auction> auctions = new ArrayList<>();

        try {
            Bukkit.getConsoleSender().sendMessage("STARTED REQUEST");
            URL url = new URL("https://api.hypixel.net/skyblock/auctions?page=" + page);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();

            if (status != 200) {
                Skyblock.getPlugin().sendMessage("Failed to initialize Auction Bot");
                return new ArrayList<>();
            }

            JSONParser parser = new JSONParser();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Object obj = parser.parse(in);
            Bukkit.getConsoleSender().sendMessage("FINISHED REQUEST");

            JSONObject jsonObject =  (JSONObject) obj;
            JSONArray auctionsJson = (JSONArray) jsonObject.get("auctions");

            for (Object auc : auctionsJson) {
                JSONObject auction = (JSONObject) auc;

                String name = (String) auction.get("item_name");
                String itemID = name.toUpperCase().replaceAll(" ", "_") + ".json";
                ItemStack neu = Skyblock.getPlugin().getItemHandler().getItem(itemID);

                if (neu != null) {
                    UUID id = getUUID(auction.get("uuid"));
                    UUID sellerId = getUUID(auction.get("auctioneer"));
                    String seller = uuidToName(sellerId);

                    long startTime = (long) auction.get("start");
                    long endTime = (long) auction.get("end");
                    long start = (long) auction.get("starting_bid");
                    long highest = (long) auction.get("highest_bid_amount");
                    boolean bin = (boolean) auction.get("bin");

                    Auction auctionObject = Skyblock.getPlugin().getAuctionHouse().createFakeAuction(neu, Bukkit.getOfflinePlayer(seller), (highest == 0 ? start : highest), (endTime - startTime) / 50, bin, id);

                    JSONArray bids = (JSONArray) auction.get("bids");

                    int i = 0;
                    for (Object b : bids) {
                        if (i >= 2) break;

                        JSONObject bid = (JSONObject) b;

                        UUID bidderId = getUUID(bid.get("bidder"));
                        String bidder = uuidToName(bidderId);
                        long amount = (long) bid.get("amount");
                        long timeStamp = (long) bid.get("timestamp");

                        auctionObject.getBidHistory().add(new AuctionBid(Bukkit.getOfflinePlayer(bidder), auctionObject, (int) amount, timeStamp));
                        i++;
                    }
                }
            }

        } catch (IOException e) {} catch (ParseException e) {
            Skyblock.getPlugin().sendMessage("Failed to initialize Auction Bot");
            return new ArrayList<>();
        }

        Bukkit.getConsoleSender().sendMessage("done");

        return auctions;
    }

    private String uuidToName(UUID uuid) {
        return uuid.toString().substring(0, Util.random(5, 8));
    }

    private UUID getUUID(Object o) {
        return getUUID((String) o);
    }

    private UUID getUUID(String s) {
        return UUID.fromString(s.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }
}
