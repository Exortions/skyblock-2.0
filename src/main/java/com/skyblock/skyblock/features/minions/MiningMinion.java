package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.MiningMinionType;
import com.skyblock.skyblock.features.crafting.SkyblockCraftingRecipe;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.minions.items.MinionItemType;
import com.skyblock.skyblock.features.minions.items.MinionItem;
import com.skyblock.skyblock.features.minions.items.MinionFuel;
import com.skyblock.skyblock.features.minions.items.storages.Storage;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;

import de.tr7zw.nbtapi.NBTItem;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MiningMinion extends MinionBase {
    public MiningMinion() {
        this(UUID.randomUUID());
    }
    public MiningMinion(UUID uuid) {
        super(uuid, "Cobblestone", "mining", Color.GRAY, Material.COBBLESTONE);

        this.plugin = Skyblock.getPlugin();
    }

    @Override
    public SkyblockCraftingRecipe getRecipe(int level) {
        if (level == 1) {
            return new SkyblockCraftingRecipe(new HashMap<String, ItemStack>() {{
                put("a", new ItemStack(Material.COBBLESTONE, 1));
            }});
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getHand(int level) {
        return new ItemStack(Material.WOOD_PICKAXE, 1);
    }
    
    @Override
    public String getHead(int level) {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjljMzhmZTRmYzk4YTI0ODA3OWNkMDRjNjViNmJmZjliNDUwMTdmMTY0NjBkYWIzYzM0YzE3YmZjM2VlMWQyZiJ9fX0=";
    }

    @Override
    public int getActionDelay(int level) {
        return 14;

        //FUEL HERE
    }

    @Override
    public int getMaxStorage(int level) {
        switch (level) {
            case 1:
                return 1;
            case 2:
            case 3:
                return 3;
            case 4:
            case 5:
                return 6;
            case 6:
            case 7:
                return 9;
            case 8:
            case 9:
                return 12;
            default:
                return 15;
        }
    }

    @Override
    public ArrayList<ItemStack> calculateDrops(int level) {
        return new ArrayList<ItemStack>(Arrays.asList(new ItemStack(Material.COBBLESTONE)));
        
    }

    @Override
    public int getSlotLevelRequirement(int slot) {
        switch (slot) {
            case 0:
                return 1;
            case 1:
            case 2:
                return 2;
            case 3:
            case 4:
            case 5:
                return 4;
            case 6:
            case 7:
            case 8:
                return 6;
            case 9:
            case 10:
            case 11:
                return 8;
            case 12:
            case 13:
            case 14:
                return 10;
            default:
                return 0;
        }
    }

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

        for (int i = 0; i < minionItems.length; ++i) {
            if (minionItems[i] != null && minionItems[i] instanceof MinionFuel) {
                if (fuelAddedTime + ((MinionFuel) minionItems[i]).duration < Math.floor(System.currentTimeMillis() / 60000) && --fuelAmount < 1) {
                    minionItems[i].onUnEquip(this);
                    minionItems[i] = null;
                }
                break;
            }
        }

        for (int i = 0; i < minionItems.length; ++i) {
            if (minionItems[i] != null) minionItems[i].onTick(this);
        }
    }
}
