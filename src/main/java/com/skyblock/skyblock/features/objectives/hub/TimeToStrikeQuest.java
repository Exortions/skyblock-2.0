package com.skyblock.skyblock.features.objectives.hub;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockEntityDeathEvent;
import com.skyblock.skyblock.features.entities.zombie.Zombie;
import com.skyblock.skyblock.features.npc.NPC;
import com.skyblock.skyblock.features.npc.NPCHandler;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TimeToStrikeQuest extends QuestLine {

    public TimeToStrikeQuest() {
        super("time_to_strike", "Time to Strike", new TalkToBartenderObjective(), new KillZombiesObjective(),
                new TalkToBartenderAgainObjective(), new TravelToSpidersDenObjective(), new TalkToHaymitchObjective(), new CollectStringObjective(), new TalkToHaymitchAgainObjective());

        NPCHandler handler = Skyblock.getPlugin().getNpcHandler();

        handler.registerNPC("bartender", new NPC(
                "Bartender",
                true,
                true,
                false,
                null,
                new Location(Skyblock.getSkyblockWorld(), -85.5, 70, -70, 90, 0),
                (player) -> {
                    if (!(boolean) SkyblockPlayer.getPlayer(player).getValue("quests.time_to_strike.bartender.interacted")) {
                        if ((int) SkyblockPlayer.getPlayer(player).getValue("quests.time_to_strike.zombiesKilled") > 9) {
                            SkyblockPlayer.getPlayer(player).setValue("quests.time_to_strike.bartender.interacted", true);
                            Util.sendDelayedMessages(player, "Bartender", (p) -> {
                                        getObjective(SkyblockPlayer.getPlayer(p)).complete(p);
                                        SkyblockPlayer.getPlayer(player).addCoins(100);
                                    },
                                    "Words cannot describe how thankful I am!",
                                    "That whole area is very dangerous, but can be quite rewarding for a warrior such as yourself.",
                                    " If you're up for the challenge, both the " + ChatColor.RED + " Graveyard " + ChatColor.WHITE + " and the " + ChatColor.RED + " Spider's Den " + ChatColor.WHITE + " beyond it are great training grounds for improving your Combat Skill.",
                                    "For now, here's a reward for helping me out!"
                            );
                        } else {
                            SkyblockPlayer.getPlayer(player).setValue("quests.time_to_strike.bartender.interacted", true);
                            Util.sendDelayedMessages(player, "Bartender", (p) -> {
                                        getObjective(SkyblockPlayer.getPlayer(p)).complete(p);
                                    },
                                    "Welcome to the Bar, friend!",
                                    "These are tying times, indeed. The " + ChatColor.RED + "Graveyard" + ChatColor.WHITE + " is overflowing with monsters! Anyone who comes in is spooked off by the grunts of zombies in the distance.",
                                    "Could you give me a hand? If you help clear out some of these monsters, I'll pay you for it."
                            );
                        }
                    }
                },
                "ewogICJ0aW1lc3RhbXAiIDogMTYwODIxODk0MDY5NiwKICAicHJvZmlsZUlkIiA6ICI3MzgyZGRmYmU0ODU0NTVjODI1ZjkwMGY4OGZkMzJmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJJb3lhbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xYzkyNTgwODcwNzRlYjEzMTRkN2EwZGQzM2QxZmNiNWNlYmYzZjZmMWE0ZThkMjM1NzIxYmMyNTliNGU0OTZhIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                "XV4wRQHNf+t8UNPxyCQWe9OTKABW2H2q8dKQJD6opc/UpjN8Ho5BZkqeCbCJ0Zdkq6YVzyQTctxOAVx99gi7FCUmtT02Z5lujim8zSemuzAN5ndYvHOBAjOJL51sbftnuGoCPBklmEAJ4uWWl+77mHe2GfXZkHTrBw0yvw777u2vtA8QJwoq2eh/8OPUFWSRtJVeW9kIggwfjJbVYjP7w1im5DKklvL7Tw71TuRx+1VebWhpD3lOTtfq1Vo6ri+LOs4o36Ix/Ec2xnmjeV2BF0CK6gkIbzaMcF4efFHxonmW2GRXL+E/tIpvAm4sY5JR5z/jV4Mp6qEN0CaU5WR8DSdkwLMTrRGuzRoZUjvZL2B6kZ7yaVmpOo7PeNVAr8hPRjAB489qJVLDawfpVCNt4jgQMMDBJUPk8F4DJPBaUMFZXNM4BO9B6DH6xVHNsaZhOZZu5tKKyXBg0yuT7FA1OgaFcye8z+JSIDHNd3kxcR02idHDmI1pDL6da2pdPoAmz19I5Ao7rI9kXPMdJUPmY7aIEd4j6RXXXnJ3UqUcKnDQqf3ElwSbZXayTo/Wn6P9KFa2aTjR/gfvIjf7+Jn9vyVGtbFG8x+xg1oSZR4RE2rmOhHQKEETaXakqbRMWUt1EHWm4c/HpxpxuRSNvFAwkvdrV4mt8VrCo0x+A/Z/3cQ="
        ));

        handler.registerNPC("haymitch", new NPC("Haymitch",
                true,
                true,
                false,
                null,
                new Location(Skyblock.getSkyblockWorld(), -203, 84, -238),
                (p) -> {
                    SkyblockPlayer player = SkyblockPlayer.getPlayer(p);
                    if (getObjective(player).getId().equals("talk_to_haymitch_again")) {
                        Util.sendDelayedMessages(p, "Haymitch", (player1) -> {
                                    getObjective(player).complete(player1);
                                    Skill.reward(Objects.requireNonNull(Skill.parseSkill("Combat")), 15, player);
                                },
                                "A true warrior will have no trouble here, but the Blazing Fortress is another story.",
                                "You will face great perils - magma cubes, wither skeletons, and...lava.",
                                "Reach the top of the mountain to find the gateway to the Blazing Fortress!"
                        );
                    } else if (getObjective(player).getId().equals("talk_to_haymitch")) {
                        Util.sendDelayedMessages(p, "Haymitch", (player1) -> {
                                    getObjective(SkyblockPlayer.getPlayer(p)).complete(p);
                                },
                                "Easy there, " + p.getName() + " Lots of spiders creeping about!",
                                "Personally I love them, collecting string is my passion.",
                                "You should try it!"
                        );
                    } else if (getObjective(player).getId().equals("collect_string_and_spider_eyes")) {
                        Util.sendDelayedMessages(p, "Haymitch",
                                "Go collect some string and spider eye!",
                                "I love collecting string, it's so versatile.",
                                "And spider eyes have proven quite useful in my brewing hobby!",
                                "Go see for yourself..."
                        );
                    }
                },
                "ewogICJ0aW1lc3RhbXAiIDogMTYxMzI5ODY4ODA4NCwKICAicHJvZmlsZUlkIiA6ICJjNTBhZmE4YWJlYjk0ZTQ1OTRiZjFiNDI1YTk4MGYwMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUd29FQmFlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJiNWIzZDc3YTI0MzFjMjA0Nzk5MDM5YTU4MWMwODBkNTM2NjgxNWFhNGQ0ZmZkYzkwMDhlMGQ1Y2QxMWIyM2UiCiAgICB9CiAgfQp9",
                "IFJRcyPocDaU9BlgD0CfVzOi9B1LONhim7xBcx7wrm7jlX/558b+lhm4FZ5fQz+OhBisWVj/Z863lb+crnIEGEbprefhsB4UjCQSKnXNt/SIEji21JcdQxdcoVhBcFMIMAwwkUo+8dCRgceFZuWKM9dND/x5M6X3BfgzJ3IFwiybofS7iRyTITI9BayB7GzYb0gdM9SNhcnbsapx9QxdoZtxTeckyk3F977ROYJhrYu6+dOkJK5X8Wn45cG/0cdI3dkMx6u637QWmnz4cfMPpfl2KrBAfEbucEb8H7wenU7YM8c/5jIeWv30w16cbRTWNGv4bFz5ty31xqSqWX/NTvFm948ikT6mXU65nlFAfx9mOzL5UpD+3TFi5WEbw7UI/53n/q9l04EUmkbFPe75clvzQ+QHjENoDUx/jwhvFZCqW78r16mTMkSs+IQzV9UKHKzNFApuMGKm1cGhGTPvy1ehZrkAV/Sh0XR5KE1vqvIjbH2wwMc47RGOzjWbTA70DfMccdTdiGVEILTA84f/qJEHvPZhcmo+bfeB+Uofu/GOXAFgVroHWKimviRAnAbJPtka+aAAtMoR/WMTvYFQC4WaMraf2fFA5OkjiyiJ54G5RXyVh6J8+d9W2tnhdLC8iuM9EfSqdvPOx6Ub6276MhMPfaj8OvZ+qAFcGZEd3Gs="));
    }

    @Override
    protected boolean hasCompletionMessage() {
        return true;
    }

    @Override
    protected List<String> getRewards() {
        return Arrays.asList(ChatColor.DARK_GRAY + "+" + ChatColor.GOLD + "100 Coins", ChatColor.DARK_GRAY + "+" + ChatColor.DARK_AQUA + "15 Combat Exp");
    }

    private static class TalkToBartenderObjective extends Objective {
        public TalkToBartenderObjective() {
            super("talk_to_bartender", "Talk to the Bartender");
        }
    }

    private static class TalkToBartenderAgainObjective extends Objective {
        public TalkToBartenderAgainObjective() {
            super("talk_to_bartender_again", "Talk to the Bartender");
        }
    }

    private static class KillZombiesObjective extends Objective {
        public KillZombiesObjective() {
            super("kill_zombies", "Kill Zombies");
        }

        @EventHandler
        public void onDeath(SkyblockEntityDeathEvent e) {
            if (e.getKiller() == null) return;
            if (!isThisObjective(e.getKiller().getBukkitPlayer())) return;
            if (e.getEntity() instanceof Zombie) {
                int zombies = (int) e.getKiller().getValue("quests.time_to_strike.zombiesKilled");
                e.getKiller().setValue("quests.time_to_strike.zombiesKilled", zombies + 1);
                e.getKiller().setValue("quests.time_to_strike.bartender.interacted", false);

                if (zombies == 9) complete(e.getKiller().getBukkitPlayer());
            }
        }

        @Override
        public String getSuffix(SkyblockPlayer player) {
            return ChatColor.translateAlternateColorCodes('&', "&7(&e" + player.getValue("quests.time_to_strike.zombiesKilled") + "&7/&a10&7)");
        }
    }

    private static class TravelToSpidersDenObjective extends Objective {
        public TravelToSpidersDenObjective() { super("travel_to_spiders_den", "Travel to the Spiders Den"); }

        @EventHandler
        public void onMove(PlayerMoveEvent e) {
            if (!isThisObjective(e.getPlayer())) return;

            if (SkyblockPlayer.getPlayer(e.getPlayer()).getCurrentLocationName().equals("Spiders Den")) complete(e.getPlayer());
        }
    }

    private static class TalkToHaymitchObjective extends Objective {
        public TalkToHaymitchObjective() { super("talk_to_haymitch", "Talk to Haymitch"); }
    }

    private static class TalkToHaymitchAgainObjective extends Objective {
        public TalkToHaymitchAgainObjective() { super("talk_to_haymitch_again", "Talk to Haymitch"); }
    }

    private static class CollectStringObjective extends Objective {
        public CollectStringObjective() { super("collect_string_and_spider_eyes", "Collect String and Spider eyes");}

        @EventHandler
        public void onPickup(PlayerPickupItemEvent e) {
            if (!isThisObjective(e.getPlayer())) return;

            Util.delay(() -> {
                if (e.getPlayer().getInventory().contains(Material.STRING) &&
                    e.getPlayer().getInventory().contains(Material.SPIDER_EYE)) {
                    complete(e.getPlayer());
                }
            }, 1);
        }
    }
}
