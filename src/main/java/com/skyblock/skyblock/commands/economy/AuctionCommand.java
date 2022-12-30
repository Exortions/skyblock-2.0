package com.skyblock.skyblock.commands.economy;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionHouse;
import com.skyblock.skyblock.features.auction.gui.AuctionHouseGUI;
import com.skyblock.skyblock.features.auction.gui.AuctionInspectGUI;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.TrueAlias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiresPlayer
@Usage(usage = "/sb auction <price> <time> <bin>")
@Description(description = "Shows auction house")
public class AuctionCommand implements Command, TrueAlias<AuctionCommand> {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        AuctionHouse ah = plugin.getAuctionHouse();

        if (args.length == 0) {
            new AuctionHouseGUI(player).show(player);
            return;
        }

        if (args.length == 1) {
            UUID uuid = UUID.fromString(args[0]);

            new AuctionInspectGUI(AuctionHouse.AUCTION_CACHE.get(uuid), player).show(player);
        } else{
            ah.createAuction(player.getItemInHand(), player, Long.parseLong(args[0]), Long.parseLong(args[1]), Boolean.parseBoolean(args[2]));
        }
    }
}
