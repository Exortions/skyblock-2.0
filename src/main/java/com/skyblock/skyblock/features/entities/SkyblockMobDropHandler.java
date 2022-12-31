package com.skyblock.skyblock.features.entities;

import com.skyblock.skyblock.Skyblock;
import net.minecraft.server.v1_8_R3.Item;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
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

public class SkyblockMobDropHandler {

    private static final HashMap<String, List<EntityDrop>> loot_tables = new HashMap<>();
    private static final HashMap<String, Integer> coins = new HashMap<>();

    public SkyblockMobDropHandler() {
        init();
    }

    private void init() {
        File folder = new File(Skyblock.getPlugin().getDataFolder() + File.separator + "mobLoot");

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            try {
                JSONParser parser = new JSONParser();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
                Object obj = parser.parse(bufferedReader);

                JSONObject jsonObject = (JSONObject) obj;

                String entityName = file.getName().replace(".json", "");

                if (!loot_tables.containsKey(entityName)) loot_tables.put(entityName, new ArrayList<>());

                JSONArray pools = (JSONArray) jsonObject.get("pools");

                for (Object pool : pools) {
                    JSONArray entries = (JSONArray) ((JSONObject) pool).get("entries");

                    for (Object e : entries) {
                        JSONObject entry = (JSONObject) e;

                        String type = (String) entry.get("name");

                        for (Object f : (JSONArray) entry.get("functions")) {
                            JSONObject function = (JSONObject) f;
                            String fType = (String) function.get("function");

                            if (type.equals("minecraft:scute")) {
                                String tag = (String) function.get("tag");
                                String coin = tag.split("BaseCoins:")[1].replace("}", "");

                                coins.put(entityName, Integer.parseInt(coin));
                            } else {
                                if (!fType.equals("minecraft:set_count")) continue;

                                Object count = function.get("count");

                                double min = 1;
                                double max = 1;

                                if (count instanceof JSONObject) {
                                    JSONObject jsonCount = (JSONObject) count;
                                    min = (double) jsonCount.get("min");
                                    max = (double) jsonCount.get("max");
                                } else {
                                    min = (long) count;
                                    max = (long) count;
                                }

                                Item nms = Item.d(type);
                                ItemStack item = CraftItemStack.asNewCraftStack(nms);

                                List<EntityDrop> drops = loot_tables.get(entityName);
                                drops.add(new EntityDrop(item, EntityDropRarity.COMMON, 100, min, max));
                                loot_tables.put(entityName, drops);
                            }
                        }
                    }
                }

                bufferedReader.close();
            } catch (ParseException | IOException ex) {
                ex.printStackTrace();
                return;
            }
        }
    }

    public List<EntityDrop> getDrops(String name) {
        return loot_tables.get(name);
    }

    public int getCoins(String name) {
        if (coins.containsKey(name)) return coins.get(name);
        return 0;
    }
}
