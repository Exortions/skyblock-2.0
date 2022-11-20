package com.skyblock.skyblock.features.merchants;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import java.util.*;

public class MerchantHandler {

    private final HashMap<String, Merchant> merchants;
    private final Skyblock skyblock;

    public MerchantHandler(Skyblock skyblock) {
        this.merchants = new HashMap<>();
        this.skyblock = skyblock;

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

                String skin = (String) merchant.get("skin");

                JSONArray items = (JSONArray) merchant.get("items");

                List<MerchantItem> merchantItems = new ArrayList<>();

                for (Object itemObject : items) {
                    JSONObject item = (JSONObject) itemObject;

                    String command = (String) item.get("command");
                    int cost = ((Long) item.get("cost")).intValue();
                    int amount = ((Long) item.get("amount")).intValue();

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
                            cost
                    ));
                }

                JSONObject locationObject = (JSONObject) merchant.get("location");
                Location location = new Location(
                        Bukkit.getWorld((String) locationObject.get("world")),
                        (double) locationObject.get("x"),
                        (double) locationObject.get("y"),
                        (double) locationObject.get("z")
                );

                this.registerMerchant(id, new Merchant(
                        name,
                        skin,
                        merchantItems,
                        location
                ));
            }
        } catch (IOException | ParseException ex) {
            this.skyblock.sendMessage("&cFailed to load &8merchants.json&c: " + ex.getMessage());
        }
    }

    public void registerMerchant(String id, Merchant merchant) {
        this.merchants.put(id, merchant);

        Bukkit.getPluginManager().registerEvents(merchant, Skyblock.getPlugin(Skyblock.class));
    }

    public Merchant getMerchant(String name) {
        return this.merchants.get(name);
    }

    public HashMap<String, Merchant> getMerchants() {
        return this.merchants;
    }

}
