package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionHouse;
import com.skyblock.skyblock.features.auction.gui.AuctionBrowserGUI;
import com.skyblock.skyblock.features.bazaar.escrow.Escrow;
import com.skyblock.skyblock.features.bazaar.escrow.EscrowTransaction;
import com.skyblock.skyblock.features.bazaar.impl.SkyblockBazaarSubItem;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiresPlayer
@Usage(usage = "/sb test")
@Description(description = "Command for testing features")
public class TestCommand implements Command {

//    int index = 0;

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {

//        FairySoulHandler soul = plugin.getFairySoulHandler();
//
//        int i = 0;
//        for (Location l : soul.getLocations()) {
//            if (i == index) player.teleport(l);
//            i++;
//        }
//
//        index++;

//        Skill.reward(new Combat(), Integer.parseInt(args[0]), SkyblockPlayer.getPlayer(player));
//
//        Jerry jerry = new Jerry();
//        jerry.setRarity(Rarity.LEGENDARY);
//
//        Tiger tiger = new Tiger();
//        tiger.setRarity(Rarity.LEGENDARY);
//
//        BlueWhale whale = new BlueWhale();
//        whale.setRarity(Rarity.RARE);
//
//        player.getInventory().addItem(jerry.toItemStack());
//        player.getInventory().addItem(tiger.toItemStack());
//        player.getInventory().addItem(whale.toItemStack());
//
//        MinionBase cobblestoneMinion = new MiningMinion(MiningMinionType.COBBLESTONE);
//        cobblestoneMinion.spawn(SkyblockPlayer.getPlayer(player), player.getLocation(), 1);

//        for (Auction auction : AuctionHouse.AUCTION_CACHE.values()) {
//            player.sendMessage(auction.toString());
//        }

//        if (args.length == 1) {
//            new AuctionBrowserGUI(player).show(player);
//            return;
//        }

        Escrow escrow = Skyblock.getPlugin().getBazaar().getEscrow();

        EscrowTransaction transaction = escrow.createTransaction(player, player, 100, 300, escrow.getBazaar().getRawItems().get(0), Escrow.TransactionType.SELL, (trans) -> {
            if (Bukkit.getPlayer(trans.getSeller().getUniqueId()) != null) {
                Bukkit.getPlayer(trans.getSeller().getUniqueId()).sendMessage("order filled!");
            }

            if (Bukkit.getPlayer(trans.getBuyer().getUniqueId()) != null) {
                Bukkit.getPlayer(trans.getBuyer().getUniqueId()).sendMessage("order filled!");
            }
        });

        escrow.fillBuyOrder(transaction, 300);

        player.sendMessage(escrow.getBuyOrders().toString());
        player.sendMessage(escrow.getSellOrders().toString());
    }
}
