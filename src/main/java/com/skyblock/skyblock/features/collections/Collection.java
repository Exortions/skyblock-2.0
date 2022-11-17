package com.skyblock.skyblock.features.collections;

import com.skyblock.skyblock.SkyblockPlayer;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class Collection {

    private final HashMap<Integer, Integer> levelToExp;
    private final Material material;
    private final String category;
    private final int maxLevel;
    private final String name;

    public Collection(String name, Material material, String category, int maxLevel, int... levelToExp) {
        this.maxLevel = maxLevel;
        this.material = material;
        this.category = category;
        this.name = name;

        this.levelToExp = new HashMap<>();

        for (int i = 0; i < levelToExp.length; i++) this.levelToExp.put(i, levelToExp[i]);
    }

    public void collect(Player player, int amount) {
        SkyblockPlayer skyblockPlayer = new SkyblockPlayer(player.getUniqueId());

        int level = (int) skyblockPlayer.getValue("collection." + this.name.toLowerCase() + ".level");
        int exp = (int) skyblockPlayer.getValue("collection." + this.name.toLowerCase() + ".exp");

        int newExp = exp + amount;

        skyblockPlayer.setValue("collection." + this.name.toLowerCase() + ".exp", newExp);

        if (newExp >= levelToExp.get(level + 1)) {
            skyblockPlayer.setValue("collection." + this.name.toLowerCase() + ".level", level + 1);
            skyblockPlayer.setValue("collection." + this.name.toLowerCase() + ".exp", 0);

            skyblockPlayer.getBukkitPlayer().sendMessage("§aYour " + this.name.toLowerCase() + " collection level is now " + (level + 1) + "!");
        }

        player.sendMessage("You collected " + amount + " " + material.name() + "!");
    }

    public static List<Collection> getCollections() {
        List<Collection> collections = new ArrayList<>();

        collections.add(new Collection("Carrot", Material.CARROT_ITEM, "Farming", 9, 100, 250, 500, 1700, 5000, 10000, 25000, 50000, 100000));

        return collections;
    }

}
