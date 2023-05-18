package com.skyblock.skyblock.features.objectives.foraging;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerLogBreakEvent;
import com.skyblock.skyblock.events.SkyblockPlayerSkillXPChangeEvent;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.features.skills.Foraging;
import com.skyblock.skyblock.features.skills.Mining;
import com.skyblock.skyblock.features.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

public class IntoTheWoodsQuest extends QuestLine {
    public IntoTheWoodsQuest() {
        super("into_the_woods", "Into the Woods", new ReachForagingVObjective(), new CollectDarkOakLogObjective(), new TalkToCharlie());
    }

    @Override
    protected boolean hasCompletionMessage() {
        return true;
    }

    private static class ReachForagingVObjective extends Objective {

        public ReachForagingVObjective() {
            super("reach_foraging_v", "Reach Foraging V");
        }

        @EventHandler
        public void onCollectBlock(SkyblockPlayerSkillXPChangeEvent event) {
            if (!isThisObjective(event.getPlayer().getBukkitPlayer())) return;

            if (Skill.getLevel(Skill.getXP(new Foraging(), event.getPlayer())) >= 5) {
                complete(event.getPlayer().getBukkitPlayer());
            }
        }
    }

    private static class CollectDarkOakLogObjective extends Objective {

        public CollectDarkOakLogObjective() {
            super("collect_dark_oak_log", "Collect dark oak logs");
        }

        @Override
        public String getSuffix(SkyblockPlayer player) {
            return ChatColor.translateAlternateColorCodes('&',
                    "&7(&e" + player.getValue("quests.intothewoods.darkOakLogsBroken") + "&7/&a64&7)");
        }

        @EventHandler
        public void onLogBreak(SkyblockPlayerLogBreakEvent event) {
            if (!isThisObjective(event.getPlayer().getBukkitPlayer())) return;

            SkyblockPlayer player = event.getPlayer();

            int collectedLogs = (int) player.getValue("quests.intothewoods.darkOakLogsBroken");

            player.setValue("quests.intothewoods.darkOakLogsBroken", collectedLogs + 1);
            if (collectedLogs == 63) complete(player.getBukkitPlayer());
        }
    }

    private static class TalkToCharlie extends Objective {
        public TalkToCharlie() { super("talk_to_charlie_into_the_woods", "Talk to Charlie"); }
    }

}
