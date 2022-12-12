package com.skyblock.skyblock.features.bazaar.impl.escrow;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.bazaar.Bazaar;
import com.skyblock.skyblock.features.bazaar.escrow.Escrow;
import com.skyblock.skyblock.features.bazaar.escrow.EscrowTransaction;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SkyblockEscrow implements Escrow {

    private final Bazaar bazaar;

    private final List<EscrowTransaction> transactions;

    private double escrowBalance;

    public SkyblockEscrow() {
        this.bazaar = Skyblock.getPlugin().getBazaar();

        this.transactions = new ArrayList<>();
        this.escrowBalance = 0;
    }

    @Override
    public List<EscrowTransaction> getTransactions() {
        return this.transactions;
    }

    @Override
    public EscrowTransaction createTransaction(OfflinePlayer seller, SkyblockPlayer buyer, double amount, Consumer<EscrowTransaction> onFill) {
        EscrowTransaction transaction = new SkyblockEscrowTransaction(this.bazaar, seller, buyer, amount, onFill);

        this.transactions.add(transaction);
        this.deposit(amount);

        return transaction;
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
