package com.skyblock.skyblock.utilities.sign;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

public class SignGui {

    @Getter
    private final SignClickCompleteHandler completeHandler;
    private final SignManager manager;
    private String[] lines;
    private Player player;

    public SignGui(SignManager manager, SignClickCompleteHandler completeHandler) {
        this.completeHandler = completeHandler;
        this.lines = new String[4];
        this.manager = manager;
        this.player = null;
    }

    public SignGui withLines(String... lines) {
        if (lines.length != 4) throw new IllegalArgumentException("Lines must have a length of 4");

        this.lines = lines;

        return this;
    }

    public void open(Player player) {
        this.player = player;

        final BlockPosition blockPosition = new BlockPosition(player.getLocation().getBlockX(), 255, player.getLocation().getBlockZ());

        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld) player.getWorld()).getHandle(), blockPosition);
        packet.block = CraftMagicNumbers.getBlock(Material.SIGN_POST).getBlockData();
        sendPacket(packet);

        IChatBaseComponent[] components = CraftSign.sanitizeLines(lines);
        TileEntitySign sign = new TileEntitySign();
        sign.isEditable = true;
        sign.a(blockPosition);
        System.arraycopy(components, 0, sign.lines, 0, sign.lines.length);
        sendPacket(sign.getUpdatePacket());

        PacketPlayOutOpenSignEditor signPacket = new PacketPlayOutOpenSignEditor(blockPosition);
        sendPacket(signPacket);

        this.manager.add(player.getUniqueId(), this);
    }

    private void sendPacket(Packet<?> packet) {
        Preconditions.checkNotNull(this.player);

        ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
    }
}
