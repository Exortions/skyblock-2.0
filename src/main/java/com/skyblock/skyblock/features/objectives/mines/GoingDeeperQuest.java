package com.skyblock.skyblock.features.objectives.mines;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerCollectItemEvent;
import com.skyblock.skyblock.events.SkyblockPlayerNPCClickEvent;
import com.skyblock.skyblock.events.SkyblockPlayerSkillXPChangeEvent;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.features.skills.Mining;
import com.skyblock.skyblock.features.skills.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Arrays;

public class GoingDeeperQuest extends QuestLine {

    public GoingDeeperQuest() {
        super("going_deeper", "Going Deeper",
                new ReachMiningVObjective(),
                new TravelToTheDeepCavernsObjective(),
                new TalkToTheLiftOperatorObjective(),
                new ReachObjective("Lapis Quarry", "lapis_quarry"),
                new CollectObjective(Material.INK_SACK, 4, "lapis lazuli"),
                new ReachObjective("Pigman's Den", "pigmans_den"),
                new CollectObjective(Material.REDSTONE, "redstone"),
                new ReachObjective("Slimehill", "slimehill"),
                new CollectObjective(Material.EMERALD, "emerald"),
                new ReachObjective("Diamond Reserve", "diamond_reserve"),
                new CollectObjective(Material.DIAMOND, "diamond"),
                new ReachObjective("Obsidian Sanctuary", "obsidian_sanctuary"),
                new CollectObjective(Material.OBSIDIAN, "obsidian"));
    }

    @Override
    protected boolean hasCompletionMessage() {
        return true;
    }

    private static final class ReachMiningVObjective extends Objective {
        public ReachMiningVObjective() {
            super("reach_mining_v", "Reach Mining V");
        }

        @EventHandler
        public void onCollectBlock(SkyblockPlayerSkillXPChangeEvent event) {
            if (!isThisObjective(event.getPlayer().getBukkitPlayer())) return;

            if (Skill.getLevel(Skill.getXP(new Mining(), event.getPlayer())) >= 5) {
                complete(event.getPlayer().getBukkitPlayer());
            }
        }
    }

    private static final class TravelToTheDeepCavernsObjective extends Objective {

        public TravelToTheDeepCavernsObjective() {
            super("travel_to_deep_caverns", "Travel to the Deep Caverns");
        }

        @EventHandler
        public void onMove(PlayerMoveEvent e) {
            if (!isThisObjective(e.getPlayer())) return;

            if (SkyblockPlayer.getPlayer(e.getPlayer()).getCurrentLocationName().equals("Deep Caverns")) complete(e.getPlayer());
        }
    }

    private static final class TalkToTheLiftOperatorObjective extends Objective {
        public TalkToTheLiftOperatorObjective() {
            super("talk_to_lift_operator", "Talk to the Lift Operator");
        }

        @EventHandler
        public void onNPC(SkyblockPlayerNPCClickEvent e) {
            if (!isThisObjective(e.getPlayer())) return;

            if (e.getNpc().getName().contains("Lift Operator")) {
                complete(e.getPlayer());
            }
        }
    }

    @Getter
    private static final class ReachObjective extends Objective {
        private final String locationName;
        private final String locationId;

        public ReachObjective(String locationName, String locationId) {
            super("reach_the_" + locationId, "Reach the " + locationName);

            this.locationId = locationId;
            this.locationName = locationName;
        }

        @EventHandler
        public void onMove(PlayerMoveEvent e) {
            if (!isThisObjective(e.getPlayer())) return;

            if (SkyblockPlayer.getPlayer(e.getPlayer()).getCurrentLocationName().equals(locationName)) complete(e.getPlayer());
        }
    }

    private static final class CollectObjective extends Objective {
        private final Material mat;
        private final int data;

        public CollectObjective(Material mat, String name) {
            this(mat, (short) 0, name);
        }

        public CollectObjective(Material mat, int data, String name) {
            super("collect_" + name, "Collect " + name);

            this.mat = mat;
            this.data = data;
        }

        @EventHandler
        public void onCollect(SkyblockPlayerCollectItemEvent e) {
            if (!isThisObjective(e.getPlayer().getBukkitPlayer())) return;

            Collection collection = e.getCollection();

            if (collection.getMaterial().equals(mat) &&
                    collection.getData() == data) {
                complete(e.getPlayer().getBukkitPlayer());
            }
        }
    }
}
