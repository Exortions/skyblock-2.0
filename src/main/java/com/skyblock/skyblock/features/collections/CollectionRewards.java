package com.skyblock.skyblock.features.collections;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionRewards {

    @Data
    static class Reward {

        private final String name;
        private final String command;
        private final int tier;

    }

    private final List<Reward> rewards;

    public CollectionRewards(Reward... rewards) {
        this.rewards = Arrays.asList(rewards);
    }

    public List<Reward> getRewards() {
        return this.rewards;
    }

    public List<Reward> getReward(int tier) {
        return this.rewards.stream().filter(reward -> reward.getTier() == tier).collect(Collectors.toList());
    }

    public void reward(Player player, int reward) {
        for (Reward r : this.getReward(reward)) {
            if (r.getCommand() == null) return;

            player.performCommand(r.getCommand());
        }
    }

    public List<String> stringify(int tier) {
        List<String> list = new ArrayList<>();

        for (Reward reward : this.getReward(tier)) {
            for (String s : reward.getName().split("\n  ")) {
                list.add("&7  " + s);
            }
        }

        return list;
    }

}
