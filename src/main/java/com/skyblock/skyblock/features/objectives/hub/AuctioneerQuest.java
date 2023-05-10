package com.skyblock.skyblock.features.objectives.hub;

import com.skyblock.skyblock.events.SkyblockPlayerNPCClickEvent;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import org.bukkit.event.EventHandler;

public class AuctioneerQuest extends QuestLine {
    public AuctioneerQuest() {
        super("auctioneer", "Auctioneer", new AuctioneerObjective());
    }

    @Override
    protected boolean hasCompletionMessage() {
        return true;
    }

    private static class AuctioneerObjective extends Objective {
        public AuctioneerObjective() {
            super("auctioneer_objective", "Talk to the Auction Master");
        }

        @EventHandler
        public void onClick(SkyblockPlayerNPCClickEvent e) {
            if (!isThisObjective(e.getPlayer())) return;

            if (e.getNpc().getName().endsWith("Auction Master")) complete(e.getPlayer());
        }
    }
}
