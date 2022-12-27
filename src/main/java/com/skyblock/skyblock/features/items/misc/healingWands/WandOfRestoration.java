package com.skyblock.skyblock.features.items.misc.healingWands;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.*;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.HashMap;

public class WandOfRestoration extends ListeningItem {
    private final int healingAmount = 120;
    private final int cooldown = 1;
    
    public WandOfRestoration(Skyblock plugin) {
        super(plugin.getItemHandler().getItem("WAND_OF_RESTORATION.json"), "wand_of_restoration");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

	int manaCost = new ItemBase(event.getItem()).getAbilityCost();
	if (!event.getPlayer().getItemInHand().equals(getItem()) || !player.checkMana(manaCost)) return;
        else if (player.isOnCooldown(getInternalName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "This ability is on cooldown.");
            return;
        }
        player.setCooldown(getInternalName(), cooldown);
        player.subtractMana(manaCost);
        long thisRun = System.currentTimeMillis();
        player.setExtraData("healingWandRun", thisRun);

        for (int i = 1; i < 8; ++i) {
		Util.delay(() -> {
		   if (((long) player.getExtraData("healingWandRun")) == thisRun) {
			if (player.getStat(SkyblockStat.MAX_HEALTH) - player.getStat(SkyblockStat.HEALTH) >= healingAmount)
			        player.setStat(SkyblockStat.HEALTH, player.getStat(SkyblockStat.HEALTH) + healingAmount);
			else if (player.getStat(SkyblockStat.MAX_HEALTH) - player.getStat(SkyblockStat.HEALTH) < healingAmount)
			        player.setStat(SkyblockStat.HEALTH, player.getStat(SkyblockStat.MAX_HEALTH));
		   }
		}, i * 20);
        }
    }
}
