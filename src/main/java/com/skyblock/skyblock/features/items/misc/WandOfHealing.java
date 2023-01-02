package com.skyblock.skyblock.features.items.misc;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class WandOfHealing extends ListeningItem {
    private final int healingAmount;

    public WandOfHealing(String type, int heals) {
        super(plugin.getItemHandler().getItem(type + ".json"), type.toLowerCase());
        healingAmount = heals;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        ItemBase base = new ItemBase(event.getItem());
        int manaCost = base.getAbilityCost();
        if (!event.getPlayer().getItemInHand().equals(getItem()) || !player.checkMana(manaCost)) return;
        else if (player.isOnCooldown(getInternalName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "This ability is on cooldown.");
            return;
        }

        int cooldown = 1;
        player.setCooldown(getInternalName(), cooldown);
        long thisRun = System.currentTimeMillis();
        player.setExtraData("healingWandRun", thisRun);

        player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.LAVA_POP, 10, 0);

        Util.sendAbility(player, base.getAbilityName(), base.getAbilityCost());

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
