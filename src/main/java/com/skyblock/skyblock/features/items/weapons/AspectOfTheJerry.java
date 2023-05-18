package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class AspectOfTheJerry extends SkyblockItem {

    public AspectOfTheJerry() {
        super(plugin.getItemHandler().getItem("ASPECT_OF_THE_JERRY.json"),
                "aspect_of_the_jerry");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        Player player = event.getPlayer();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.isOnCooldown(getInternalName())) {
            skyblockPlayer.sendOnCooldown(getInternalName());

            return;
        }

        player.playSound(player.getLocation(), Sound.VILLAGER_HAGGLE, 10, 1);
        Util.sendAbility(skyblockPlayer, "Parley", 0);

        skyblockPlayer.setCooldown(getInternalName(), 5);
    }
}
