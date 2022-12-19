package com.skyblock.skyblock.features.objectives.impl.hub.auction;

import com.skyblock.skyblock.event.SkyblockNPCClickEvent;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class AuctioneerQuest extends QuestLine {
    public AuctioneerQuest() {
        super("auctioneer", "Auctioneer", new AuctioneerObjective());
    }

    private static class AuctioneerObjective extends Objective {
        public AuctioneerObjective() {
            super("auctioneer_objective", "Talk to the Auction Master");
        }

        @EventHandler
        public void onClick(SkyblockNPCClickEvent e) {
            if (!isThisObjective(e.getPlayer())) return;

            if (e.getNpc().getName().endsWith("Auction Master")) complete(e.getPlayer());
        }
    }
}
