package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class YetiSword extends ListeningItem {

    private static final List<FallingBlock> registered = new ArrayList<>();
    public YetiSword() {
        super(plugin.getItemHandler().getItem("YETI_SWORD.json"), "yeti_sword");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        Player player = event.getPlayer();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.isOnCooldown(getInternalName())) {
            skyblockPlayer.sendOnCooldown(getInternalName());
            return;
        }

        if (skyblockPlayer.checkMana(250)) {
            Block below = player.getLocation().clone().subtract(0, 1, 0).getBlock();
            Block belowTwo = player.getLocation().clone().subtract(0, 2, 0).getBlock();

            Location loc = player.getLocation().clone().add(0, 3, 0);

            List<Location> tops = new ArrayList<>();
            List<Location> bottoms = new ArrayList<>();

            for (int i = 1; i < 3; i++) tops.add(loc.clone().add(i, 0, 0));
            for (int i = 1; i < 3; i++) tops.add(loc.clone().add(0, 0, i));
            for (int i = 1; i < 3; i++) tops.add(loc.clone().add(-i, 0, 0));
            for (int i = 1; i < 3; i++) tops.add(loc.clone().add(0, 0, -i));

            tops.add(loc.clone().add(1, 0, 1));
            tops.add(loc.clone().add(1, 0, -1));
            tops.add(loc.clone().add(-1, 0, 1));
            tops.add(loc.clone().add(-1, 0, -1));
            tops.add(loc.clone());

            bottoms.add(loc.clone().add(1, -1, 0));
            bottoms.add(loc.clone().add(-1, -1, 0));
            bottoms.add(loc.clone().add(0, -1, 1));
            bottoms.add(loc.clone().add(0, -1, -1));
            bottoms.add(loc.clone().add(0, -1, 0));

            Vector vector = player.getLocation().getDirection().multiply(0.8);
            vector.setY(0.5);

            List<FallingBlock> blocks = new ArrayList<>();

            if (belowTwo.getType().equals(Material.AIR)) belowTwo = below;

            for (Location top : tops) {
                FallingBlock block = loc.getWorld().spawnFallingBlock(top, below.getType(), below.getData());

                block.setDropItem(false);
                block.setHurtEntities(false);
                block.setVelocity(vector);

                blocks.add(block);
            }

            for (Location bottom : bottoms) {
                FallingBlock block = loc.getWorld().spawnFallingBlock(bottom, belowTwo.getType(), belowTwo.getData());

                block.setDropItem(false);
                block.setHurtEntities(false);
                block.setVelocity(vector);

                blocks.add(block);
            }

            skyblockPlayer.setCooldown("yeti_sword", 1);

            Util.sendAbility(skyblockPlayer, "Terrain Toss", 250);

            registered.addAll(blocks);
            markFalling(blocks, skyblockPlayer);
        }
    }

    @EventHandler
    public void EntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            FallingBlock block = (FallingBlock) event.getEntity();
            if (registered.contains(block)) event.setCancelled(true);
        }
    }

    private void markFalling(List<FallingBlock> blocks, SkyblockPlayer player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (FallingBlock block : blocks) {
                    if (block.isOnGround() || block.getLocation().clone().subtract(0, 2, 0).getBlock().getType() != Material.AIR) {
                        for (SkyblockEntity entity : Util.getNearbyEntities(block.getLocation(), 5, 5, 5)) {
                            entity.damage(Util.calculateAbilityDamage(1000, player.getStat(SkyblockStat.MAX_MANA), 0.3, player.getStat(SkyblockStat.ABILITY_DAMAGE)), player, false);
                        }

                        block.getWorld().playEffect(block.getLocation(), Effect.EXPLOSION_HUGE, 10);

                        blocks.forEach(Entity::remove);

                        cancel();
                    }
                }
            }
        }.runTaskTimer(Skyblock.getPlugin(), 1, 1);
    }
}
