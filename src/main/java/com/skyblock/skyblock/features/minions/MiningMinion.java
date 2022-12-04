package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.MiningMinionType;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MiningMinion extends MinionBase {

    private BukkitRunnable task;
    private int i;

    private int radius;

    private MiningMinionType type;

    public MiningMinion(MiningMinionType minion, UUID uuid) {
        super(
                uuid,
                minion,
                minion.getName(),
                minion.getRecipe(),
                minion.getHand(),
                minion.getHead(),
                minion.getLeatherArmorColor(),
                minion.getTimeBetweenActions(),
                minion.getGetMaximumStorage()
        );

        this.task = null;
        this.i = 0;

        this.type = minion;
    }

    public MiningMinion(MiningMinionType minion) {
        this(minion, UUID.randomUUID());
    }

    @Override
    public void load(SkyblockPlayer player, int index) {

    }

    @Override
    public void spawn(SkyblockPlayer player, Location location, int level) {
        if (!location.getWorld().getName().startsWith(IslandManager.ISLAND_PREFIX)) return;

        Skyblock.getPlugin().getMinionHandler().initializeMinion(player, this, location);

        if (this.minion != null) this.minion.remove();

        this.level = level;

        this.text = location.getWorld().spawn(location.clone().add(0, 0.5, 0), ArmorStand.class);
        this.text.setCustomName("");
        this.text.setCustomNameVisible(false);
        this.text.setGravity(false);
        this.text.setVisible(false);
        this.text.setSmall(true);
        this.text.setMarker(true);

        this.minion = location.getWorld().spawn(location, ArmorStand.class);
        this.minion.setCustomName("");
        this.minion.setCustomNameVisible(false);
        this.minion.setGravity(false);
        this.minion.setVisible(false);
        this.minion.setSmall(true);
        this.minion.setArms(true);
        this.minion.setBasePlate(false);
        this.minion.setCanPickupItems(false);

        ItemStack head = Util.IDtoSkull(new ItemBuilder("", Material.SKULL_ITEM, 1, (short) 3).toItemStack(), this.head.apply(this.level));
        this.minion.setHelmet(head);

        ItemStack hand = this.hand.apply(this.level);
        this.minion.setItemInHand(hand);

        this.minion.setChestplate(Util.colorLeatherArmor(new ItemBuilder("", Material.LEATHER_CHESTPLATE, 1).toItemStack(), this.leatherArmorColor));
        this.minion.setLeggings(Util.colorLeatherArmor(new ItemBuilder("", Material.LEATHER_LEGGINGS, 1).toItemStack(), this.leatherArmorColor));
        this.minion.setBoots(Util.colorLeatherArmor(new ItemBuilder("", Material.LEATHER_BOOTS, 1).toItemStack(), this.leatherArmorColor));

        this.i = 0;

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (minion == null || minion.isDead()) {
                    cancel();
                    return;
                }

                int ticksBetweenActions = getTimeBetweenActions.apply(level) * 20;

                if (i >= ticksBetweenActions) {
                    i = 0;

                    tick(player, location);
                } else {
                    i++;
                }
            }
        };

        this.task.runTaskTimer(Skyblock.getPlugin(Skyblock.class), 0, 1);
    }

    @Override
    public void pickup(SkyblockPlayer player, Location location) {

    }

    @Override
    public void showInventory(SkyblockPlayer player) {
    }

    @Override
    protected void tick(SkyblockPlayer player, Location location) {
        List<Block> blocks = new ArrayList<>();

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Block block = location.clone().add(x, -1, z).getBlock();

                blocks.add(block);
            }
        }

        List<Block> air = blocks.stream().filter(block -> block.getType().equals(Material.AIR)).collect(Collectors.toList());
        List<Block> ores = blocks.stream().filter(block -> block.getType().equals(this.type.getMaterial())).collect(Collectors.toList());

        if (air.size() != 0) {
            Block block = air.get(Skyblock.getPlugin().getRandom().nextInt(air.size()));

            Material toSet = this.type.getMaterial();

            block.setType(toSet);

            for (Player target : location.getWorld().getPlayers()) {
                PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(0, new BlockPosition(block.getX(), block.getY(), block.getZ()), 50);
                PacketPlayOutBlockChange packet1 = new PacketPlayOutBlockChange(((CraftWorld) location.getWorld()).getHandle(), new BlockPosition(block.getX(), block.getY(), block.getZ()));
                ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
                ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet1);
            }
        } else {
            Block block = ores.get(Skyblock.getPlugin().getRandom().nextInt(ores.size()));

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
                        }
                    }
                }.runTaskLater(Skyblock.getPlugin(Skyblock.class), i * 10);
            }
        }
    }
}
