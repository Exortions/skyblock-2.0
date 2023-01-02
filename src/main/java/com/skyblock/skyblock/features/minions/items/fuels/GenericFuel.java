package com.skyblock.skyblock.features.minions.items.fuels;

import lombok.Getter;

import com.skyblock.skyblock.features.minions.MinionBase;
import com.skyblock.skyblock.features.minions.items.MinionFuel;

@Getter
public class GenericFuel extends MinionFuel {
    protected final float bonus;

    public GenericFuel(String item, String internalName, boolean stackable, int duration, float bonus) {
        super(plugin.getItemHandler().getItem(item), internalName, stackable, duration);
        this.bonus = bonus;
    }

    @Override
    public void onTick(MinionBase minion) {
        minion.timeBetweenActions -= minion.timeBetweenActions * bonus;
    }
}
