package com.skyblock.skyblock.features.bazaar.escrow;

import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.OfflinePlayer;

import java.util.List;

public interface Escrow {

    List<EscrowTransaction> getTransactions();

    void createTransaction(OfflinePlayer seller, SkyblockPlayer buyer, double amount);

    void deposit(double amount);

    double getBalance();
}
