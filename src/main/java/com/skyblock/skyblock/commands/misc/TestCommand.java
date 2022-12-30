package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.MiningMinionType;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.features.auction.AuctionCategory;
import com.skyblock.skyblock.features.auction.AuctionSettings;
import com.skyblock.skyblock.features.bazaar.escrow.Escrow;
import com.skyblock.skyblock.features.bazaar.escrow.EscrowTransaction;
import com.skyblock.skyblock.features.minions.MiningMinion;
import com.skyblock.skyblock.features.minions.MinionBase;
import com.skyblock.skyblock.features.time.gui.CalendarEventsGUI;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiresPlayer
@Usage(usage = "/sb test")
@Description(description = "Command for testing features")
public class TestCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
//        Escrow escrow = Skyblock.getPlugin().getBazaar().getEscrow();
//
//        EscrowTransaction transaction = escrow.createTransaction(player, player, 100, 300, escrow.getBazaar().getRawItems().get(0), Escrow.TransactionType.SELL, (trans) -> {
//            if (Bukkit.getPlayer(trans.getSeller().getUniqueId()) != null) {
//                Bukkit.getPlayer(trans.getSeller().getUniqueId()).sendMessage("order filled!");
//            }
//
//            if (Bukkit.getPlayer(trans.getBuyer().getUniqueId()) != null) {
//                Bukkit.getPlayer(trans.getBuyer().getUniqueId()).sendMessage("order filled!");
//            }
//        });
//
//        escrow.fillBuyOrder(transaction, 300);
//
//        player.sendMessage(escrow.getBuyOrders().toString());
//        player.sendMessage(escrow.getSellOrders().toString());
//
//        new CalendarEventsGUI(player).show(player);
//
//        player.teleport(new Location(Bukkit.createWorld(new WorldCreator(args[0])), 0, 100, 0));

//        MinionBase minion = new MiningMinion(MiningMinionType.COBBLESTONE);
//        minion.spawn(SkyblockPlayer.getPlayer(player), player.getLocation(), 6);

        ItemStack item = player.getItemInHand();

        AuctionCategory category = AuctionCategory.valueOf(args[0]);
        Rarity teir = Rarity.valueOf(args[1]);
//        String search = args[2];

        if (!category.getCanPut().test(item)) Bukkit.broadcastMessage("Failed Category Test");
        if (!Rarity.valueOf(ChatColor.stripColor(new NBTItem(item).getString("rarity")).split(" ")[0]).equals(teir)) Bukkit.broadcastMessage("Failed Rarity Test");
//        if (!ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase().contains(search.toLowerCase()) && !search.equals("")) Bukkit.broadcastMessage("Failed Search Test");
    }
}
