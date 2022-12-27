package com.skyblock.skyblock.features.trades;

import com.skyblock.skyblock.Skyblock;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TradeHandler {

    private final List<Trade> trades;

    public TradeHandler(Skyblock plugin) {
        this.trades = this.index(plugin, new File(plugin.getDataFolder(), "trades.json"));
    }

    public List<Trade> index(Skyblock plugin, File file) {
        List<Trade> list = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
            Object obj = parser.parse(bufferedReader);

            JSONArray trades = (JSONArray) ((JSONObject) obj).get("trades");

            for (Object trade : trades) {
                String item = (String) ((JSONObject) trade).get("item");
                int amount = ((Long) ((JSONObject) trade).get("amount")).intValue();

                JSONObject cost = (JSONObject) ((JSONObject) trade).get("cost");
                String type = (String) cost.get("type");
                int costAmount = 0;
                String costItem = null;

                if (type.equals("coin")) {
                    costAmount = ((Long) cost.get("amount")).intValue();
                } else if (type.equals("item")) {
                    costAmount = ((Long) cost.get("amount")).intValue();
                    costItem = (String) cost.get("item");
                }

                list.add(new Trade(item, amount, type, costItem, costAmount));
            }
        } catch (Exception ex) {
            plugin.sendMessage("&cCould not index &8trades.json&c: &8" + ex.getMessage() + "&c!");
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }

        return list;
    }

}
