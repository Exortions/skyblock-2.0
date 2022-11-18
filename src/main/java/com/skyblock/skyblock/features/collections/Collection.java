package com.skyblock.skyblock.features.collections;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.chat.ChatMessageBuilder;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class Collection {

    private final HashMap<Integer, Integer> levelToExp;
    private final CollectionRewards rewards;
    private final Material material;
    private final String category;
    private final int maxLevel;
    private final String name;

    public Collection(String name, Material material, String category, int maxLevel, CollectionRewards rewards, int... levelToExp) {
        this.maxLevel = maxLevel;
        this.material = material;
        this.category = category;
        this.name = name;

        this.levelToExp = new HashMap<>();

        this.rewards = rewards;

        for (int i = 0; i < levelToExp.length; i++) this.levelToExp.put(i, levelToExp[i]);
    }

    public void collect(Player player, int amount) {
        SkyblockPlayer skyblockPlayer = new SkyblockPlayer(player.getUniqueId());

        int level = (int) skyblockPlayer.getValue("collection." + this.name.toLowerCase() + ".level");
        int exp = (int) skyblockPlayer.getValue("collection." + this.name.toLowerCase() + ".exp");

        int newExp = exp + amount;

        skyblockPlayer.setValue("collection." + this.name.toLowerCase() + ".exp", newExp);

        if (newExp >= levelToExp.get(level + 1)) {
            this.rewards.reward(player, level);

            skyblockPlayer.setValue("collection." + this.name.toLowerCase() + ".level", level + 1);
            skyblockPlayer.setValue("collection." + this.name.toLowerCase() + ".exp", 0);

            ChatMessageBuilder builder = new ChatMessageBuilder();

            builder
                    .add("&e&l&m================================")
                    .add("&6&l  COLLECTION LEVEL UP &e" + StringUtils.capitalize(this.name.toLowerCase()) + " &8" + Util.toRoman(level + 1) + " ➜ &e" + Util.toRoman(level + 2))
                    .add("")
                    .add("&a&l  REWARDS");

            for (String s : this.rewards.stringify(level + 1)) builder.add(s);

            builder.add("&e&l&m================================");

            builder.build(player);
        }
    }

    public static List<Collection> getCollections() {
        List<Collection> collections = new ArrayList<>();

        collections.add(new Collection("Carrot", Material.CARROT_ITEM, "Farming", 9, new CollectionRewards(new CollectionRewards.Reward(ChatColor.BLUE + "Carrot Minion " + ChatColor.GRAY + " Recipes", "sb recipe carrot_minion", 1), new CollectionRewards.Reward(ChatColor.GREEN + "Simple Carrot Candy" + ChatColor.GRAY + " Recipe", "sb recipe simple_carrot_candy", 2)), 100, 250, 500, 1700, 5000, 10000, 25000, 50000, 100000));

        return collections;
    }

}
