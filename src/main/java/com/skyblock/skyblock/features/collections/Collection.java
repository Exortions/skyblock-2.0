package com.skyblock.skyblock.features.collections;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerCollectionRewardEvent;
import com.skyblock.skyblock.features.crafting.gui.CraftingGUI;
import com.skyblock.skyblock.utilities.Constants;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.chat.ChatMessageBuilder;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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

@Getter
public class Collection {

    public static final HashMap<String, List<String>> recipes = new HashMap<String, List<String>>() {{
        put("Farming", new ArrayList<>());
        put("Mining", new ArrayList<>());
        put("Combat", new ArrayList<>());
        put("Foraging", new ArrayList<>());
        put("Fishing", new ArrayList<>());
    }};
    private final HashMap<Integer, Integer> levelToExp;
    private final CollectionRewards rewards;
    private final Material material;
    private final String category;
    private final int maxLevel;
    private final String name;
    private final short data;

    public static boolean INITIALIZED = false;
    private static final List<Collection> collections = new ArrayList<>();

    public static class CollectionInitializationException extends Exception {
        public CollectionInitializationException(String message) {
            super(message);
        }

        public CollectionInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public Collection(String name, Material material, short data, String category, int maxLevel, CollectionRewards rewards, int... levelToExp) {
        this.maxLevel = maxLevel;
        this.material = material;
        this.category = category;
        this.data = data;
        this.name = name;

        this.levelToExp = new HashMap<>();

        this.rewards = rewards;

        for (int i = 0; i < levelToExp.length; i++) this.levelToExp.put(i, levelToExp[i]);
    }

    public boolean collect(Player player, int amount, ItemStack stack) {
        NBTItem item;

        try {
            item = new NBTItem(stack);
        } catch (Exception ex) {
            return false;
        }

        if (item.hasKey("collected") || item.getBoolean("collected").equals(true)) {
            return false;
        }

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        int level = (int) skyblockPlayer.getValue("collection." + this.name.toLowerCase() + ".level");
        int exp = (int) skyblockPlayer.getValue("collection." + this.name.toLowerCase() + ".exp");

        int newExp = exp + amount;

        skyblockPlayer.setValue("collection." + this.name.toLowerCase() + ".exp", newExp);

        if (skyblockPlayer.getValue("collection." + this.name.toLowerCase() + ".unlocked").equals(false)) {
            skyblockPlayer.setValue("collection." + this.name.toLowerCase() + ".unlocked", true);

            skyblockPlayer.setValue("collection.unlocked", skyblockPlayer.getIntValue("collection.unlocked") + 1);

            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "  COLLECTION UNLOCKED " + ChatColor.YELLOW + this.name);

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 2);
            return true;
        }

        if ((level < 1 && newExp >= levelToExp.get(0)) || newExp >= levelToExp.get(level)) {
            this.rewards.reward(player, level);

            skyblockPlayer.setValue("collection." + this.name.toLowerCase() + ".level", level + 1);

            ChatMessageBuilder builder = new ChatMessageBuilder();

            builder
                    .add("&e&l" + Constants.COLLECTION_SEPERATOR)
                    .add("&6&l  COLLECTION LEVEL UP &e" + WordUtils.capitalize(this.name.toLowerCase()) + " " + (level == 0 ? "" : "&8" + (Util.toRoman(level)) + " ➜ &e") + Util.toRoman(level + 1))
                    .add("")
                    .add("&a&l  REWARDS");

            for (String s : this.rewards.stringify(level + 1)) {
                builder.add(Util.buildLore("  " + s.replace("  \n", "    \n")));
                Bukkit.getPluginManager().callEvent(new SkyblockPlayerCollectionRewardEvent(skyblockPlayer, s.substring(4)));
            }

            if (this.rewards.stringify(level + 1).size() == 0) builder.add("    &cComing soon");

            builder.add("&e&l" + Constants.COLLECTION_SEPERATOR);

            builder.build(player);

            List<String> recipeRewards = new ArrayList<>();
            List<String> unlocked = (List<String>) skyblockPlayer.getValue("recipes.unlocked");

            for (String reward : rewards.stringify(level + 1)) {
                String stripColor = ChatColor.stripColor(reward);
                if (reward.endsWith("Recipe")) {
                    String result = stripColor.replace(" Recipe", "").toUpperCase().replaceAll(" ", "_");
                    result = result.replace("__", "");
                    result = result.substring(2);

                    recipeRewards.add(result);
                }
            }

            unlocked.addAll(recipeRewards);
            skyblockPlayer.setValue("recipes.unlocked", unlocked);

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        }

        return true;
    }

    public static void initializeCollections(Skyblock skyblock) throws CollectionInitializationException {
        skyblock.sendMessage("Registering collections...");
        long start = System.currentTimeMillis();

        Collection.INITIALIZED = true;

        File folder = new File(skyblock.getDataFolder() + File.separator + "collections");

        if (!folder.exists() && !folder.mkdirs()) {
            skyblock.sendMessage("&cFailed to initialize collections: could not create folder &8collections");
            Collection.INITIALIZED = false;
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)));
                JSONObject object = (JSONObject) obj;

                String category = (String) object.get("category");

                boolean found = false;

                for (CollectionCategory cat : Collection.getCollectionCategories()) {
                    if (cat.getName().equalsIgnoreCase(category)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    skyblock.sendMessage("&cFailed to initialize collection file &8" + file.getName() + "&c: category &8" + category + "&c does not exist");
                    continue;
                }

                JSONArray collections = (JSONArray) object.get("collections");

                for (Object collectionObject : collections) {
                    JSONObject collection = (JSONObject) collectionObject;

                    String name = (String) collection.get("name");
                    Material material = Material.valueOf((String) collection.get("material"));
                    short data = ((Long) collection.get("data")).shortValue();

                    JSONArray rewardsList = (JSONArray) collection.get("rewards");
                    HashMap<Integer, String> rewards = new HashMap<>();

                    int r = 0;

                    int[] levelToExp = new int[rewardsList.size()];

                    for (Object reward : rewardsList) {
                        String[] split = ((String) reward).split(";");
                        rewards.put(r, split[1]);

                        levelToExp[r] = Integer.parseInt(split[0]);

                        r++;
                    }

                    JSONArray commandsList = (JSONArray) collection.get("commands");
                    HashMap<Integer, String> commands = new HashMap<>();

                    int cmd = 0;

                    for (Object command : commandsList) {
                        commands.put(cmd, (String) command);

                        cmd++;
                    }

                    CollectionRewards.Reward[] rewardsArray = new CollectionRewards.Reward[rewards.size()];

                    for (int i = 0; i < rewards.size(); i++) {
                        int level = i + 1;

                        String reward = ChatColor.translateAlternateColorCodes('&', rewards.get(i));

                        String command = commands.get(i);

                        rewardsArray[i] = new CollectionRewards.Reward(reward, command, level);
                    }

                    List<String> requirements = new ArrayList<>();
                    List<String> collectionRecipes = recipes.get(category);

                    for (CollectionRewards.Reward reward : rewardsArray) {
                        for (String s : reward.getName().split("\n  ")) {
                            String stripColor = ChatColor.stripColor(s);
                            if (!stripColor.endsWith("Recipe")) continue;
                            String result = stripColor.replace(" Recipe", "").toUpperCase().replaceAll(" ", "_");
                            result = result.replace("__", "");

                            requirements.add(result);
                        }
                    }

                    collectionRecipes.addAll(requirements);
                    CraftingGUI.needsUnlocking.addAll(requirements);

                    recipes.put(category, collectionRecipes);

                    CollectionRewards collectionRewards = new CollectionRewards(rewardsArray);

                    Collection generated = new Collection(name, material, data, category, levelToExp.length, collectionRewards, levelToExp);

                    Collection.collections.add(generated);
                }
            } catch (ParseException | IOException ex) {
                throw new CollectionInitializationException("Failed to initialize collection file " + file.getName(), ex);
            }
        }

        skyblock.sendMessage("Successfully registered " + ChatColor.GREEN + Collection.collections.size() + ChatColor.WHITE + " collections [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public static List<Collection> getCollections() {
        if (Collection.INITIALIZED) return Collection.collections;

        throw new RuntimeException("Collections not initialized");
    }

    public static List<CollectionCategory> getCollectionCategories() {
        List<CollectionCategory> categories = new ArrayList<>();

        categories.add(new CollectionCategory("Farming", Material.GOLD_HOE));
        categories.add(new CollectionCategory("Mining", Material.STONE_PICKAXE));
        categories.add(new CollectionCategory("Combat", Material.STONE_SWORD));
        categories.add(new CollectionCategory("Foraging", Material.SAPLING, (short) 3));
        categories.add(new CollectionCategory("Fishing", Material.FISHING_ROD));

        return categories;
    }

}
