package com.skyblock.skyblock.features.items.bows;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.SkyblockItem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EndstoneBow extends SkyblockItem {

    public EndstoneBow() {
        super(plugin.getItemHandler().getItem("END_STONE_BOW.json"), "end_stone_bow");
    }

    @Override
    public double getModifiedDamage(SkyblockPlayer skyblockPlayer, EntityDamageByEntityEvent e, double damage) {
        Player player = skyblockPlayer.getBukkitPlayer();
        if (isThisItem(player.getItemInHand()) && skyblockPlayer.hasExtraData("endstoneBowBuff")) {
            int buff = (int) skyblockPlayer.getExtraData("endstoneBowBuff");
            skyblockPlayer.setExtraData("endstoneBowBuff", null);
            player.sendMessage(ChatColor.RED + "Your Extreme Focus has worn off.");
            return damage + buff;
        }
        return damage;
    }

    @Override
    public void onLeftClick(PlayerInteractEvent event) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(event.getPlayer());
        int playerMana = skyblockPlayer.getStat(SkyblockStat.MANA);
        if (playerMana > 0) {
            if (!skyblockPlayer.hasExtraData("endstoneBowBuff")) {
                skyblockPlayer.setExtraData("endstoneBowBuff", playerMana);
                skyblockPlayer.setStat(SkyblockStat.MANA, 0);
            }
            else {
                event.getPlayer().sendMessage(ChatColor.RED + "Your last Extreme Focus buff hasn't worn off yet!");
            }
        }
    }
}
