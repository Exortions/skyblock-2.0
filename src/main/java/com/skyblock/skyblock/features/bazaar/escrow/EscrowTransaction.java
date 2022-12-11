package com.skyblock.skyblock.features.bazaar.escrow;

import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.OfflinePlayer;

import java.util.function.Consumer;

public interface EscrowTransaction {

    OfflinePlayer getSeller();
    SkyblockPlayer getBuyer();
    double getAmount();

    Consumer<EscrowTransaction> onFill();

}
