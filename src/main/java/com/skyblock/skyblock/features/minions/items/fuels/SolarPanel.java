package com.skyblock.skyblock.features.minions.items.fuels;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import de.tr7zw.nbtapi.NBTItem;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.items.DynamicLore;
import com.skyblock.skyblock.features.minions.MinionBase;
import com.skyblock.skyblock.features.minions.items.MinionFuel;
import com.skyblock.skyblock.features.minions.items.MinionItemType;

public class SolarPanel extends MinionFuel {

    public SolarPanel() {
        super(plugin.getItemHandler().getItem("SOLAR_PANEL.json"), "solar_panel", false, -1);
    }

    @Override
    public float onSleep(MinionBase minion, float duration) {
        long worldTime = minion.getMinion().getWorld().getTime();
        double properTime = worldTime / 1000 + 6;
        if (properTime > 6 && properTime < 18) {
            return duration / 1.25f;
        }

        return duration;
    }
}
