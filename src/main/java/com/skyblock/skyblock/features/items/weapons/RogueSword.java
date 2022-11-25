package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class RogueSword extends SkyblockItem {

    public RogueSword(Skyblock plugin) {
        super(plugin.getItemHandler().getItem("ROGUE_SWORD.json"), "rogue_sword");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.checkMana(50)) {
            int speed = 20;
            if (skyblockPlayer.getStat(SkyblockStat.SPEED) > 400) return;
            if (skyblockPlayer.getStat(SkyblockStat.SPEED) + 20 > 400) {
                speed = speed - ((skyblockPlayer.getStat(SkyblockStat.SPEED) + 20) - 400);
            }

            skyblockPlayer.addStat(SkyblockStat.SPEED, speed);
            int finalSpeed = speed;
            skyblockPlayer.delay(() -> skyblockPlayer.subtractStat(SkyblockStat.SPEED, finalSpeed), 20);

            Util.sendAbility(skyblockPlayer, "Speed Boost", 50);
        }
    }
}
