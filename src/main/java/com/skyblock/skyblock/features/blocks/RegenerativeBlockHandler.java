package com.skyblock.skyblock.features.blocks;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class RegenerativeBlockHandler implements Listener {

    public static final HashMap<Material, Integer> resetTimes = new HashMap<Material, Integer>() {{
       put(Material.LOG, 20);
       put(Material.LOG_2, 20);
       put(Material.LEAVES, 20);
       put(Material.LEAVES_2, 20);
       put(Material.YELLOW_FLOWER, 8);
       put(Material.RED_ROSE, 8);
       put(Material.DOUBLE_PLANT, 8);
       put(Material.COAL_ORE, 7);
       put(Material.IRON_ORE, 7);
       put(Material.GOLD_ORE, 7);
       put(Material.LAPIS_ORE, 7);
       put(Material.REDSTONE_ORE, 7);
       put(Material.EMERALD_ORE, 7);
       put(Material.DIAMOND_ORE, 7);
       put(Material.DIAMOND_BLOCK, 7);
       put(Material.NETHERRACK, 7);
       put(Material.QUARTZ_ORE, 7);
       put(Material.GLOWSTONE, 7);
       put(Material.OBSIDIAN, 7);
       put(Material.ENDER_STONE, 7);
    }};

    public double getXpFromOre(Material material) {
        switch (material) {
            case COAL_ORE:
            case IRON_ORE:
            case QUARTZ_ORE:
                return 5;
            case GOLD_ORE:
                return 6;
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case GLOWSTONE:
                return 7;
            case EMERALD_ORE:
                return 9;
            case DIAMOND_ORE:
                return 10;
            case DIAMOND_BLOCK:
                return 15;
            case NETHERRACK:
                return 0.5;
            case OBSIDIAN:
                return 24;
            case ENDER_STONE:
                return 3;
            default:
                return 0;
        }
    }

    public RegenerativeBlockHandler() {
        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin(Skyblock.class));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock() == null || event.getBlock().getType().equals(Material.AIR)) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());
        Skyblock skyblock = Skyblock.getPlugin(Skyblock.class);

        if (!player.isNotOnPrivateIsland()) {
            event.setCancelled(false);
            return;
        }

        event.setCancelled(true);

        Block block = event.getBlock();

        if (!block.getType().equals(Material.COBBLESTONE)) player.setBrokenBlock(block);

        switch (block.getType()) {
            case STONE:
                if (block.getData() != 0) break;

                Skill.reward(Objects.requireNonNull(Skill.parseSkill("Mining")), 1.0, player);

                block.getDrops().forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation(), drop));
                block.setType(Material.COBBLESTONE);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.setType(Material.STONE);
                    }
                }.runTaskLater(skyblock, 140);
                break;
            case COBBLESTONE:
                Skill.reward(Objects.requireNonNull(Skill.parseSkill("Mining")), 1.0, player);

                block.getDrops().forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation(), drop));

                block.setType(Material.BEDROCK);

                AtomicReference<Material> setAfter = new AtomicReference<>(Material.COBBLESTONE);
                if (player.getBrokenBlock() != null && player.getBrokenBlock().getType().equals(Material.COBBLESTONE)) setAfter.set(Material.STONE);
                else if (player.getBrokenBlock() == null) setAfter.set(Material.BEDROCK);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.setType(setAfter.get());
                    }
                }.runTaskLater(skyblock, 140);
                break;
            case LOG:
            case LOG_2:
                this.breakNaturalBlock(event, block, player, "Foraging", 6);
                break;
            case DOUBLE_PLANT:
            case YELLOW_FLOWER:
            case RED_ROSE:
                this.breakNaturalBlock(event, block, player, "Foraging", 8);
                break;
            case LEAVES:
            case LEAVES_2:
                event.setCancelled(false);
                this.regenerateBlock(block.getType(), block.getData(), block.getType(), player.getBrokenBlock().getData(), block, resetTimes.get(block.getType()));
                break;
            case COAL_ORE:
            case IRON_ORE:
            case GOLD_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case EMERALD_ORE:
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
            case NETHERRACK:
            case QUARTZ_ORE:
            case GLOWSTONE:
            case OBSIDIAN:
            case ENDER_STONE:
                this.breakOre(event, block, player, this.getXpFromOre(block.getType()));
                break;
        }
    }

    public void breakOre(BlockBreakEvent event, Block block, SkyblockPlayer player, double xp) {
        Skill.reward(Objects.requireNonNull(Skill.parseSkill("Mining")), xp, player);
        event.setCancelled(false);

        Material type = block.getType();

        block.getDrops().forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation(), drop));
        block.setType(Material.BEDROCK);
        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(type);
            }
        }.runTaskLater(Skyblock.getPlugin(Skyblock.class), resetTimes.get(type));
    }

    public void breakNaturalBlock(BlockBreakEvent event, Block block, SkyblockPlayer player, String skill, double xp) {
        Skill.reward(Objects.requireNonNull(Skill.parseSkill(skill)), xp, player);
        event.setCancelled(false);
        this.regenerateBlock(block.getType(), block.getData(), block.getType(), player.getBrokenBlock().getData(), block, resetTimes.get(block.getType()));
        block.getDrops().forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation(), drop));
    }

    public void regenerateBlock(Material id, byte data, Material newId, byte newData, Block block, int seconds) {
        block.setType(id, true);
        block.setData(data);

        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(newId);
                block.setData(newData);
            }
        }.runTaskLater(Skyblock.getPlugin(Skyblock.class), 20L * seconds);
    }

}
