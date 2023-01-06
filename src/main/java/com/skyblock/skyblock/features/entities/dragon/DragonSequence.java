package com.skyblock.skyblock.features.entities.dragon;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Triple;
import com.skyblock.skyblock.utilities.Util;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class DragonSequence implements Listener {

    private static final Location center = new Location(Skyblock.getSkyblockWorld(), -671, 62, -277);
    private static final List<Triple<Material, Byte, Location>> needsRegenerating = new ArrayList<>();
    private static List<FallingBlock> registered = new ArrayList<>();

    @EventHandler
    public void EntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            FallingBlock block = (FallingBlock) event.getEntity();
            if (registered.contains(block)) event.setCancelled(true);
        }
    }

    public static void startSequence() {
        summoningEyeSequence();

        Util.delay(() -> {
            List<Block> blocks = Util.blocksFromTwoPoints(new Location(Skyblock.getSkyblockWorld(), -662, 13, -268), new Location(Skyblock.getSkyblockWorld(), -680, 45, -285));

            for (Block block : blocks) {
                if (block.getType().equals(Material.STAINED_GLASS_PANE)) {
                    needsRegenerating.add(Triple.of(block.getType(), block.getData(), block.getLocation()));
                    block.setType(Material.AIR);
                }
            }

            removePillar();
        }, 60);

        Util.delay(DragonSequence::explodeEgg, 100);

        Util.delay(DragonSequence::spawnDragon, 100);
    }

    public static void spawnDragon() {
        Dragon.DragonType type;
        int rand = Util.random(0, 100);

        if (inRange(rand, 0, 16)) type = Dragon.DragonType.PROTECTOR;
        else if (inRange(rand, 16, 32)) type = Dragon.DragonType.OLD;
        else if (inRange(rand, 32, 48)) type = Dragon.DragonType.WISE;
        else if (inRange(rand, 48, 64)) type = Dragon.DragonType.UNSTABLE;
        else if (inRange(rand, 64, 80)) type = Dragon.DragonType.YOUNG;
        else if (inRange(rand, 80, 96)) type = Dragon.DragonType.STRONG;
        else type = Dragon.DragonType.SUPERIOR;

        Dragon dragon = new Dragon(type.name());

        dragon.spawn(center);

        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "☬ " + ChatColor.BOLD + ChatColor.LIGHT_PURPLE + "The " + ChatColor.RESET + ChatColor.RED + WordUtils.capitalize(dragon.getType().name().toLowerCase()) + " Dragon " + ChatColor.BOLD + ChatColor.LIGHT_PURPLE + "has spawned!");
    }

    private static boolean inRange(int i, int min, int max) {
        return i >= min && i < max;
    }

    public static void endingSequence() {
        for (Triple<Material, Byte, Location> triple : needsRegenerating) {
            Material mat = triple.getFirst();
            byte data = triple.getSecond();
            Location loc = triple.getThird();

            Block b = loc.getWorld().getBlockAt(loc);

            b.setType(mat);
            b.setData(data);
        }
    }

    private static void removePillar() {
        List<Block> blocks = Util.blocksFromTwoPoints(new Location(Skyblock.getSkyblockWorld(), -671, 9, -276), new Location(Skyblock.getSkyblockWorld(), -671, 45, -276));
        List<Block> pillar = new ArrayList<>();

        for (Block block : blocks) {
            if (!block.getType().equals(Material.STAINED_GLASS)) continue;
            pillar.add(block);
        }

        pillar.sort((o1, o2) -> (int) (o1.getLocation().getY() - o2.getLocation().getY()));

        for (int i = 0; i < pillar.size(); i++) {
            Block block = pillar.get(i);

            Util.delay(() -> {
                needsRegenerating.add(Triple.of(block.getType(), block.getData(), block.getLocation()));
                block.setType(Material.AIR);
            }, i);
        }
    }

    private static void explodeEgg() {
        List<Block> blocks = Util.blocksFromTwoPoints(new Location(Skyblock.getSkyblockWorld(), -661, 46, -266), new Location(Skyblock.getSkyblockWorld(), -679, 81, -284));

        center.getWorld().playEffect(center, Effect.EXPLOSION_HUGE, 10);
        center.getWorld().playEffect(center.clone().add(0, 5, 0), Effect.EXPLOSION_HUGE, 10);
        center.getWorld().playEffect(center.clone().subtract(0, 5, 0), Effect.EXPLOSION_HUGE, 10);

        for (Player player : center.getWorld().getPlayers()) {
            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

            if (skyblockPlayer.getCurrentLocationName().equals("Dragon's Nest")) {
                player.playSound(player.getLocation(), Sound.EXPLODE, 100, 1);
                player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 100, 1);
            }
        }

        for (Block block : blocks) {
            if (block.getType().equals(Material.AIR)) continue;
            needsRegenerating.add(Triple.of(block.getType(), block.getData(), block.getLocation()));

            if (exposed(block)) {
                FallingBlock falling = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                falling.setDropItem(false);

                registered.add(falling);

                Vector velocity = center.toVector().subtract(block.getLocation().toVector()).multiply(-1).normalize();
                velocity.multiply(4);
                velocity.setY(3);

                falling.setVelocity(velocity);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!falling.getLocation().clone().subtract(0, 2, 0).getBlock().getType().equals(Material.AIR)) {
                            falling.remove();
                            cancel();
                        }
                    }
                }.runTaskTimer(Skyblock.getPlugin(), 1, 1);
            }

            block.setType(Material.AIR);
        }
    }

    private static void summoningEyeSequence() {
        DragonAltar altar = DragonAltar.getMainAltar();

        for (Player player : center.getWorld().getPlayers()) {
            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

            if (skyblockPlayer.getCurrentLocationName().equals("Dragon's Nest")) {
                player.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 100, 1);
            }
        }

        for (Location l : altar.getFrames()) {
            Location loc = l.clone();
            loc.setPitch(-90);
            loc.subtract(0, 1, 0);

            ArmorStand stand = l.getWorld().spawn(loc, ArmorStand.class);
            stand.setHelmet(Util.idToSkull(new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOGU3ZjJkYjhlYWE4OGEwNDFjODlkNGMzNTNkMDY2Y2M0ZWRlZjc3ZWRjZjVlMDhiYjVkM2JhYWQifX19"));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (stand.isDead()) cancel();
                    stand.setVelocity(new Vector(0, 0.1, 0));
                }
            }.runTaskTimer(Skyblock.getPlugin(), 1, 1);

            stand.setVisible(false);

            Util.delay(stand::remove, 60);
        }

        altar.reset();
    }

    private static boolean exposed(Block block) {
        Location pos1 = block.getLocation().clone().add(1, 1, 1);
        Location pos2 = block.getLocation().clone().add(-1, -1, -1);

        for (Block b : Util.blocksFromTwoPoints(pos1, pos2)) if (b.getType().equals(Material.AIR)) return true;

        return false;
    }
}
