package com.skyblock.skyblock.features.bazaar.escrow;

import com.skyblock.skyblock.features.bazaar.Bazaar;
import com.skyblock.skyblock.features.bazaar.BazaarSubItem;
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

    Bazaar getBazaar();

    HashMap<UUID, EscrowTransaction> getTransactions();

    List<EscrowTransaction> getBuyOrders();
    List<EscrowTransaction> getSellOrders();

    List<EscrowTransaction> getRankedBuyOrders();
    List<EscrowTransaction> getRankedSellOrders();

    double getBuyPrice(BazaarSubItem item);
    double getSellPrice(BazaarSubItem item);

    EscrowTransaction createTransaction(OfflinePlayer seller, OfflinePlayer buyer, double price, int amount, BazaarSubItem item, TransactionType type, Consumer<EscrowTransaction> onFill);

    EscrowTransaction getTransaction(UUID uuid);

    void removeTransaction(UUID uuid);

    void fillSellOrder(EscrowTransaction transaction, int amountSold);
    void fillBuyOrder(EscrowTransaction transaction, int amountBought);

    void deposit(double amount);
    void withdraw(double amount) throws EscrowBalanceException;

    double getBalance();

    void reset();
}
