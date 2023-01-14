package com.skyblock.skyblock.listeners;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerAbilityTriggerEvent;
import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import com.skyblock.skyblock.features.items.SkyblockItemHandler;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ItemListener implements Listener {

    private final Skyblock plugin;
    private final SkyblockItemHandler handler;

    public ItemListener(Skyblock skyblock) {
        this.plugin = skyblock;
        this.handler = plugin.getSkyblockItemHandler();
    }

    @EventHandler
    public void onBlock(BlockBreakEvent e) {
        try {
            ItemStack item = e.getPlayer().getItemInHand();

            if (item == null) return;

            if (handler.isRegistered(item)) {
                handler.getRegistered(item).onBlockBreak(e);
            }
        } catch (UnsupportedOperationException ignored) { }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        try {
            if (e.getDamager() instanceof Player) {
                Player player = (Player) e.getDamager();

                ItemStack item = player.getItemInHand();

                if (handler.isRegistered(item)) {
                    handler.getRegistered(item).onEntityDamage(e);
                }
            } else if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    Player player = (Player) arrow.getShooter();

                    ItemStack item = player.getItemInHand();

                    if (handler.isRegistered(item)) {
                        handler.getRegistered(item).onEntityDamage(e);
                    }
                }
            }
        } catch (UnsupportedOperationException ignored) { }
    }

    @EventHandler
    public void onDamage(SkyblockPlayerDamageEntityEvent e){
        try {
            Player player = e.getPlayer().getBukkitPlayer();

            ItemStack item = player.getItemInHand();

            if (handler.isRegistered(item)) {
                handler.getRegistered(item).onEntityDamage(e);
            }
        } catch (UnsupportedOperationException ignored) { }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        ItemStack item = e.getItem();

        try {
            if (item != null) {
                if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    if (handler.isRegistered(item)) {
                        SkyblockPlayerAbilityTriggerEvent event = new SkyblockPlayerAbilityTriggerEvent(SkyblockPlayer.getPlayer(e.getPlayer()), new ItemBase(item), new HashMap<>());

                        Bukkit.getPluginManager().callEvent(event);

                        if (event.isCancelled()) return;

                        HashMap<String, Object> data = event.getData();

                        handler.getRegistered(item).onRightClick(e, data);
                    }
                } else if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    if (handler.isRegistered(item)) {
                        handler.getRegistered(item).onLeftClick(e);
                    }
                }
            }
        } catch (UnsupportedOperationException ignored) { }
    }

    @EventHandler
    public void onArmorEquip(PlayerArmorEquipEvent e){
        ItemStack item = e.getItemStack();

        if (item != null) {
            if (handler.isRegistered(item)) {
                try {
                    handler.getRegistered(item).onArmorEquip(e);
                } catch (UnsupportedOperationException ignored) { }
            }
        }

        ItemStack[] armor = e.getPlayer().getInventory().getArmorContents();

        for (ItemStack itemStack : armor) {
            if (!Util.notNull(itemStack)) return;
        }

        if (handler.isRegistered(armor)) {
            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(e.getPlayer());
            HashMap<String, Object> extraData = skyblockPlayer.getExtraData();

            if (extraData.get("fullSetBonusType") != null && extraData.get("fullSetBonusType").equals(handler.getRegistered(armor).getId()))
                return;

            extraData.put("fullSetBonus", true);
            extraData.put("fullSetBonusType", handler.getRegistered(armor).getId());

            skyblockPlayer.setExtraData(extraData);

            skyblockPlayer.setArmorSet(handler.getRegistered(armor));
            skyblockPlayer.getArmorSet().fullSetBonus(e.getPlayer());
        }
    }

    @EventHandler
    public void onArmorUnEquip(PlayerArmorUnequipEvent e){
        ItemStack item = e.getItemStack();

        if (item != null) {
            if (handler.isRegistered(item)) {
                try {
                    handler.getRegistered(item).onArmorUnEquip(e);
                } catch (UnsupportedOperationException ignored) { }
            }
        }

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(e.getPlayer());

        if (skyblockPlayer.getArmorSet() == null) return;

        HashMap<String, Object> extraData = skyblockPlayer.getExtraData();

        extraData.put("fullSetBonus", false);
        extraData.put("fullSetBonusType", null);

        skyblockPlayer.setExtraData(extraData);

        skyblockPlayer.getArmorSet().stopFullSetBonus(e.getPlayer());
        skyblockPlayer.setArmorSet(null);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            ItemStack item = player.getItemInHand();
            if (handler.isRegistered(item)) {
                try {
                    handler.getRegistered(item).onBowShoot(e);
                } catch (UnsupportedOperationException ignored) { }
            }
        }
    }
}
