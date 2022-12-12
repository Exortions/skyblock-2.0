package com.skyblock.skyblock.features.bazaar.impl;

import com.skyblock.skyblock.features.bazaar.BazaarOffer;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class SkyblockBazaarOffer implements BazaarOffer, ConfigurationSerializable {

    private final UUID owner;
    private final int amount;
    private final double price;

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("owner", this.owner.toString());
        map.put("amount", this.amount);
        map.put("price", this.price);

        return map;
    }

    public SkyblockBazaarOffer deserialize(Map<String, Object> map) {
        return new SkyblockBazaarOffer(
                UUID.fromString((String) map.get("owner")),
                (int) map.get("amount"),
                (double) map.get("price")
        );
    }
}
