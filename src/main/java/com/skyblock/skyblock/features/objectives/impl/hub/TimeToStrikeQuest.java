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
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;

public class TimeToStrikeQuest extends QuestLine {

    public TimeToStrikeQuest() {
        super("time_to_strike", "Time to Strike", new TalkToBartenderObjective(), new KillZombiesObjective());

        NPCHandler handler = Skyblock.getPlugin().getNpcHandler();

        handler.registerNPC("bartender", new NPC(
                "Bartender",
                true,
                true,
                false,
                null,
                new Location(Skyblock.getSkyblockWorld(), -86, 70, -70, 90, 0),
                (player) -> {
                    if (!(boolean) SkyblockPlayer.getPlayer(player).getValue("quests.time_to_strike.bartender.interacted")) {
                        SkyblockPlayer.getPlayer(player).setValue("quests.time_to_strike.bartender.interacted", true);
                        Util.sendDelayedMessages(player, "Bartender", (p) -> getObjective(SkyblockPlayer.getPlayer(p)).complete(p),
                                "Welcome to the Bar, friend!",
                                "These are tying times, indeed. The " + ChatColor.RED + "Graveyard" + ChatColor.WHITE + " is overflowing with monsters! Anyone who comes in is spooked off by the grunts of zombies in the distance.",
                                "Could you give me a hand? If you help clear out some of these monsters, I'll pay you for it."
                        );
                    }
                },
                "ewogICJ0aW1lc3RhbXAiIDogMTYwODIxODk0MDY5NiwKICAicHJvZmlsZUlkIiA6ICI3MzgyZGRmYmU0ODU0NTVjODI1ZjkwMGY4OGZkMzJmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJJb3lhbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xYzkyNTgwODcwNzRlYjEzMTRkN2EwZGQzM2QxZmNiNWNlYmYzZjZmMWE0ZThkMjM1NzIxYmMyNTliNGU0OTZhIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                "XV4wRQHNf+t8UNPxyCQWe9OTKABW2H2q8dKQJD6opc/UpjN8Ho5BZkqeCbCJ0Zdkq6YVzyQTctxOAVx99gi7FCUmtT02Z5lujim8zSemuzAN5ndYvHOBAjOJL51sbftnuGoCPBklmEAJ4uWWl+77mHe2GfXZkHTrBw0yvw777u2vtA8QJwoq2eh/8OPUFWSRtJVeW9kIggwfjJbVYjP7w1im5DKklvL7Tw71TuRx+1VebWhpD3lOTtfq1Vo6ri+LOs4o36Ix/Ec2xnmjeV2BF0CK6gkIbzaMcF4efFHxonmW2GRXL+E/tIpvAm4sY5JR5z/jV4Mp6qEN0CaU5WR8DSdkwLMTrRGuzRoZUjvZL2B6kZ7yaVmpOo7PeNVAr8hPRjAB489qJVLDawfpVCNt4jgQMMDBJUPk8F4DJPBaUMFZXNM4BO9B6DH6xVHNsaZhOZZu5tKKyXBg0yuT7FA1OgaFcye8z+JSIDHNd3kxcR02idHDmI1pDL6da2pdPoAmz19I5Ao7rI9kXPMdJUPmY7aIEd4j6RXXXnJ3UqUcKnDQqf3ElwSbZXayTo/Wn6P9KFa2aTjR/gfvIjf7+Jn9vyVGtbFG8x+xg1oSZR4RE2rmOhHQKEETaXakqbRMWUt1EHWm4c/HpxpxuRSNvFAwkvdrV4mt8VrCo0x+A/Z/3cQ="
        ));
    }

    private static class TalkToBartenderObjective extends Objective {
        public TalkToBartenderObjective() {
            super("talk_to_bartender", "Talk to the Bartender");
        }
    }

    private static class KillZombiesObjective extends Objective {
        public KillZombiesObjective() {
            super("kill_zombies", "Kill Zombies");
        }

        @Override
        public String getSuffix(SkyblockPlayer player) {
            return ChatColor.translateAlternateColorCodes('&', "&7(&e" + player.getValue("quests.time_to_strike.zombiesKilled") + "&7/&a10&7)");
        }
    }
}
