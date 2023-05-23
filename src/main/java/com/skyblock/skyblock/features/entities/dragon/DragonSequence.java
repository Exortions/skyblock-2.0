package com.skyblock.skyblock.features.entities.dragon;

import com.sk89q.worldedit.EditSession;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Triple;
import com.skyblock.skyblock.utilities.Util;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class DragonSequence implements Listener {

    private static final Location center = new Location(Skyblock.getSkyblockWorld(), -671, 62, -277);
    private static final List<Triple<Material, Byte, Location>> needsRegenerating = new ArrayList<>();
    private static final List<Triple<Material, Byte, Location>> gateBlocks = new ArrayList<>();
    private static final List<FallingBlock> registered = new ArrayList<>();
    private static EditSession session;
    private static DragonGate dragonGate;

    @EventHandler
    public void EntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            FallingBlock block = (FallingBlock) event.getEntity();
            if (registered.contains(block)) event.setCancelled(true);
        }
    }

    public static void startSequence() {
        summoningEyeSequence();
        gateClose();

        Util.delay(() -> {
            playSound(Sound.ENDERDRAGON_DEATH, 2);

            removeEdgePanes();
            removePillar();
        }, 80);

        Util.delay(DragonSequence::explodeEgg, 180);
        Util.delay(DragonSequence::spawnDragon, 180);
        Util.delay(DragonSequence::launchPlayers, 180);
    }

    public static void endingSequence() {
        endingSequence(false);
    }

    public static void endingSequence(boolean slow) {
        try {
            openGate();

            for (Triple<Material, Byte, Location> triple : gateBlocks) {
                Material mat = triple.getFirst();
                byte data = triple.getSecond();
                Location loc = triple.getThird();

                Block b = loc.getWorld().getBlockAt(loc);

                b.setType(mat);
                b.setData(data);
            }

            if (slow) {
                HashMap<Double, List<Triple<Material, Byte, Location>>> regenByY = new HashMap<>();
                for (Triple<Material, Byte, Location> triple : needsRegenerating) {
                    if (!regenByY.containsKey(triple.getThird().getY())) regenByY.put(triple.getThird().getY(), new ArrayList<>());
                    regenByY.get(triple.getThird().getY()).add(triple);
                }

                int i = 0;
                for (List<Triple<Material, Byte, Location>> byY : regenByY.values()) {
                    Util.delay(() -> {
                        for (Triple<Material, Byte, Location> triple : byY) {
                            Material mat = triple.getFirst();
                            byte data = triple.getSecond();
                            Location loc = triple.getThird();

                            if (loc.getY() == 47) {
                                endingSequence(false);

                                Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "☬ " + ChatColor.LIGHT_PURPLE + "The Dragon Egg has spawned!");
                                return;
                            }

                            Block b = loc.getWorld().getBlockAt(loc);

                            b.setType(mat);
                            b.setData(data);
                        }
                    }, i * 30);

                    i++;
                }
            } else {
                for (Triple<Material, Byte, Location> triple : needsRegenerating) {
                    Material mat = triple.getFirst();
                    byte data = triple.getSecond();
                    Location loc = triple.getThird();

                    Block b = loc.getWorld().getBlockAt(loc);

                    b.setType(mat);
                    b.setData(data);
                }
            }

            needsRegenerating.clear();
            gateBlocks.clear();
            registered.clear();
        } catch (NullPointerException ignored) { }
    }

    private static void gateClose() {
        List<Block> gate = Util.blocksFromTwoPoints(new Location(Skyblock.getSkyblockWorld(), -602, 22, -280), new Location(Skyblock.getSkyblockWorld(), -597, 40, -272));

        for (Block block : gate) {
            if (block.getType().equals(Material.AIR)) continue;
            gateBlocks.add(Triple.of(block.getType(), block.getData(), block.getLocation()));
            block.setType(Material.AIR);
        }

        try {
            session = Util.pasteSchematic(new Location(Skyblock.getSkyblockWorld(), -595, 22, -276), "gate_closed");
        } catch (Exception ex) {
            session = null;

            Bukkit.broadcastMessage(ChatColor.RED + "Cannot summon dragon: Gate schematic not found or corrupted!");

            return;
        }

        dragonGate = new DragonGate();
        dragonGate.spawn();
    }

    public static void openGate() {
        session.undo(session);

        for (Triple<Material, Byte, Location> triple : gateBlocks) {
            Material mat = triple.getFirst();
            byte data = triple.getSecond();
            Location loc = triple.getThird();

            Block b = loc.getWorld().getBlockAt(loc);

            b.setType(mat);
            b.setData(data);
        }

        dragonGate.despawn();
    }

    public static void playSound(Sound sound, int pitch) {
        for (Player player : center.getWorld().getPlayers()) {
            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

            if (skyblockPlayer.getCurrentLocationName().equals("Dragon's Nest")) {
                player.playSound(player.getLocation(), sound, 100, pitch);
            }
        }
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

    private static void launchPlayers() {
        List<Player> players = Bukkit.getOnlinePlayers().stream().filter((p) -> SkyblockPlayer.getPlayer(p).getCurrentLocationName().equals("Dragon's Nest")).collect(Collectors.toList());

        for (Player player : players) {
            Vector vector = player.getLocation().getDirection().multiply(-1.5);
            vector.setY(2);

            player.setVelocity(vector);
        }
    }

    private static void removeEdgePanes() {
        List<Block> b = Util.blocksFromTwoPoints(new Location(Skyblock.getSkyblockWorld(), -662, 14, -268), new Location(Skyblock.getSkyblockWorld(), -680, 28, -285));
        HashMap<Double, List<Block>> blocks = new HashMap<>();

        for (Block block : b) {
            if (!blocks.containsKey(block.getLocation().getY())) blocks.put(block.getLocation().getY(), new ArrayList<>());
            blocks.get(block.getLocation().getY()).add(block);
        }

        int i = 0;
        for (List<Block> byY : blocks.values()) {
            Util.delay(() -> {
                for (Block block : byY) {
                    if (block.getType().equals(Material.STAINED_GLASS_PANE)) {
                        needsRegenerating.add(Triple.of(block.getType(), block.getData(), block.getLocation()));
                        block.setType(Material.AIR);
                    }
                }
            }, i);

            i++;
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

            if (exposed(block) && Util.random(0, 5) == 0) {
                FallingBlock falling = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                falling.setDropItem(false);

                registered.add(falling);
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

        Location l = center.clone();
        l.setY(47);
        for (int i = 0; i < 13; i++) center.getWorld().spawn(l.clone().add(0, i * 2, 0), TNTPrimed.class).setFuseTicks(0);
    }

    private static void summoningEyeSequence() {
        DragonAltar altar = DragonAltar.getMainAltar();

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
                    stand.setVelocity(new Vector(0, 0.025, 0));
                }
            }.runTaskTimer(Skyblock.getPlugin(), 1, 1);

            stand.setVisible(false);

            Util.delay(stand::remove, 80);
        }

        altar.reset();
    }

    private static boolean exposed(Block block) {
        return block.getLocation().clone().add(1, 0, 0).getBlock().getType().equals(Material.AIR) ||
            block.getLocation().clone().add(-1, 0, 0).getBlock().getType().equals(Material.AIR) ||
            block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.AIR) ||
            block.getLocation().clone().add(0, -1, 0).getBlock().getType().equals(Material.AIR) ||
            block.getLocation().clone().add(0, 0, 1).getBlock().getType().equals(Material.AIR) ||
            block.getLocation().clone().add(0, 0, -1).getBlock().getType().equals(Material.AIR);
    }
}
