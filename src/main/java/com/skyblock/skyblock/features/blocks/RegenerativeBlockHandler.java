package com.skyblock.skyblock.features.blocks;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerLogBreakEvent;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.features.skills.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("deprecation")
public class RegenerativeBlockHandler implements Listener {

    private static final HashMap<Location, BlockData> blocks = new HashMap<>();
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
       put(Material.STONE, 7);
        put(Material.COBBLESTONE, 7);
    }};

    private static final String[] mines = new String[]{"Coal Mine", "Gold Mine", "Deep Caverns", "Gunpowder Mines", "Lapis Quarry", "Pigman's Den", "Slimehill", "Diamond Reserve", "Obsidian Sanctuary"};
    private static final String[] farms = new String[]{"Farm", "The Barn", "Mushroom Desert"};
    private static final String[] forests = new String[]{"Forest", "The Park", "Birch Park", "Spruce Woods", "Dark Thicket", "Savanna Woodland", "Jungle Island"};
    private static final HashMap<Material, String[]> locations = new HashMap<Material, String[]>() {{
        put(Material.LOG, forests);
        put(Material.LOG_2, forests);
        put(Material.LEAVES, forests);
        put(Material.LEAVES_2, forests);
        put(Material.YELLOW_FLOWER, new String[]{"Forest", "The Park", "Flower House"});
        put(Material.RED_ROSE, new String[]{"Forest", "The Park", "Flower House"});
        put(Material.DOUBLE_PLANT, new String[]{"Forest", "The Park", "Flower House"});
        put(Material.STONE, mines);
        put(Material.COBBLESTONE, mines);
        put(Material.IRON_ORE, mines);
        put(Material.COAL_ORE, mines);
        put(Material.GOLD_ORE, mines);
        put(Material.LAPIS_ORE, mines);
        put(Material.REDSTONE_ORE, mines);
        put(Material.EMERALD_ORE, mines);
        put(Material.DIAMOND_ORE, mines);
        put(Material.DIAMOND_BLOCK, mines);
        put(Material.NETHERRACK, new String[] {"Blazing Fortress"});
        put(Material.QUARTZ_ORE, new String[] {"Blazing Fortress"});
        put(Material.GLOWSTONE, new String[] {"Blazing Fortress"});
        put(Material.OBSIDIAN, new String[] {"Coal Mine", "Gold Mine", "Deep Caverns", "Gunpowder Mines", "Lapis Quarry", "Pigman's Den", "Slimehill", "Diamond Reserve", "Obsidian Sanctuary", "Dragon's Den", "The End"});
        put(Material.ENDER_STONE, new String[]{"The End"});
        put(Material.CROPS, farms);
        put(Material.CARROT, new String[] {"Farm", "The Barn", "Mushroom Desert", "Mountain"});
        put(Material.POTATO, farms);
        put(Material.PUMPKIN, farms);
        put(Material.MELON_BLOCK, farms);
    }};

    @Getter
    @AllArgsConstructor
    private static class BlockData {
        private Material material;
        private byte data;
    }

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
            return;
        }

        event.setCancelled(true);

        Block block = event.getBlock();

        if (!block.getType().equals(Material.COBBLESTONE)) player.setBrokenBlock(block);

        if (!blocks.containsKey(block.getLocation())) blocks.put(block.getLocation(), new BlockData(block.getType(), block.getData()));

        SkyblockLocation location = Skyblock.getPlugin().getLocationManager().getLocation(block.getLocation());
        if (location == null) return;
        if (locations.get(block.getType()) == null) return;
        if (!Arrays.asList(locations.get(block.getType())).contains(location.getName())) return;

        boolean hasTelekinesis = player.hasTelekinesis();

        switch (block.getType()) {
            case STONE:
                if (block.getData() != 0) break;

                Skill.reward(Objects.requireNonNull(Skill.parseSkill("Mining")), 1.0, player);

                if (!hasTelekinesis) block.getDrops().forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation(), drop));
                else {
                    for (ItemStack drop : block.getDrops()) {
                        Item item = block.getWorld().dropItem(block.getLocation().add(0, block.getLocation().getY() + (255 - block.getLocation().getY()), 0), drop);

                        Bukkit.getPluginManager().callEvent(new PlayerPickupItemEvent(player.getBukkitPlayer(), item, 0));

                        item.remove();
                    }
                }
                block.setType(Material.COBBLESTONE);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.setType(blocks.get(block.getLocation()).getMaterial());
                        block.setData(blocks.get(block.getLocation()).getData());
                    }
                }.runTaskLater(skyblock, resetTimes.get(blocks.get(block.getLocation()).getMaterial()) * 20);
                break;
            case COBBLESTONE:
                Skill.reward(Objects.requireNonNull(Skill.parseSkill("Mining")), 1.0, player);

                if (!hasTelekinesis) block.getDrops().forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation(), drop));
                else {
                    for (ItemStack drop : block.getDrops()) {
                        Item item = block.getWorld().dropItem(block.getLocation().add(0, block.getLocation().getY() + (255 - block.getLocation().getY()), 0), drop);

                        Bukkit.getPluginManager().callEvent(new PlayerPickupItemEvent(player.getBukkitPlayer(), item, 0));

                        item.remove();
                    }
                }

                block.setType(Material.BEDROCK);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.setType(blocks.get(block.getLocation()).getMaterial());
                        block.setData(blocks.get(block.getLocation()).getData());
                    }
                }.runTaskLater(skyblock, resetTimes.get(blocks.get(block.getLocation()).getMaterial()) * 20);
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
                this.breakOre(event, block, player, this.getXpFromOre(block.getType()));
                break;
            case NETHERRACK:
            case QUARTZ_ORE:
            case GLOWSTONE:
            case OBSIDIAN:
            case ENDER_STONE:
                this.breakOre(event, block, player, this.getXpFromOre(block.getType()), Material.BEDROCK);
                break;
            case CROPS:
            case CARROT:
            case MELON_BLOCK:
            case POTATO:
                this.breakCrop(event, block, player, 4);
                break;
            case PUMPKIN:
                this.breakCrop(event, block, player, 5);
                break;
            default:
                break;
        }
    }

    public void breakCrop(BlockBreakEvent event, Block block, SkyblockPlayer player, double xp) {
        Skill.reward(Objects.requireNonNull(Skill.parseSkill("Farming")), xp, player);
        event.setCancelled(false);

        dropOverridenDrops(block, player);
    }

    public void breakOre(BlockBreakEvent event, Block block, SkyblockPlayer player, double xp) {
        breakOre(event, block, player, xp, Material.STONE);
    }

    public void breakOre(BlockBreakEvent event, Block block, SkyblockPlayer player, double xp, Material next) {
        Skill.reward(Objects.requireNonNull(Skill.parseSkill("Mining")), xp, player);
        event.setCancelled(true);

        Material type = block.getType();

        dropOverridenDrops(block, player);

        block.setType(next);
        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(blocks.get(block.getLocation()).getMaterial());
                block.setData(blocks.get(block.getLocation()).getData());
            }
        }.runTaskLater(Skyblock.getPlugin(Skyblock.class), resetTimes.get(type) * 20);
    }

    private void dropOverridenDrops(Block block, SkyblockPlayer player) {
        if (hasOverridenDrops(block, player)) {
            player.dropItems(Arrays.asList(getOverrideDrops(block, player)), block.getLocation());
        } else {
            if (!player.hasTelekinesis()) {
                block.getDrops().forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation(), drop));
            } else {
                for (ItemStack drop : block.getDrops()) {
                    Item item = block.getWorld().dropItem(block.getLocation().add(0, block.getLocation().getY() + (255 - block.getLocation().getY()), 0), drop);

                    Bukkit.getPluginManager().callEvent(new PlayerPickupItemEvent(player.getBukkitPlayer(), item, 0));

                    item.remove();
                }
            }
        }
    }

    public void breakNaturalBlock(BlockBreakEvent event, Block block, SkyblockPlayer player, String skill, double xp) {
        if (block.getType().equals(Material.LOG) || block.getType().equals(Material.LOG_2)) {
            Bukkit.getPluginManager().callEvent(new SkyblockPlayerLogBreakEvent(player, block));
        }

        Skill.reward(Objects.requireNonNull(Skill.parseSkill(skill)), xp, player);
        event.setCancelled(false);

        dropOverridenDrops(block, player);

        this.regenerateBlock(block.getType(), block.getData(), block.getType(), player.getBrokenBlock().getData(), block, resetTimes.get(block.getType()));
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

    private boolean hasOverridenDrops(Block block, SkyblockPlayer player) {
        return ((HashMap<String, ItemStack[]>) player.getExtraData("dropOverrides")).containsKey(block.getType() + ":" + block.getData());
    }

    private ItemStack[] getOverrideDrops(Block block, SkyblockPlayer player) {
        return ((HashMap<String, ItemStack[]>) player.getExtraData("dropOverrides")).get(block.getType() + ":" + block.getData());
    }
}
