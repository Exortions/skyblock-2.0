package com.skyblock.skyblock.features.objectives.foraging;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerLogBreakEvent;
import com.skyblock.skyblock.features.npc.NPC;
import com.skyblock.skyblock.features.npc.NPCHandler;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.features.objectives.gui.GiftGui;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimberQuest extends QuestLine {

    public TimberQuest() {
        super("timber", "Timber!", new TalkToLumberjackObjective(), new CollectLogsObjective(), new ReturnToLumberjackObjective(),
                new TravelToParkObjective(), new CollectBirchLogObjective(), new TalkToCharlie());

        NPCHandler npcHandler = Skyblock.getPlugin().getNpcHandler();

        npcHandler.registerNPC("lumberjack", new NPC(
                "Lumberjack",
                true,
                true,
                false,
                null,
                new Location(Skyblock.getSkyblockWorld(), -113, 74, -37),
                (player) -> {
                    boolean talkedAlready = (boolean) SkyblockPlayer.getPlayer(player).getValue("quests.timber.talkedToLumberjack");

                    if (!talkedAlready) {
                        Util.sendDelayedMessages(player, "Lumberjack", (p) -> {
                            getObjective(SkyblockPlayer.getPlayer(p)).complete(p);
                            SkyblockPlayer.getPlayer(p).setValue("quests.timber.talkedToLumberjack", true);
                        }, "Timber!", "My woodcutting assistant has fallen quite ill! Do you think you could take over for him?", "I just need you to chop down some Logs. If you do, I'll even give you his old axe as a reward!");
                    } else {
                        if (getObjective(SkyblockPlayer.getPlayer(player)).getId().equals("collect_logs")) {
                            List<String> busyMessages = new ArrayList<String>() {{
                                add("A lumberjack always pays his debts!");
                                add("Bring me some Logs. You can chop them down in my Forest!");
                                add("Howdy, " + player.getName() + "!");
                            }};

                            Util.sendDelayedMessage(player, "Lumberjack", busyMessages.get(Skyblock.getPlugin().getRandom().nextInt(busyMessages.size())), 0);
                        }

                        if (getObjective(SkyblockPlayer.getPlayer(player)).getId().equals("return_to_lumberjack")) {
                            Util.sendDelayedMessages(player, "Lumberjack", (p) -> {
                                        new GiftGui(player, Skyblock.getPlugin().getItemHandler().getItem("SWEET_AXE.json")).show(player);
                                        getObjective(SkyblockPlayer.getPlayer(player)).complete(player);
                                    },
                                    "Thank you! Take this " + ChatColor.YELLOW + "Sweet Axe" + ChatColor.WHITE + ", it's so sweet that it drops apples from logs sometimes!",
                                    "You've got the knack for wood. Could you get some Birch Planks from The Park?",
                                    "My associate will be there waiting for you. He will reward you in Coins if you're up to the task!");
                        }
                    }
                },
                "ewogICJ0aW1lc3RhbXAiIDogMTU5MTQ2MzM5MzU5MywKICAicHJvZmlsZUlkIiA6ICJmMjljOTIyMmVjNmY0NjExOTc3YWNkMmFjYzExNDAxOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJJSVJleWRlbklJIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2NkMGFiZGQ5ZjZlZTdkODM3MGQyNjIxMDljYjg1M2JhY2QzZjQzZjhjZTQyNDExNjRkMDhlM2QyYTU2ODNlMzIiCiAgICB9CiAgfQp9",
                "kyxxya3FqBJo6onG3bC1BjsjrF4uWTDd7Qitz14iNFwvZOn6FsW1A7dkNiZmiR1nVBfex7b4XHYAb6f3jXx8wIEvoDzSjzVLkCkzbQ9aMTTlDSAvkZ/fqtgMJbbXnppUSETmbWm7fdPjr4P5J30+Mz5vb66kNYu8QXsWqQ36YxI6sS2P77+vLdq+n1l389Npw1uMBpMLPjXaTsjAMrq1U6bDbTj7YwYqtURh0hxJm6v3q9d+oXD989fvYu04DYtEiW6H3VDjtvoEAb+m3H9tlDt74SNVXlIJ0lGa6RNpidBhKgSS38F0P5nMo1XbHJ/FcrWP+UZ6D4rT5TuW0T1J7n+5q+/LMMOR2hofFHgdqmTD85tTOmTmKKtIBPW9yreEKNZg7Ah/s3jStnVosp6A9qkpTcmdneRJwL+ZvHcpCbZpJq9Ii/NV+cBNrL49ylVCDZnRN4I7xENYfAq/Xe241cs2bEErrpu9uDH7dXSnhwQn4PdtMN6ZZrr4IJS2sjAyPjcuN/A006gs3Cu+9Cb4MViRCdIgOZDsIV/C0yDqr3+/SgKa+GRp7qIiphIMgRXC03GDSd/btYea1g8qJhhnkAL5MStubm/rdPaEf8wBc3y8Kc4pdEemo5kHQ8SPGvrk+xI8sGWKmlf/bZkyXKw9Wdhg/npeptxjNb2rUuaIDvk="
        ));

        npcHandler.registerNPC("charlie", new NPC(
                "Charlie",
                true,
                true,
                false,
                null,
                new Location(Skyblock.getSkyblockWorld(), -286, 82, -16),
                (p) -> {
                    SkyblockPlayer player = SkyblockPlayer.getPlayer(p);
                    QuestLine quest = Skyblock.getPlugin().getQuestLineHandler().getFromPlayer(player);

                    String[] dialogue = new String[] { "Hello!", "Fine time to cut some wood!", "I'm quite forgetful, I tend to say the same thing over and over again." };
                    if (quest != null && quest.getObjective(player).getId().contains("talk_to_charlie")) {
                        Util.sendDelayedMessages(p, "Charlie", (player1 -> {
                            quest.getObjective(player).complete(p);
                        }), dialogue);
                    } else {
                        Util.sendDelayedMessages(p, "Charlie", dialogue);
                    }
                },
                "ewogICJ0aW1lc3RhbXAiIDogMTU5MDE1NjYzNDYzOCwKICAicHJvZmlsZUlkIiA6ICI5MWZlMTk2ODdjOTA0NjU2YWExZmMwNTk4NmRkM2ZlNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJoaGphYnJpcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iMmIxMmE4MTRjZWQ4YWYwMmNkZGYyOWEzN2U3ZjMwMTFlNDMwZThhMThiMzhiNzA2ZjI3YzZiZDMxNjUwYjY1IgogICAgfQogIH0KfQ==",
                "Ib9AF9NCfiTD6qxjEoMldjDKh5SWBfPddBBprtEcCFZY8KsGPX3qSaLy6bmYEnR1KjLmDR0XcuaMqHB4v4E7ZfiebJoBRSgwbZmCN7SMJTKRKbrgL1t/m2/Exo9C6UcGzy2fK/mRdyCu+a2YjHzdTv9BP8JzvvKuejq02IrfzF9IzpsRrvkfUPGAZ4Ukc9OgiRLmjznaM7+nCGUh7EaEvWqQxYGY1jT/DH0nj7W880WDNCLs5nWBzs/Ho8PDbOOgdVmpVDvWDbaEE0S7Q7AxHhTXRVNKyxk9vwa90keKz6kRRzUQnd+CmL2rNYmZ4ngSy3U/+yxSBYCvMJEEzQNZrizb0Zf9ciUNpysDc51HPBn8/lqDXI8E69Z96Ch+JfKxWuOQTJ1S2aSuI3yX5lwYwVeYyVTtk3v1PHvk3by7v5sf3lop9Q2w5UKNhSlDZwZ74dZ9k0oLw+luJ/OPsll1DUT95Ly3AoHPAyNyKsgaWI+IJJlpl8LM2tAHAv1sns1p8ZBgpRZH+8dI7yVhNI42GM6WIITUXK0PzUDxlHGfuaVAlP3LfUrFYMTfeeqrqO1QM6nt1fPrFd9XkcdcMJjYkMU5WZbifcl2RfGiv4SHr7LiFJl1f2XtFKdO0isnLyqGrwAczsbUdrIJzUjDheKSGZWfDLUPEim99hzsvyE/xfY="
        ));
    }

    @Override
    protected boolean hasCompletionMessage() {
        return true;
    }

    @Override
    protected List<String> getRewards() {
        return Collections.singletonList(ChatColor.DARK_GRAY + "+" + ChatColor.GOLD + "100 Coins");
    }

    @Override
    protected void reward(SkyblockPlayer player) {
        player.addCoins(100);
    }

    private static class TalkToLumberjackObjective extends Objective {
        public TalkToLumberjackObjective() {
            super("talk_to_lumberjack", "Talk to the Lumberjack");
        }
    }

    private static class CollectLogsObjective extends Objective {
        public CollectLogsObjective() {
            super("collect_logs", "Collect Logs");
        }

        @Override
        public String getSuffix(SkyblockPlayer player) {
            return ChatColor.translateAlternateColorCodes('&',
                    "&7(&e" + player.getValue("quests.timber.logsBroken") + "&7/&a10&7)"
            );
        }

        @EventHandler
        public void onLogBreak(SkyblockPlayerLogBreakEvent event) {
            SkyblockPlayer player = event.getPlayer();

            if (player.getQuestLine() == null) return;
            if (!player.getQuestLine().getName().equals("timber")) return;

            int collectedLogs = (int) player.getValue("quests.timber.logsBroken");

            player.setValue("quests.timber.logsBroken", collectedLogs + 1);
            if (collectedLogs == 9) complete(player.getBukkitPlayer());
        }

    }

    private static class ReturnToLumberjackObjective extends Objective {
        public ReturnToLumberjackObjective() {
            super("return_to_lumberjack", "Talk to the Lumberjack");
        }

        @Override
        public String getSuffix(SkyblockPlayer player) {
            return ChatColor.translateAlternateColorCodes('&', "&fOak Wood &8x10");
        }
    }

    private static class TravelToParkObjective extends Objective {
        public TravelToParkObjective() {
            super("travel_to_park", "Travel to The Park");
        }

        @EventHandler
        public void onMove(PlayerMoveEvent e) {
            if (!isThisObjective(e.getPlayer())) return;

            if (SkyblockPlayer.getPlayer(e.getPlayer()).getCurrentLocationName().equals("Birch Park")) complete(e.getPlayer());
        }

        @Override
        public void complete(Player p) {
            super.complete(p);

            Skill.reward(Skill.parseSkill("Foraging"), 10, SkyblockPlayer.getPlayer(p));
        }
    }

    private static class CollectBirchLogObjective extends Objective {

        public CollectBirchLogObjective() {
            super("collect_birch_log_objective", "Collect birch logs");
        }

        @Override
        public String getSuffix(SkyblockPlayer player) {
            return ChatColor.translateAlternateColorCodes('&',
                    "&7(&e" + player.getValue("quests.timber.birchLogsBroken") + "&7/&a64&7)");
        }

        @EventHandler
        public void onLogBreak(SkyblockPlayerLogBreakEvent event) {
            if (!isThisObjective(event.getPlayer().getBukkitPlayer())) return;

            SkyblockPlayer player = event.getPlayer();

            int collectedLogs = (int) player.getValue("quests.timber.birchLogsBroken");

            player.setValue("quests.timber.birchLogsBroken", collectedLogs + 1);
            if (collectedLogs == 63) complete(player.getBukkitPlayer());
        }
    }

    private static class TalkToCharlie extends Objective {
        public TalkToCharlie() { super("talk_to_charlie", "Talk to Charlie"); }
    }



}
