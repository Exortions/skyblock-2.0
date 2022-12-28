package com.skyblock.skyblock.features.launchpads;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LaunchPadHandler {

    public static final String LAUNCHPAD_FILE_NAME = "launchpads.yml";

    private final File file;
    private final List<Player> onLaunchpad;

    public LaunchPadHandler() {
        this.file = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + LAUNCHPAD_FILE_NAME);
        this.onLaunchpad = new ArrayList<>();

        this.init();
    }

    public String closeTo(Player player) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ArrayList<String> pads = new ArrayList<>(config.getConfigurationSection("launchpads").getKeys(false));

        for (String pad : pads) {
            Location from = (Location) getField(pad, "from");
            if (player.getWorld().equals(from.getWorld()) && player.getLocation().distance(from) < 2) return pad;
        }

        return "NONE";
    }

//    public void savePad(String start, String end, Location from, Location to, Location teleport) {
//        Location infront = from.multiply(5);
//        infront.setY(from.getY() + 4);
//
//        String id = "launchpads." + start + "_to_" + end + ".";
//        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
//
//        config.set(id + "start", start);
//        config.set(id + "end", end);
//        config.set(id + "from", from);
//        config.set(id + "to", to);
//        config.set(id + "infront", infront);
//        config.set(id + "teleport", teleport);
//
//        try {
//            config.save(file);
//        } catch (IOException ignored) {}
//    }

    public void launch(Player player, String padName) {
        if (onLaunchpad.contains(player)) return;

        Location to = (Location) getField(padName, "to");
        Location front = (Location) getField(padName, "infront");
        Location teleport = (Location) getField(padName, "teleport");

        player.teleport(front);

        onLaunchpad.add(player);

        ArmorStand am = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        am.setVisible(false);
        am.setPassenger(player);

        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_HUGE, true, (float)player.getLocation().getBlockX(), (float)player.getLocation().getBlockY(), (float)player.getLocation().getBlockZ(), 0.0f, 0.0f, 0.0f, 0.0f, 2);
        for (Player p : player.getLocation().getWorld().getPlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            p.playSound(player.getLocation(), Sound.EXPLODE, 10, 2);
        }

        new BukkitRunnable() {
            final double x1 = 0.0;
            final double x3 = to.distance(player.getLocation()) - this.x1;
            final double x2 = this.x3 / 3.0;
            final double y1 = 0.0;
            final double y3 = Math.abs(to.getBlockY() - player.getLocation().getBlockY()) % 10;
            final double A3 = -((-this.x2 + this.x3) / (-this.x1 + this.x2)) * (-(this.x1 * this.x1) + this.x2 * this.x2) - this.x2 * this.x2 + this.x3 * this.x3;
            final double D3 = -((-this.x2 + this.x3) / (-this.x1 + this.x2)) * (-this.y1 + this.x2) - this.x2 + this.y3;
            final double a = this.D3 / this.A3;
            final double b = (-this.y1 + this.x2 - (-(this.x1 * this.x1) + this.x2 * this.x2) * this.a) / (-this.x1 + this.x2);
            final double c = this.y1 - this.a * this.x1 * this.x1 - this.b * this.x1;
            double xC = 0.0;

            public void run() {
                if (to.distance(am.getLocation()) < 2.0 || this.xC > 200.0 || !onLaunchpad.contains(player)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.teleport(teleport);
                            player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 10, 2);
                            SkyblockPlayer.getPlayer(player).setExtraData("lastSpawn", teleport);
                        }
                    }.runTask(Skyblock.getPlugin());

                    am.remove();
                    onLaunchpad.remove(player);
                    cancel();
                }
                moveToward(am, yCalculate(this.a, this.b, this.c, this.xC), to);
                this.xC += 0.84;
            }
        }.runTaskTimerAsynchronously(Skyblock.getPlugin(Skyblock.class), 1L, 1L);
    }

    private void moveToward(final Entity player, double yC, Location to) {
        final Location loc = player.getLocation();
        final double x = loc.getX() - to.getX();
        final double y = loc.getY() - to.getY() - (Math.max(yC, 0.0));
        final double z = loc.getZ() - to.getZ();
        final Vector velocity = new Vector(x, y, z).normalize().multiply(-0.8);
        player.setVelocity(velocity);
    }

    private double yCalculate(final double a, final double b, final double c, final double x) {
        return a * x * x + x * b + c;
    }

    public void init() {
        if (file.exists()) return;

        try {
            boolean success = file.createNewFile();

            if (!success) return;

            YamlConfiguration.loadConfiguration(file).save(file);
        } catch (Exception ignored) { }
    }

    public Object getField(String name, String field) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.get("launchpads." + name + "." + field);
    }

}
