package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.bazaar.escrow.Escrow;
import com.skyblock.skyblock.features.bazaar.escrow.EscrowTransaction;
import com.skyblock.skyblock.features.blocks.crops.FloatingCrystal;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiresPlayer
@Usage(usage = "/sb test")
@Description(description = "Command for testing features")
public class TestCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
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
