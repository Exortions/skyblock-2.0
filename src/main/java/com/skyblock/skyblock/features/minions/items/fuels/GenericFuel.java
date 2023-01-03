package com.skyblock.skyblock.features.minions.items.fuels;

import lombok.Getter;

import com.skyblock.skyblock.features.minions.MinionBase;
import com.skyblock.skyblock.features.minions.items.MinionFuel;

@Getter
public class GenericFuel extends MinionFuel {
    protected final float speed;

    public GenericFuel(String item, String internalName, boolean stackable, int duration, float speed) {
        super(plugin.getItemHandler().getItem(item), internalName, stackable, duration);
        this.speed = speed;
    }

    @Override
    public float onSleep(MinionBase minion, float duration) {
        return duration / speed;
    }
}
