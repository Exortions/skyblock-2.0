package com.skyblock.skyblock.features.bazaar.impl.escrow;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.bazaar.Bazaar;
import com.skyblock.skyblock.features.bazaar.BazaarSubItem;
import com.skyblock.skyblock.features.bazaar.escrow.Escrow;
import com.skyblock.skyblock.features.bazaar.escrow.EscrowTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
public class SkyblockEscrowTransaction implements EscrowTransaction {

    private final Bazaar bazaar;

    private final UUID uuid;

    private final OfflinePlayer seller;
    private final OfflinePlayer buyer;
    private final double price;
    private int amount;

    private final BazaarSubItem subItem;

    private final Escrow.TransactionType type;

    private final Consumer<EscrowTransaction> onFill;

    private boolean cancelled;
    private boolean filled;

    @Override
    public void fill(int amount) {
        if (this.cancelled) return;

        this.amount -= amount;

        if (this.amount <= 0) {
            this.filled = true;
            this.onFill.accept(this);

            this.cancel();
        }
    }

    @Override
    public void cancel() {
        this.cancelled = true;

        this.bazaar.getEscrow().removeTransaction(this.getUuid());
    }

}
