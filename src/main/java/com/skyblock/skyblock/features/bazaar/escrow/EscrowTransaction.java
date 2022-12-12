package com.skyblock.skyblock.features.bazaar.escrow;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.bazaar.Bazaar;
import org.bukkit.OfflinePlayer;

import java.util.function.Consumer;

public interface EscrowTransaction {

    Bazaar getBazaar();

    OfflinePlayer getSeller();
    SkyblockPlayer getBuyer();
    double getAmount();

    Consumer<EscrowTransaction> getOnFill();

    void fill();

    boolean isFilled();

    void cancel();

    boolean isCancelled();

}
