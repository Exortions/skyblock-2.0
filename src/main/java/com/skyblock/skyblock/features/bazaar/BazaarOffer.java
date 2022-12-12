package com.skyblock.skyblock.features.bazaar;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.UUID;

public interface BazaarOffer {

    UUID getOwner();
    int getAmount();
    double getPrice();

}
