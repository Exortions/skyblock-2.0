package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class MiningMinion extends MinionBase {
    public MiningMinion(UUID uuid,
                      String name,
                      String adjective,
                      Color leatherArmorColor,
                      Material material) {
        super(uuid, name, adjective, leatherArmorColor, material);

        this.plugin = Skyblock.getPlugin();
    }

    @Override
    public abstract ItemStack getHand(int level);
    
    @Override
    public String getHead(int level) {
        return MinionBase.getHeadVaueFromMinion(name, level);
    }

    @Override
    public abstract int getActionDelay(int level);
    
    @Override
    public abstract int getMaxStorage(int level);
    
    @Override
    public abstract ArrayList<ItemStack> calculateDrops(int level);
    
    @Override
    public abstract int getSlotLevelRequirement(int level);

    @Override
    protected void tick(SkyblockPlayer player, Location location) {
        List<Block> blocks = new ArrayList<>();

        int miningRadius = actionRadius + additionalActionRadius;
        for (int x = miningRadius * -1; x <= miningRadius; x++) {
            for (int z = miningRadius * - 1; z <= miningRadius; z++) {
                Block block = location.clone().add(x, -1, z).getBlock();
                blocks.add(block);
            }
        }

        List<Block> air = blocks.stream().filter(block -> block.getType().equals(Material.AIR)).collect(Collectors.toList());
        List<Block> ores = blocks.stream().filter(block -> block.getType().equals(getMaterial())).collect(Collectors.toList());

        if (air.size() != 0) {
            this.text.setCustomNameVisible(false);

            Block block = air.get(this.plugin.getRandom().nextInt(air.size()));

            Material toSet = getMaterial();

            block.setType(toSet);

            for (Player target : location.getWorld().getPlayers()) {
                PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(0, new BlockPosition(block.getX(), block.getY(), block.getZ()), 50);
                PacketPlayOutBlockChange packet1 = new PacketPlayOutBlockChange(((CraftWorld) location.getWorld()).getHandle(), new BlockPosition(block.getX(), block.getY(), block.getZ()));
                ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
                ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet1);
            }
        } else {
            Block block;
            try {
                block = ores.get(this.plugin.getRandom().nextInt(ores.size()));
            } catch (IllegalArgumentException ex) {
                this.text.setCustomName(ChatColor.RED + "I need more space!");
                this.text.setCustomNameVisible(true);

                return;
            }

            this.text.setCustomNameVisible(false);

            for (int i = 0; i < 10; i++) {
                int finalI = i;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(0, new BlockPosition(block.getX(), block.getY(), block.getZ()), finalI);
                        PacketPlayOutBlockChange packet2 = new PacketPlayOutBlockChange(((CraftWorld) location.getWorld()).getHandle(), new BlockPosition(block.getX(), block.getY(), block.getZ()));

                        for (Player target : location.getWorld().getPlayers()) ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);

                        if (finalI == 9) {
                            block.setType(Material.AIR);

                            for (Player target : location.getWorld().getPlayers()) ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet2);

                            collect(player);
                        }
                    }
                }.runTaskLater(Skyblock.getPlugin(Skyblock.class), i * 10);
            }
        }
    }
}
