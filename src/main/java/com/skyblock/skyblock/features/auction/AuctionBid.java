package com.skyblock.skyblock.features.auction;


import com.skyblock.skyblock.Skyblock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuctionBid implements ConfigurationSerializable {

    private OfflinePlayer bidder;
    private Auction auction;
    private int amount;
    private long timeStamp;

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("bidder", bidder.getName());
        map.put("auction", auction.getUuid().toString());
        map.put("amount", amount);
        map.put("timeStamp", timeStamp);

        return map;
    }

    public void setAuction(Auction auction) { this.auction = auction; }

    public static AuctionBid deserialize(Map<String, Object> map) {
        return new AuctionBid((map.get("bidder").equals("") ? null : Bukkit.getOfflinePlayer((String) map.get("bidder"))), AuctionHouse.AUCTION_CACHE.get(UUID.fromString((String) map.get("auction"))),
                (int) map.get("amount"), (long) map.get("timeStamp"));
    }
}
