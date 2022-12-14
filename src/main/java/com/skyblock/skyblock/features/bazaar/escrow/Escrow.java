package com.skyblock.skyblock.features.bazaar.escrow;

import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface Escrow {

    class EscrowBalanceException extends RuntimeException {

        public EscrowBalanceException(String message) {
            super(message);
        }

    }

    enum TransactionType {
        BUY,
        SELL
    }

    HashMap<UUID, EscrowTransaction> getTransactions();

    List<EscrowTransaction> getBuyOrders();
    List<EscrowTransaction> getSellOrders();

    EscrowTransaction createTransaction(OfflinePlayer seller, OfflinePlayer buyer, double price, int amount, TransactionType type, Consumer<EscrowTransaction> onFill);
    EscrowTransaction getTransaction(UUID uuid);

    void removeTransaction(UUID uuid);

    void fillSellOrder(EscrowTransaction transaction, int amountSold);
    void fillBuyOrder(EscrowTransaction transaction, int amountBought);

    void deposit(double amount);
    void withdraw(double amount) throws EscrowBalanceException;

    double getBalance();

    void reset();
}
