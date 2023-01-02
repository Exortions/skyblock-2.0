package com.skyblock.skyblock.features.items.weapons;

import com.google.common.util.concurrent.AtomicDouble;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class RogueSword extends SkyblockItem {

    public RogueSword() {
        super(plugin.getItemHandler().getItem("ROGUE_SWORD.json"), "rogue_sword");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        Player player = event.getPlayer();

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.checkMana(50)) {
            AtomicDouble speed = new AtomicDouble(20);
            if (skyblockPlayer.getStat(SkyblockStat.SPEED) > 400) return;
            if (skyblockPlayer.getStat(SkyblockStat.SPEED) + 20 > 400) {
                speed.set(speed.get() - ((skyblockPlayer.getStat(SkyblockStat.SPEED) + 20) - 400));
            }

            skyblockPlayer.addStat(SkyblockStat.SPEED, speed.get());
            skyblockPlayer.delay(() -> skyblockPlayer.subtractStat(SkyblockStat.SPEED, speed.get()), 20);

            Util.sendAbility(skyblockPlayer, "Speed Boost", 50);
        }
    }
}
