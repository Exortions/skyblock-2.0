package com.skyblock.skyblock.features.items.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.dragon.DragonAltar;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SleepingEye extends SkyblockItem {
    public SleepingEye() {
        super(plugin.getItemHandler().getItem("SLEEPING_EYE.json"), "sleeping_eye");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getClickedBlock().getType().equals(Material.ENDER_PORTAL_FRAME)) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        DragonAltar altar = DragonAltar.getMainAltar();

        if (!altar.getFrames().contains(event.getClickedBlock().getLocation().clone().add(0.5, 0, 0.5))) return;
        if (!altar.isFilled(block)) return;
        if (!block.hasMetadata("placer")) return;
        if (!block.getMetadata("placer").get(0).asString().equals(player.getName())) return;

        BlockState state = block.getState();
        state.setRawData((byte) 2);
        state.update();


        player.setItemInHand(plugin.getItemHandler().getItem("SUMMONING_EYE.json"));
        player.sendMessage(ChatColor.DARK_PURPLE + "You recovered a Summoning Eye");

        block.removeMetadata("placer", Skyblock.getPlugin());
    }
}
