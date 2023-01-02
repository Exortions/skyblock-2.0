package com.skyblock.skyblock.features.items.weapons;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.SkyblockItem;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class EndStoneSword extends SkyblockItem {


    public EndStoneSword() {
        super(plugin.getItemHandler().getItem("END_STONE_SWORD.json"), "end_stone_sword");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        Player player = event.getPlayer();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        final int MAX_DAMAGE_RESISTANCE = 100;
        final int MAX_DAMAGE = 100;

        double mana = skyblockPlayer.getStat(SkyblockStat.MANA);

        double damageResistanceAdded = 0;
        double addedDamage = 0;

        // 5 mana = 1 damage resistance
        // 5 mana = 1 damage
        // max 100 damage resistance
        // max 100 damage

        if (mana >= 5) {
            damageResistanceAdded = mana / 5;
            addedDamage = mana / 5;
        }

        if (damageResistanceAdded > MAX_DAMAGE_RESISTANCE) damageResistanceAdded = MAX_DAMAGE_RESISTANCE;
        if (addedDamage > MAX_DAMAGE) addedDamage = MAX_DAMAGE;

        skyblockPlayer.setStat(SkyblockStat.MANA, 0);

        double addedDefense = (100 * damageResistanceAdded) / (1 - damageResistanceAdded);

        skyblockPlayer.setStat(SkyblockStat.DEFENSE, skyblockPlayer.getStat(SkyblockStat.DEFENSE) + addedDefense);

        new BukkitRunnable() {
            @Override
            public void run() {
                skyblockPlayer.setStat(SkyblockStat.DEFENSE, skyblockPlayer.getStat(SkyblockStat.DEFENSE) - addedDefense);

                if ((boolean) skyblockPlayer.getExtraData().get("isEndStoneSwordActive")) {
                    HashMap<String, Object> extraData = skyblockPlayer.getExtraData();

                    extraData.put("isEndStoneSwordActive", false);
                    extraData.put("endStoneSwordDamage", 0);

                    skyblockPlayer.setExtraData(extraData);
                }
            }
        }.runTaskLater(this.plugin, 20 * 5);

        HashMap<String, Object> extraData = skyblockPlayer.getExtraData();

        extraData.put("isEndStoneSwordActive", true);
        extraData.put("endStoneSwordDamage", addedDamage);

        skyblockPlayer.setExtraData(extraData);

        skyblockPlayer.setStat(SkyblockStat.DAMAGE, skyblockPlayer.getStat(SkyblockStat.DAMAGE) + ((skyblockPlayer.getStat(SkyblockStat.DAMAGE) * addedDamage) / 100));
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        if (event.getEntity().hasMetadata("NPC")) return;

        Player player = (Player) event.getDamager();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (!(boolean) skyblockPlayer.getExtraData().get("isEndStoneSwordActive")) return;

        HashMap<String, Object> extraData = skyblockPlayer.getExtraData();

        int damageAdded = (int) extraData.get("endStoneSwordDamage");

        extraData.put("isEndStoneSwordActive", false);
        extraData.put("endStoneSwordDamage", 0);

        skyblockPlayer.setStat(SkyblockStat.DAMAGE, skyblockPlayer.getStat(SkyblockStat.DAMAGE) - damageAdded);
        skyblockPlayer.setExtraData(extraData);
    }
}
