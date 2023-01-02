package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.items.BlockHelmetSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpongeArmor extends BlockHelmetSet {

    private static final List<Player> applied = new ArrayList<>();
    public SpongeArmor() {
        super("SPONGE_HELMET", "SPONGE_CHESTPLATE", "SPONGE_LEGGINGS", "SPONGE_BOOTS", "sponge_armor");
    }

    @Override
    public void tick(Player player) {
        if (!player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER) && applied.contains(player)) {
            SkyblockPlayer.getPlayer(player).subtractStatMultiplier(SkyblockStat.DEFENSE, 1);
            applied.remove(player);
            return;
        }
        if (applied.contains(player)) return;

        if (player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)) {
            SkyblockPlayer.getPlayer(player).addStatMultiplier(SkyblockStat.DEFENSE, 1);
            applied.add(player);
        }
    }
}
