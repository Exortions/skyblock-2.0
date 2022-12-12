package com.skyblock.skyblock.features.bazaar.escrow;

import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.function.Consumer;

public interface Escrow {

    class EscrowBalanceException extends RuntimeException {

        public EscrowBalanceException(String message) {
            super(message);
        }

    }

    List<EscrowTransaction> getTransactions();

    EscrowTransaction createTransaction(OfflinePlayer seller, SkyblockPlayer buyer, double amount, Consumer<EscrowTransaction> onFill);

    void deposit(double amount);
    void withdraw(double amount) throws EscrowBalanceException;

    double getBalance();

    void reset();
}
