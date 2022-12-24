package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.potions.PotionEffect;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PotionListener implements Listener {

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        if (event.getItem() == null || event.getPlayer() == null || !event.getItem().getType().equals(Material.POTION)) return;

        NBTItem nbtItem = new NBTItem(event.getItem());
        boolean isPotion = nbtItem.getBoolean("potion.is_potion");

        if (!isPotion) return;

        String type = nbtItem.getString("potion.type");
        int amplifier = nbtItem.getInteger("potion.amplifier");
        double duration = (double) nbtItem.getInteger("potion.duration");

        if (SkyblockPlayer.getPlayer(event.getPlayer()).hasEffect(type)) {
            event.getPlayer().sendMessage(ChatColor.WHITE + "You already have " + PotionEffect.getMaxLevelsAndColors.get(type).getThird() + WordUtils.capitalize(type) + ChatColor.WHITE + " active!");

            Util.delay(() -> {
                for (org.bukkit.potion.PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
                    event.getPlayer().removePotionEffect(effect.getType());
                }
            }, 3);

            return;
        }

        event.setCancelled(true);

        event.getPlayer().performCommand("sb effect " + type + " " + amplifier + " " + duration);

        event.getPlayer().setItemInHand(null);

        Util.delay(() -> {
            for (org.bukkit.potion.PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
                event.getPlayer().removePotionEffect(effect.getType());
            }
        }, 3);
    }

}
