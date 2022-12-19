package com.skyblock.skyblock.features.objectives.impl.hub;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.npc.NPC;
import com.skyblock.skyblock.features.npc.NPCHandler;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.Collections;
import java.util.List;

public class IntroduceYourselfQuest extends QuestLine {

    public IntroduceYourselfQuest() {
        super("introduce_yourself", "Introduce Yourself", new TalkToVillagersObjective());
    }

    @Override
    public void onEnable() {
        Skyblock skyblock = Skyblock.getPlugin();
        NPCHandler npcHandler = skyblock.getNpcHandler();

        npcHandler.registerNPC("leo", new NPC("Leo", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), -5.5, 70, -89.5),
                (p) -> {
                    sendDelayedMessages(p, "Leo",
                    "You can unlock Leaflet Armor by progressing through your Oak Log Collection",
                            "There is a Forest west of the Village where you can gather Oak Logs",
                            "To check your Collection progress and rewards, open the Collection Menu in your Skyblock Menu");
                }, "", ""));

        npcHandler.registerNPC("tom", new NPC("Tom", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), -14.5, 70, -84.5),
                (p) -> {
                    sendDelayedMessages(p, "Tom",
                            "I will teach you the Promising Axe Recipe to get you started!",
                            "All Skyblock recipes can be found by opening the Recipe Book in your Skyblock Menu");
                }, "", ""));

        npcHandler.registerNPC("jamie", new NPC("Jamie", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), -17.5, 70, -67.5),
                (p) -> {
                    sendDelayedMessages(p, "Jamie",
                            "You might have noticed that you have a Mana bar!",
                            "Some items have mysterious properties called Abilities",
                            "Abilities use your Mana as a resource. Here take this Rogue Sword. I don't need it!");
                }, "", ""));

        npcHandler.registerNPC("andrew", new NPC("Andrew", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), 9.5, 70, -64.5),
                (p) -> {
                    sendDelayedMessages(p, "Andrew",
                            "If you find a bug or exploit, remember to report it on the forums!");
                }, "", ""));

        npcHandler.registerNPC("jack", new NPC("Jack", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), -1.5, 70, -66.5),
                (p) -> {
                    sendDelayedMessages(p, "Jack",
                            "Your SkyBlock Profile in your SkyBlock Menu shows details about your current stats!",
                            "Equipped armor, weapons, and accessories in your inventory all improve your stats.",
                            "Additionally, leveling your Skills can permanently boost some of your stats!",
                            "The higher your ❤ Health stat, the faster your health will regenerate!");
                }, "", ""));

        npcHandler.registerNPC("vex", new NPC("Vex", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), 12.5, 70, -85.5),
                (p) -> {
                    sendDelayedMessages(p, "Vex",
                            "You can shift click any player to trade with them!",
                            "Once both players are ready to trade, click on Accept trade!",
                            "You can trade with me if you want!");
                }, "", ""));

        npcHandler.registerNPC("stella", new NPC("Stella", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), 28.5, 70, -116.5),
                (p) -> {
                    sendDelayedMessages(p, "Stella",
                            "At any time you can create a Co-op with your friends",
                            "Simply go to your Skyblock Menu where you can find the Profile Menu",
                            "Enter /coop followed by the name of all the friends you want to invite!");
                }, "", ""));

        npcHandler.registerNPC("duke", new NPC("Duke", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), 1.5, 70, -103.5),
                (p) -> {
                    sendDelayedMessages(p, "Duke",
                            "Are you new here? As you can see there is a lot to explore!",
                            "My advice is to start by visiting the Farm, or the Coal Mine both North of here.",
                            "If you do need some wood, the best place to get some is West of the Village!");
                }, "", ""));

        npcHandler.registerNPC("felix", new NPC("Felix", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), -14.5, 70, -98.5),
                (p) -> {
                    sendDelayedMessages(p, "Felix",
                            "You can access your Ender Chest in your SkyBlock Menu.",
                            "Store items in this chest and access them at any time!");
                }, "", ""));

        npcHandler.registerNPC("lynn", new NPC("Lynn", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), 8.5, 70, -101.5),
                (p) -> {
                    sendDelayedMessages(p, "Lynn",
                            "If you ever get lost during a quest, open your Quest Log in your SkyBlock Menu!");
                }, "", ""));

        npcHandler.registerNPC("liam", new NPC("Liam", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), -35.5, 70, -97.5),
                (p) -> {
                    sendDelayedMessages(p, "Liam",
                            "One day those houses in the Village will be rentable for Coins!",
                            "Anyone will be able to rent them, players, co-ops, even Guilds!");
                }, "", ""));

        npcHandler.registerNPC("ryu", new NPC("Ryu", true, false, true, Villager.Profession.BUTCHER,
                new Location(Skyblock.getSkyblockWorld(), -6.5, 70, -118.5),
                (p) -> {
                    sendDelayedMessages(p, "Ryu",
                            "There are 7 Skills in Skyblock!",
                            "Farming, Mining, Foraging, Fishing, Combat, Enchanting and Alchemy",
                            "You can access yours skills through your Skyblock Menu");
                }, "", ""));
    }

    @Override
    protected List<String> getRewards() {
        return Collections.singletonList(ChatColor.DARK_GRAY + "+" + ChatColor.GOLD + 50 + " &7Coins");
    }

    @Override
    protected boolean hasCompletionMessage() {
        return true;
    }

    @Override
    protected void reward(SkyblockPlayer player) {
        player.addCoins(50);
    }

    private void sendDelayedMessages(Player player, String npc, String... messages) {
        List<String> talked = (List<String>) SkyblockPlayer.getPlayer(player).getValue("quests.introduceYourself.talkedTo");

        if (talked.contains(npc)) return;

        int i = 0;
        for (String message : messages) {
            sendDelayedMessage(player, npc, message, i);
            i++;
        }

        talked.add(npc);
        SkyblockPlayer.getPlayer(player).setValue("quests.introduceYourself.talkedTo", talked);

        if (talked.size() == 12) {
            complete(player);
        }
    }

    private void sendDelayedMessage(Player player, String npc, String message, int delay) {
        Util.delay(() -> {
            player.sendMessage(ChatColor.YELLOW + "[NPC] " + npc + ChatColor.WHITE + ": " + message);
            player.playSound(player.getLocation(), Sound.VILLAGER_YES, 10, 1);
        }, delay * 20);
    }

    private static class TalkToVillagersObjective extends Objective {

        public TalkToVillagersObjective() {
            super("talk_to_villagers", "Talk to the Villagers");
        }

        @Override
        public String getSuffix(SkyblockPlayer player) {
            return ChatColor.GRAY + "(" + ChatColor.YELLOW + ((List<String>) player.getValue("quests.introduceYourself.talkedTo")).size()
                    + ChatColor.GRAY + "/" + ChatColor.GREEN + "12" + ChatColor.GRAY + ")";
        }
    }
}
