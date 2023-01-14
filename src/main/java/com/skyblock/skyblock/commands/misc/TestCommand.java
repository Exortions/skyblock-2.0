package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.dragon.DragonSequence;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.entity.Player;

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

//            MinionBase minion = new CobblestoneMinion();
//            minion.spawn(SkyblockPlayer.getPlayer(player), player.getLocation(), 6);

//        player.openInventory(new AnvilGUI());

        DragonSequence.endingSequence();

//        ItemStack item = player.getItemInHand();
//
//        AuctionCategory category = AuctionCategory.valueOf(args[0]);
//        Rarity tier = Rarity.valueOf(args[1]);
//        String search = args[2];
//
//        if (!category.getCanPut().test(item)) Bukkit.broadcastMessage("Failed Category Test");
//        if (!Rarity.valueOf(ChatColor.stripColor(new NBTItem(item).getString("rarity")).split(" ")[0]).equals(tier)) Bukkit.broadcastMessage("Failed Rarity Test");
//        if (!ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase().contains(search.toLowerCase()) && !search.equals("")) Bukkit.broadcastMessage("Failed Search Test");
    }
}
