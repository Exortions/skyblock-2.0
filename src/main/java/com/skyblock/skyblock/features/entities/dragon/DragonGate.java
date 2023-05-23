package com.skyblock.skyblock.features.entities.dragon;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DragonGate implements Listener {

    private final int maxHealth = 1000000;
    private int health = maxHealth;
    private NPC npc;
    private NPC healthDisplay;

    public DragonGate() {
        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin());
    }

    public void spawn() {
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARMOR_STAND, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Dragon's Gate", new Location(Skyblock.getSkyblockWorld(), -594.5, 26, -275.5));
        ArmorStandTrait stand = npc.getOrAddTrait(ArmorStandTrait.class);

        stand.setMarker(true);
        stand.setGravity(false);
        stand.setVisible(false);

        healthDisplay = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARMOR_STAND, "", new Location(Skyblock.getSkyblockWorld(), -594.5, 25.5, -275.5));
        ArmorStandTrait healthStand = healthDisplay.getOrAddTrait(ArmorStandTrait.class);

        healthStand.setMarker(true);
        healthStand.setGravity(false);
        healthStand.setVisible(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (healthDisplay.getEntity() == null || healthDisplay.getEntity().isDead()) {
                    cancel();
                    return;
                }
                healthDisplay.getEntity().setCustomName(getHealthDisplay());
            }
        }.runTaskTimer(Skyblock.getPlugin(), 1, 1);
    }

    public void despawn() {
        npc.getEntity().remove();
        npc.destroy();

        healthDisplay.getEntity().remove();
        healthDisplay.destroy();
    }

    private String getHealthDisplay() {
        double filled = Math.floor(20 * (1.0f * health / maxHealth));
        StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.GRAY).append("[");

        for (int i = 0; i < filled; i++) builder.append(ChatColor.RED).append("|");
        for (int i = 0; i < (20 - filled); i++) builder.append(ChatColor.GRAY).append("|");

        builder.append(ChatColor.GRAY).append("]");

        return builder.toString();
    }

    @EventHandler
    public void onHit(PlayerInteractEvent e) {
        if (health <= 0) return;

        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            List<Block> gate = Util.blocksFromTwoPoints(new Location(Skyblock.getSkyblockWorld(), -602, 22, -280), new Location(Skyblock.getSkyblockWorld(), -597, 40, -272));

            if (!gate.contains(e.getClickedBlock())) return;

            Player p = e.getPlayer();
            SkyblockPlayer player = SkyblockPlayer.getPlayer(p);
            double damage = 5 + player.getStat(SkyblockStat.DAMAGE) + (player.getStat(SkyblockStat.STRENGTH) / 5F) * (1 + player.getStat(SkyblockStat.STRENGTH) / 100F);
            boolean crit = player.crit();

            double combat = 4 * Skill.getLevel((double) player.getValue("skill.combat.exp"));

            damage += damage * (combat / 100F);

            damage = damage * (1 + player.getStat(SkyblockStat.CRIT_DAMAGE) / 100F);

            if (crit)
                Util.setDamageIndicator(e.getClickedBlock().getLocation().clone().add(1, 0, 0), Util.addCritTexture((int) Math.round(damage)), false);
            else
                Util.setDamageIndicator(e.getClickedBlock().getLocation().clone().add(1, 0, 0), ChatColor.GRAY + "" + Math.round(damage), true);

            p.getWorld().playEffect(e.getClickedBlock().getLocation().clone().add(1, 0, 0), Effect.PARTICLE_SMOKE, 10);
            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 2);

            health -= damage;

            if (health <= 0) {
                despawn();

                DragonSequence.openGate();
            }
        }
    }
}
