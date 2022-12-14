package com.skyblock.skyblock.features.bazaar.impl.escrow;

import com.skyblock.skyblock.features.bazaar.Bazaar;
import com.skyblock.skyblock.features.bazaar.escrow.Escrow;
import com.skyblock.skyblock.features.bazaar.escrow.EscrowTransaction;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Escrow is currently stored in memory
 * TODO: Store escrow in server files
 *
 * The escrow is a list of transactions that are pending
 * It manages the transactions and waits for buy/sell
 * orders to be filled.
 */

public class SkyblockEscrow implements Escrow {

    private final Bazaar bazaar;

    private final HashMap<UUID, EscrowTransaction> transactions;

    private double escrowBalance;

    public SkyblockEscrow(Bazaar bazaar) {
        this.bazaar = bazaar;

        this.transactions = new HashMap<>();
        this.escrowBalance = 0;
    }

    @Override
    public HashMap<UUID, EscrowTransaction> getTransactions() {
        return this.transactions;
    }

    @Override
    public List<EscrowTransaction> getBuyOrders() {
        return this.transactions.values().stream().filter(EscrowTransaction::isBuyOrder).collect(Collectors.toList());
    }

    @Override
    public List<EscrowTransaction> getSellOrders() {
        return this.transactions.values().stream().filter(EscrowTransaction::isSellOrder).collect(Collectors.toList());
    }

    @Override
    public void fillSellOrder(EscrowTransaction transaction, int amountSold) {
        if (transaction.isCancelled() || !this.transactions.containsKey(transaction.getUuid())) return;

        List<EscrowTransaction> buyOrders = this.getBuyOrders().stream().sorted((o1, o2) -> {
            double o1Price = o1.getPrice();
            double o2Price = o2.getPrice();

            return Double.compare(o2Price, o1Price);
        }).collect(Collectors.toList());

        for (EscrowTransaction buyOrder : buyOrders) {
            if (amountSold <= 0) break;

            if (buyOrder.isCancelled() || !this.transactions.containsKey(buyOrder.getUuid())) continue;

            if (buyOrder.getAmount() > amountSold) {
                buyOrder.fill(amountSold);
                amountSold = 0;
            } else {
                buyOrder.fill(buyOrder.getAmount());
                amountSold -= buyOrder.getAmount();
            }

            this.transactions.put(buyOrder.getUuid(), buyOrder);
        }
    }

    @Override
    public void fillBuyOrder(EscrowTransaction transaction, int amountBought) {
        if (transaction.isCancelled() || !this.transactions.containsKey(transaction.getUuid())) return;

        List<EscrowTransaction> sellOrders = this.getSellOrders().stream().sorted((o1, o2) -> {
            double o1Price = o1.getPrice();
            double o2Price = o2.getPrice();

            return Double.compare(o1Price, o2Price);
        }).collect(Collectors.toList());

        for (EscrowTransaction sellOrder : sellOrders) {
            if (amountBought <= 0) break;

            if (sellOrder.isCancelled() || !this.transactions.containsKey(sellOrder.getUuid())) continue;

            if (sellOrder.getAmount() > amountBought) {
                sellOrder.fill(amountBought);
                amountBought = 0;
            } else {
                sellOrder.fill(sellOrder.getAmount());
                amountBought -= sellOrder.getAmount();
            }

            this.transactions.put(sellOrder.getUuid(), sellOrder);
        }
    }

    @Override
    public EscrowTransaction createTransaction(OfflinePlayer seller, OfflinePlayer buyer, double price, int amount, TransactionType type, Consumer<EscrowTransaction> onFill) {
        EscrowTransaction transaction = new SkyblockEscrowTransaction(this.bazaar, UUID.randomUUID(), seller, buyer, price, amount, type, onFill, false, false);

        this.transactions.put(transaction.getUuid(), transaction);
        this.deposit(amount);

        return transaction;
    }

    @Override
    public EscrowTransaction getTransaction(UUID uuid) {
        return this.transactions.get(uuid);
    }

    @Override
    public void removeTransaction(UUID uuid) {
        this.transactions.remove(uuid);
    }

    @Override
    public void deposit(double amount) {
        this.escrowBalance += amount;
    }

    @Override
    public void withdraw(double amount) throws EscrowBalanceException{
        if (this.escrowBalance < amount) throw new EscrowBalanceException("Escrow balance is too low to withdraw " + amount);

        this.escrowBalance -= amount;
    }

    @Override
    public double getBalance() {
        return this.escrowBalance;
    }

    @Override
    public void reset() {
        this.escrowBalance = 0;
        this.transactions.clear();
    }
}
