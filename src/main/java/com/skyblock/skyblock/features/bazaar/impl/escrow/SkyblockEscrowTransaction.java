package com.skyblock.skyblock.features.bazaar.impl.escrow;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.bazaar.escrow.EscrowTransaction;
import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;

import java.util.function.Consumer;

@AllArgsConstructor
public class SkyblockEscrowTransaction implements EscrowTransaction {

    private final OfflinePlayer seller;
    private final SkyblockPlayer buyer;
    private final double amount;

    private final Consumer<EscrowTransaction> onFill;

    private boolean cancelled;
    private boolean filled;

    @Override
    public OfflinePlayer getSeller() {
        return this.seller;
    }

    @Override
    public SkyblockPlayer getBuyer() {
        return this.buyer;
    }

    @Override
    public double getAmount() {
        return this.amount;
    }

    @Override
    public Consumer<EscrowTransaction> onFill() {
        return this.onFill;
    }

    @Override
    public void fill() {
        if (this.cancelled) return;

        this.onFill.accept(this);

        this.filled = true;
    }

    @Override
    public boolean isFilled() {
        return this.filled;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
