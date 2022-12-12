package com.skyblock.skyblock.features.bazaar.impl.escrow;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.bazaar.Bazaar;
import com.skyblock.skyblock.features.bazaar.escrow.EscrowTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.OfflinePlayer;

import java.util.function.Consumer;

@Data
public class SkyblockEscrowTransaction implements EscrowTransaction {

    private final Bazaar bazaar;

    private final OfflinePlayer seller;
    private final SkyblockPlayer buyer;
    private final double amount;

    private final Consumer<EscrowTransaction> onFill;

    private boolean cancelled;
    private boolean filled;

    @Override
    public void fill() {
        if (this.cancelled) return;

        this.onFill.accept(this);

        this.filled = true;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

}
