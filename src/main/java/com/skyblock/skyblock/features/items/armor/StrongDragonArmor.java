package com.skyblock.skyblock.features.items.armor;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerAbilityTriggerEvent;
import com.skyblock.skyblock.features.items.ArmorSet;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;

public class StrongDragonArmor extends ArmorSet {

    public StrongDragonArmor() {
        super(Skyblock.getPlugin().getItemHandler().getItem("STRONG_DRAGON_HELMET.json"),
                Skyblock.getPlugin().getItemHandler().getItem("STRONG_DRAGON_CHESTPLATE.json"),
                Skyblock.getPlugin().getItemHandler().getItem("STRONG_DRAGON_LEGGINGS.json"),
                Skyblock.getPlugin().getItemHandler().getItem("STRONG_DRAGON_BOOTS.json"),
                "strong_dragon_armor"
        );
    }

    @Override
    public void tick(Player player) {}

    @EventHandler
    public void onAspectOfTheEnd(SkyblockPlayerAbilityTriggerEvent event) {
        SkyblockPlayer player = event.getPlayer();
        ItemBase base = event.getItem();

        if (!player.hasFullSetBonus() || !player.getArmorSet().equals(this)) return;
        if (!base.getSkyblockId().equals("aspect_of_the_end")) return;
        if (event.isCancelled()) return;

        HashMap<String, Object> data = event.getData();
        data.put("range_increase", 2);
        data.put("time_increase", 3);
        data.put("strength_increase", 5);

        event.setData(data);
    }
}
