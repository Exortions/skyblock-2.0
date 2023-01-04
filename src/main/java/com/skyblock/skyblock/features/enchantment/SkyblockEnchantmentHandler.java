package com.skyblock.skyblock.features.enchantment;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.enchantment.types.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.Range;
import org.bukkit.Bukkit;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SkyblockEnchantmentHandler {

    private final List<SkyblockEnchantment> enchantments = new ArrayList<>();
    private final List<ModdedLevel> levels = new ArrayList<>();
    private final Skyblock skyblock;

    public SkyblockEnchantmentHandler(Skyblock skyblock) {
        this.skyblock = skyblock;
        init();
    }

    @Getter
    @AllArgsConstructor
    static class ModdedLevel {
        private String name;
        private long weight;
        private HashMap<Integer, Range<Integer>> ranges;

        @Override
        public String toString() {
            return String.format("ModdedLevel[name=%s,weight=%s,ranges=%s]", name, weight, ranges);
        }
    }

    private void init() {
        try {
            JSONParser parser = new JSONParser();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(new File(skyblock.getDataFolder() + File.separator + "enchants.json").toPath()), StandardCharsets.UTF_8));
            Object obj = parser.parse(bufferedReader);

            JSONObject jsonObject = (JSONObject) obj;
            JSONArray enchants = (JSONArray) jsonObject.get("enchants");

            for (Object e : enchants) {
                JSONObject enchant = (JSONObject) e;
                String name = (String) enchant.get("name");
                long weight = (long) enchant.get("weight");

                HashMap<Integer, Range<Integer>> l = new HashMap<>();
                JSONArray ls = (JSONArray) enchant.get("levels");

                String prev = "";
                for (Object lev : ls) {
                    String level = (String) lev;

                    if (level.contains("max")) {
                        Integer min = Integer.parseInt(prev.split("min_")[1]);
                        Integer max = Integer.parseInt(level.split("max_")[1]);

                        l.put(Integer.parseInt(level.split("_max")[0]), Range.between(min, max));
                    }

                    prev = level;
                }

                levels.add(new ModdedLevel(name, weight, l));
            }
        } catch (ParseException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<ModdedLevel> getLevels() {
        return levels;
    }

    public void registerEnchantment(SkyblockEnchantment enchantment) {
        enchantments.add(enchantment);

        Bukkit.getPluginManager().registerEvents(enchantment, skyblock);
    }

    public SkyblockEnchantment getEnchantment(String name) {
        for (SkyblockEnchantment enchantment : enchantments) {
            if (enchantment.getName().replace(" ", "_").equalsIgnoreCase(name)) return enchantment;
        }

        return null;
    }

    public List<SkyblockEnchantment> getEnchantments() {
        return enchantments;
    }

    public List<SkyblockEnchantment> getEnchantments(String type) {
        List<Class<? extends SkyblockEnchantment>> instancesToCheck = new ArrayList<>(Collections.singletonList(MiscEnchantment.class));

        if (type.equalsIgnoreCase("armor") || type.equalsIgnoreCase("helmet") || type.equalsIgnoreCase("chestplate") || type.equalsIgnoreCase("leggings") || type.equalsIgnoreCase("boots")) {
            instancesToCheck.add(ArmorEnchantment.class);
        } else if (type.equalsIgnoreCase("bow")) {
            instancesToCheck.add(BowEnchantment.class);
        } else if (type.equalsIgnoreCase("fishing rod")) {
            instancesToCheck.add(FishingRodEnchantment.class);
        } else if (type.equalsIgnoreCase("sword")) {
            instancesToCheck.add(SwordEnchantment.class);
        } else if (type.equalsIgnoreCase("hoe") || type.equalsIgnoreCase("axe") || type.equalsIgnoreCase("pickaxe") || type.equalsIgnoreCase("shovel"))
            instancesToCheck.add(ToolEnchantment.class);

        if (type.equalsIgnoreCase("boots")) {
            instancesToCheck.add(BootEnchantment.class);
        }

        if (type.equalsIgnoreCase("helmet")) {
            instancesToCheck.add(HeadEnchantment.class);
        }

        return enchantments.stream().filter(enchantment -> instancesToCheck.stream().anyMatch(instance -> instance.isInstance(enchantment))).collect(Collectors.toList());
    }

}
