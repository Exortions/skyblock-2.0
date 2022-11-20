package com.skyblock.skyblock.features.merchants;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class MerchantHandler {

    private final HashMap<String, Merchant> merchants;

    public MerchantHandler() {
        this.merchants = new HashMap<>();

        this.registerMerchants();
    }

    public void registerMerchants() {
        // TODO: Load from JSON file

        registerMerchant("mine_merchant", new Merchant(
                "Mine Merchant",
                "akhunta",
                Collections.singletonList(
                        new MerchantItem(
                                Skyblock.getPlugin(Skyblock.class).getItemHandler().getItem("ASPECT_OF_THE_END.json"),
                                "sb item ASPECT_OF_THE_END.json",
                                100
                        )
                ),
                new Location(Bukkit.getWorld("world"), -19, 70, -48)
        ));
    }

    public void registerMerchant(String name, Merchant merchant) {
        this.merchants.put(name, merchant);

        Bukkit.getPluginManager().registerEvents(merchant, Skyblock.getPlugin(Skyblock.class));
    }

    public Merchant getMerchant(String name) {
        return this.merchants.get(name);
    }

    public HashMap<String, Merchant> getMerchants() {
        return this.merchants;
    }

}
