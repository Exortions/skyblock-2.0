package com.skyblock.skyblock.features.merchants;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MerchantHandler {
    private final MerchantSellPriceHandler merchantSellPriceHandler;

    private final HashMap<String, Merchant> merchants;
    private final Skyblock skyblock;

    public MerchantHandler(Skyblock skyblock) {
        this.merchants = new HashMap<>();
        this.skyblock = skyblock;

        this.merchantSellPriceHandler = new MerchantSellPriceHandler();

        this.registerMerchants();
    }

    public void registerMerchants() {
        try {
            File file = new File(this.skyblock.getDataFolder(), "merchants.json");

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)));

            JSONArray merchants = (JSONArray) object.get("merchants");

            for (Object merchantObject : merchants) {
                JSONObject merchant = (JSONObject) merchantObject;

                String name = (String) merchant.get("name");
                String id = (String) merchant.get("id");

                String skinValue = (String) merchant.get("skinValue");
                String skinSignature = (String) merchant.get("skinSignature");

                JSONArray items = (JSONArray) merchant.get("items");

                List<MerchantItem> merchantItems = new ArrayList<>();

                for (Object itemObject : items) {
                    JSONObject item = (JSONObject) itemObject;

                    String command = (String) item.get("command");
                    int cost = ((Long) item.get("cost")).intValue();
                    int amount = ((Long) item.get("amount")).intValue();

                    boolean trade = false;
                    String tradeItem = null;
                    int tradeAmount = 0;

                    if (item.get("trade") != null) {
                        trade = (boolean) item.get("trade");

                        JSONObject tradeObject = (JSONObject) item.get("trade_item");

                        if (trade) {
                            tradeItem = (String) tradeObject.get("id");
                            tradeAmount = ((Long) tradeObject.get("amount")).intValue();
                        }
                    }

                    String identifier = (String) item.get("id");

                    ItemStack stack = this.skyblock.getItemHandler().getItem(identifier);

                    if (stack == null) {
                        this.skyblock.sendMessage(ChatColor.RED + "Failed to initialize merchant item in &8merchants.json&c: item &8" + identifier + " &cdoes not exist");
                        continue;
                    }

                    stack.setAmount(amount);

                    merchantItems.add(new MerchantItem(
                            this.skyblock.getItemHandler().getItem(identifier),
                            command,
                            cost,
                            trade,
                            tradeItem,
                            tradeAmount
                    ));
                }

                JSONObject locationObject = (JSONObject) merchant.get("location");
                Location location = new Location(
                        Skyblock.getSkyblockWorld(),
                        (double) locationObject.get("x"),
                        (double) locationObject.get("y"),
                        (double) locationObject.get("z")
                );

                JSONArray interaction = (JSONArray) merchant.get("interaction");

                List<String> initialInteraction = new ArrayList<>();

                if (interaction != null) {
                    for (Object interactionObject : interaction) {
                        initialInteraction.add((String) interactionObject);
                    }
                }

                this.registerMerchant(id, new Merchant(
                        name,
                        skinValue,
                        skinSignature,
                        merchantItems,
                        location,
                        initialInteraction,
                        Objects.equals(id, "librarian_merchant"),
                        Objects.equals(id, "librarian_merchant") ? Villager.Profession.LIBRARIAN : null
                ));
            }
        } catch (IOException | ParseException ex) {
            this.skyblock.sendMessage("&cFailed to load &8merchants.json&c: " + ex.getMessage());
        }
    }

    public void registerMerchant(String id, Merchant merchant) {
        this.merchants.put(id, merchant);

        if (!Skyblock.getPlugin().isEnabled()) {
            Skyblock.getPlugin().sendMessage("&cFailed to register merchant &8" + id + "&c: plugin is not enabled");
            return;
        }
        Bukkit.getPluginManager().registerEvents(merchant, Skyblock.getPlugin(Skyblock.class));
    }

    public Merchant getMerchant(String name) {
        return this.merchants.get(name);
    }

    public HashMap<String, Merchant> getMerchants() {
        return this.merchants;
    }

    public MerchantSellPriceHandler getPriceHandler() { return merchantSellPriceHandler; }
}
