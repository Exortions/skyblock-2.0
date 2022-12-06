package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.island.IslandManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinionListener implements Listener {

    @EventHandler
    public void onRightClickMinion(PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer() == null || event.getRightClicked() == null) return;

        if (!event.getRightClicked().hasMetadata("minion")) return;

        event.setCancelled(true);

        UUID minionId = UUID.fromString(event.getRightClicked().getMetadata("minion_id").get(0).asString());

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (player == null || !player.getBukkitPlayer().getWorld().getName().equals(IslandManager.ISLAND_PREFIX + player.getBukkitPlayer().getUniqueId().toString())) return;

        player.getBukkitPlayer().sendMessage(Skyblock.getPlugin().getMinionHandler().getMinions().toString());

        MinionBase minion = Skyblock.getPlugin().getMinionHandler().getMinion(minionId);

        if (minion == null) return;

        minion.showInventory(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getWhoClicked() == null || event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        if (!event.getClickedInventory().getName().contains("Minion")) return;

        event.setCancelled(true);
    }

}
