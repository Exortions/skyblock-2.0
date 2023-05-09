package com.skyblock.skyblock.features.entities.dragon;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerDamageByEntityEvent;
import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.TextUtil;
import com.skyblock.skyblock.utilities.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Dragon extends SkyblockEntity implements Listener {
    @Getter
    @AllArgsConstructor
    public enum DragonType {

        PROTECTOR(9000000, 1100, 85, 1.4),
        OLD(15000000, 1100, 18, 1.3),
        WISE(9000000, 2200, 25, 1.4),
        UNSTABLE(9000000, 1100, 25, 1.4),
        YOUNG(7500000, 1100, 18, 2.25),
        STRONG(9000000, 1100, 25, 1.4),
        SUPERIOR(12000000, 1650, 30, 1.8);

        private final int health;
        private final int damage;
        private final int defense; // currently unused
        private final double speedMultiplier;
    }

    private final HashMap<UUID, Integer> damagers = new HashMap<>();
    private final DragonType type;
    private final boolean appliedSpeed;
    private boolean rushing;
    private boolean frozen;
    public Dragon(String arg) {
        super(EntityType.ENDER_DRAGON);

        this.type = DragonType.valueOf(arg);

        loadStats(type.getHealth(), type.getDamage(), false, false, false, new Equipment(), WordUtils.capitalize(type.name().toLowerCase()) + " Dragon", 100, 0, "");

        this.lifeSpan = 12000 * 60;
        this.appliedSpeed = false;
        this.frozen = false;

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin());
    }

    @Override
    protected void tick() {
        EnderDragon dragon = (EnderDragon) getVanilla();
        EntityEnderDragon nms = ((CraftEnderDragon) dragon).getHandle();

        if (frozen) {
            dragon.setVelocity(new Vector(0, 0, 0));
            return;
        }

        if (tick == 0){
            updateSpeed(nms);

            try
            {
                Field b = PathfinderGoalSelector.class.getDeclaredField("b");
                Field c = PathfinderGoalSelector.class.getDeclaredField("c");
                b.setAccessible(true);
                c.setAccessible(true);
                ((List<?>) b.get(nms.goalSelector)).clear();
                ((List<?>) c.get(nms.goalSelector)).clear();
                ((List<?>) b.get(nms.targetSelector)).clear();
                ((List<?>) c.get(nms.targetSelector)).clear();
            }
            catch (NoSuchFieldException | IllegalAccessException ex)
            {
                ex.printStackTrace();
            }
        }

        dragon.setCustomNameVisible(false);
        dragon.setCustomName(ChatColor.RED + getEntityData().entityName);

        if (getEntityData().health >= 0) {
            dragon.setHealth(getEntityData().health / 100000f);
            dragon.setMaxHealth(type.health / 100000f);
        }

        if (tick % 500 == 0 && tick != 0) {
            switch (Util.random(0, 2)) {
                case 0:
                    fireBall(dragon);
                    break;
                case 1:
                    lightningStrike();
                    break;
                case 2:
                    rush(dragon);
                    break;
            }
        }

        lifeSpan = 12000 * 60;
    }

    private void fireBall(EnderDragon dragon) {
        List<Player> players = Bukkit.getOnlinePlayers().stream().filter((p) -> SkyblockPlayer.getPlayer(p).getCurrentLocationName().equals("Dragon's Nest")).collect(Collectors.toList());

        Player target = players.get(Util.random(0, players.size() - 1));

        freezeFor(60);

        for (int i = 0; i < 25; i++) {
            Util.delay(() -> {
                Fireball fire = dragon.launchProjectile(Fireball.class);
                fire.setDirection(target.getLocation().toVector().subtract(dragon.getLocation().toVector()).normalize().add(new Vector(Util.random(0, 0.1), Util.random(0, 0.1), Util.random(0, 0.1))));
                fire.setIsIncendiary(false);
                fire.setYield(0f);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (fire.isDead() || fire.isOnGround()) {
                            for (Entity e : fire.getNearbyEntities(5, 5, 5)) {
                                if (e instanceof Player) {
                                    SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) e);

                                    double damage = Util.triangularDistribution(300, 500, 700);

                                    if (type.equals(DragonType.WISE)) damage += damage * 0.2;
                                    if (type.equals(DragonType.SUPERIOR)) damage += damage * 0.15;

                                    Vector dir = player.getBukkitPlayer().getLocation().getDirection().normalize();

                                    dir.setY(3);

                                    player.getBukkitPlayer().setVelocity(dir);

                                    player.getBukkitPlayer().sendMessage(ChatColor.DARK_PURPLE + "☬ " + ChatColor.RED + getEntityData().entityName + " " + ChatColor.LIGHT_PURPLE + "used " + ChatColor.YELLOW + "Fireball " + ChatColor.LIGHT_PURPLE + "on you for " + ChatColor.RED + (int) damage + " damage.");
                                    player.damage(damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getVanilla(), true);
                                }
                            }

                            fire.remove();
                            cancel();
                        }
                    }
                }.runTaskTimer(Skyblock.getPlugin(), 1, 1);
            }, i);
        }
    }

    private void lightningStrike() {
        List<Player> players = Bukkit.getOnlinePlayers().stream().filter((p) -> SkyblockPlayer.getPlayer(p).getCurrentLocationName().equals("Dragon's Nest")).collect(Collectors.toList());

        freezeFor(60);

        for (int i = 0; i < 5; i++) {
            Util.delay(() -> {
                getVanilla().getWorld().strikeLightningEffect(getVanilla().getLocation());
            }, 10 * i);
        }

        Util.delay(() -> {
            double damage = Util.triangularDistribution(300, 500, 700);
            if (type.equals(DragonType.WISE)) damage += damage * 0.2;
            if (type.equals(DragonType.SUPERIOR)) damage += damage * 0.15;

            for (Player player : players) {
                player.getWorld().strikeLightningEffect(player.getLocation());

                SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
                skyblockPlayer.damage(damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getVanilla(), true);
                player.sendMessage(ChatColor.DARK_PURPLE + "☬ " + ChatColor.RED + getEntityData().entityName + " " + ChatColor.LIGHT_PURPLE + "used " + ChatColor.YELLOW + "Lightning Strike " + ChatColor.LIGHT_PURPLE + "on you for " + ChatColor.RED + (int) damage + " damage.");
            }
        }, 50);
    }

    private void rush(EnderDragon dragon) {
        List<Player> players = Bukkit.getOnlinePlayers().stream().filter((p) -> SkyblockPlayer.getPlayer(p).getCurrentLocationName().equals("Dragon's Nest")).collect(Collectors.toList());

        Player target = players.get(Util.random(0, players.size() - 1));

        dragon.setVelocity(target.getLocation().toVector().subtract(dragon.getLocation().toVector()).normalize());
        this.rushing = true;
    }

    private void updateSpeed(EntityEnderDragon nms) {
        AttributeInstance speed = nms.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        speed.setValue(speed.getValue() * type.getSpeedMultiplier());
    }

    private void freezeFor(int ticks) {
        frozen = true;

        Util.delay(() -> frozen = false, ticks);
    }

    private String getPlace(int place) {
        try {
            return (getDamaged().get(place - 1) != null ? getDamaged().get(place - 1).getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + damagers.get(getDamaged().get(place - 1).getUniqueId()) : ChatColor.RED + "N/A " + ChatColor.GRAY + "- " + ChatColor.RED + "N/A");
        } catch (IndexOutOfBoundsException e) {
            return ChatColor.RED + "N/A " + ChatColor.GRAY + "-" + ChatColor.RED + " N/A";
        }
    }

    @Override
    protected void onDeath() {
        super.onDeath();

        List<Player> players = Bukkit.getOnlinePlayers().stream().filter((p) -> SkyblockPlayer.getPlayer(p).getCurrentLocationName().equals("Dragon's Nest")).collect(Collectors.toList());
        getDamaged().sort((p1, p2) -> Math.toIntExact(damagers.get(p1.getUniqueId()) - damagers.get(p2.getUniqueId())));

        for (Player p : players) {
            p.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            TextUtil.sendCenteredMessage(p, "" + ChatColor.GOLD + ChatColor.BOLD + getType().name() + " DRAGON DOWN!");
            p.sendMessage("");
            TextUtil.sendCenteredMessage(p, "" + ChatColor.GREEN + (getLastDamager() != null ? getLastDamager().getBukkitPlayer().getName() : "N/A") + ChatColor.GRAY + " dealt the final blow.");
            p.sendMessage("");
            TextUtil.sendCenteredMessage(p, "" + ChatColor.YELLOW + ChatColor.BOLD + "1st Damager " + ChatColor.GREEN + "- " + getPlace(1));
            TextUtil.sendCenteredMessage(p, "" + ChatColor.GOLD + ChatColor.BOLD + "2nd Damager " + ChatColor.GREEN + "- " + getPlace(2));
            TextUtil.sendCenteredMessage(p, "" + ChatColor.RED + ChatColor.BOLD + "3rd Damager " + ChatColor.GREEN + "- " + getPlace(3));
            p.sendMessage(" ");
            TextUtil.sendCenteredMessage(p, "" + ChatColor.YELLOW + "Your Damage: " + ChatColor.GREEN + (damagers.containsKey(p.getUniqueId()) ? Util.formatInt(damagers.get(p.getUniqueId())) : ChatColor.RED + "N/A ") + ChatColor.GRAY + " (Position #" + (getDamaged().indexOf(p) + 1) + ")");
            TextUtil.sendCenteredMessage(p, "" + ChatColor.YELLOW + "Runecrafting Experience: " + ChatColor.LIGHT_PURPLE + 0);
            p.sendMessage(" ");
            p.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }

        DragonSequence.endingSequence(true);

        if (type.equals(DragonType.UNSTABLE)) {
            for (Entity entity : getVanilla().getNearbyEntities(10, 10, 10)) {
                if (!(entity instanceof Player)) return;

                double damage = Util.triangularDistribution(1900, 2000, 2100);

                SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) entity);
                player.damage(damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getVanilla());
                player.getBukkitPlayer().sendMessage(ChatColor.DARK_PURPLE + "☬ " + ChatColor.RED + getEntityData().entityName + " " + ChatColor.LIGHT_PURPLE + "used " + ChatColor.YELLOW + "Implosion " + ChatColor.LIGHT_PURPLE + "on you for " + ChatColor.RED + (int) damage + " damage.");
            }
        }
    }

    @EventHandler
    public void onDamage(SkyblockPlayerDamageByEntityEvent e) {
        if (e.getEntity() == null) return;
        if (!e.getEntity().getVanilla().equals(getVanilla())) return;

        Player player = e.getPlayer().getBukkitPlayer();

        if (isRushing()) {
            double damage = Util.triangularDistribution(300, 500, 700);

            if (type.equals(DragonType.STRONG)) damage += damage * 0.2;
            if (type.equals(DragonType.SUPERIOR)) damage += damage * 0.15;

            e.setDamage(damage);
            e.setTrueDamage(true);
            player.sendMessage(ChatColor.DARK_PURPLE + "☬ " + ChatColor.RED + getEntityData().entityName + " " + ChatColor.LIGHT_PURPLE + "used " + ChatColor.YELLOW + "Rush " + ChatColor.LIGHT_PURPLE + "on you for " + ChatColor.RED + (int) damage + " damage.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 10, 1);

            Vector dir = player.getLocation().getDirection().normalize();

            dir.setY(2);

            player.setVelocity(dir);

            this.rushing = false;
        }
    }

    @EventHandler
    public void onDamage(SkyblockPlayerDamageEntityEvent e) {
        if (!e.getEntity().getVanilla().equals(getVanilla())) return;

        getDamaged().add(e.getPlayer().getBukkitPlayer());
        if (type.equals(DragonType.PROTECTOR)) e.setDamage(e.getDamage() / 2f);

        UUID id = e.getPlayer().getBukkitPlayer().getUniqueId();
        if (!getDamagers().containsKey(id)) getDamagers().put(id, 0);

        getDamagers().put(id, (int) (getDamagers().get(id) + e.getDamage()));
    }
}
