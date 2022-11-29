package com.skyblock.skyblock.utilities.sign;

import lombok.Data;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.entity.Player;

@Data
public class SignCompleteEvent {

    private final BlockPosition location;
    private final String[] lines;
    private final Player player;

    public SignCompleteEvent(Player player, BlockPosition location, String[] lines) {
        this.location = location;
        this.player = player;
        this.lines = lines;
    }

}
