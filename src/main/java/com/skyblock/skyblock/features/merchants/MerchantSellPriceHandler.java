package com.skyblock.skyblock.features.merchants;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
import java.util.HashMap;

public class MerchantSellPriceHandler {

    private final HashMap<String, Double> prices;

    public MerchantSellPriceHandler() {
        this.prices = new HashMap<>();

        init();
    }

    private void init() {
        try {
            File file = new File(Skyblock.getPlugin().getDataFolder(), "prices.json");

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)));

            JSONArray prices = (JSONArray) object.get("prices");

            for (Object item : prices) {
                String str = (String) item;
                String[] split = str.split(": ");

                String name = split[0];
                Double price = Double.parseDouble(split[1]);

                this.prices.put(name, price);
            }
        } catch (IOException | NumberFormatException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public double getPrice(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (!meta.hasDisplayName()) return 0.0;

        String name = StringUtils.capitalize(ChatColor.stripColor(meta.getDisplayName()));

        return prices.get(name);
    }
}
