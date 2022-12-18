package com.skyblock.skyblock.utilities;

import lombok.Data;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Data
public class BossBar {

    private final Player player;
    private String message;
    private EntityWither wither;

    public BossBar(Player player) {
        this.player = player;
        this.message = "";
    }

    public void update() {
        Vector dir = player.getLocation().getDirection();
        Location loc = player.getLocation().add(dir.multiply(40));
        reset();
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        wither = new EntityWither(world);
        wither.setSize(1.0f, 1.0f);
        wither.setLocation(loc.getX(), player.getLocation().getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
        wither.setCustomName(message);
        wither.setInvisible(true);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void reset() {
        if (wither == null) return;

        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
