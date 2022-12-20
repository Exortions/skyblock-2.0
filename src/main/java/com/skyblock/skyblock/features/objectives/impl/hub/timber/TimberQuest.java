package com.skyblock.skyblock.features.objectives.impl.hub.timber;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.event.SkyblockLogBreakEvent;
import com.skyblock.skyblock.features.npc.NPC;
import com.skyblock.skyblock.features.npc.NPCHandler;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.features.objectives.gui.GiftGui;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class TimberQuest extends QuestLine {

    public TimberQuest() {
        super("timber", "Timber!", new TalkToLumberjackObjective(), new CollectLogsObjective(), new ReturnToLumberjackObjective());

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
                        int logsCollected = (int) SkyblockPlayer.getPlayer(player).getValue("quests.timber.logsBroken");

                        if (getObjective(SkyblockPlayer.getPlayer(player)).getId().equals("collect_logs")) {
                            List<String> busyMessages = new ArrayList<String>() {{
                                add("A lumberjack always pays his debts!");
                                add("Bring me some Logs. You can chop them down in my Forest!");
                                add("Howdy, " + player.getName() + "!");
                            }};

                            Util.sendDelayedMessage(player, "Lumberjack", busyMessages.get(Skyblock.getPlugin().getRandom().nextInt(busyMessages.size())), 0);
                        }

                        if (logsCollected == 10) {
                            Util.sendDelayedMessages(player, "Lumberjack", (p) -> new GiftGui(player, Skyblock.getPlugin().getItemHandler().getItem("SWEET_AXE.json")),
                                    "Thank you! Take this " + ChatColor.YELLOW + "Sweet Axe" + ChatColor.WHITE + ", it's so sweet that it drops apples from logs sometimes!",
                                    "You've got the knack for wood. Could you get some Birch Planks from The Park?",
                                    "My associate will be there waiting for you. He will reward you in Coins if you're up to the task!");
                        }
                    }
                },
                "ewogICJ0aW1lc3RhbXAiIDogMTU5MTQ2MzM5MzU5MywKICAicHJvZmlsZUlkIiA6ICJmMjljOTIyMmVjNmY0NjExOTc3YWNkMmFjYzExNDAxOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJJSVJleWRlbklJIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2NkMGFiZGQ5ZjZlZTdkODM3MGQyNjIxMDljYjg1M2JhY2QzZjQzZjhjZTQyNDExNjRkMDhlM2QyYTU2ODNlMzIiCiAgICB9CiAgfQp9",
                "kyxxya3FqBJo6onG3bC1BjsjrF4uWTDd7Qitz14iNFwvZOn6FsW1A7dkNiZmiR1nVBfex7b4XHYAb6f3jXx8wIEvoDzSjzVLkCkzbQ9aMTTlDSAvkZ/fqtgMJbbXnppUSETmbWm7fdPjr4P5J30+Mz5vb66kNYu8QXsWqQ36YxI6sS2P77+vLdq+n1l389Npw1uMBpMLPjXaTsjAMrq1U6bDbTj7YwYqtURh0hxJm6v3q9d+oXD989fvYu04DYtEiW6H3VDjtvoEAb+m3H9tlDt74SNVXlIJ0lGa6RNpidBhKgSS38F0P5nMo1XbHJ/FcrWP+UZ6D4rT5TuW0T1J7n+5q+/LMMOR2hofFHgdqmTD85tTOmTmKKtIBPW9yreEKNZg7Ah/s3jStnVosp6A9qkpTcmdneRJwL+ZvHcpCbZpJq9Ii/NV+cBNrL49ylVCDZnRN4I7xENYfAq/Xe241cs2bEErrpu9uDH7dXSnhwQn4PdtMN6ZZrr4IJS2sjAyPjcuN/A006gs3Cu+9Cb4MViRCdIgOZDsIV/C0yDqr3+/SgKa+GRp7qIiphIMgRXC03GDSd/btYea1g8qJhhnkAL5MStubm/rdPaEf8wBc3y8Kc4pdEemo5kHQ8SPGvrk+xI8sGWKmlf/bZkyXKw9Wdhg/npeptxjNb2rUuaIDvk="
        ));
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
        public void onLogBreak(SkyblockLogBreakEvent event) {
            SkyblockPlayer player = event.getPlayer();

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

}
