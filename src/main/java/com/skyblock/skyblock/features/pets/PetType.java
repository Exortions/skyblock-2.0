package com.skyblock.skyblock.features.pets;

import com.skyblock.skyblock.features.pets.combat.BlackCat;
import com.skyblock.skyblock.features.pets.combat.EnderDragon;
import com.skyblock.skyblock.features.pets.fishing.BlueWhale;
import com.skyblock.skyblock.features.pets.combat.Jerry;
import com.skyblock.skyblock.features.pets.combat.Tiger;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

@Getter
@AllArgsConstructor
public enum PetType {

    JERRY(Jerry.class),
    TIGER(Tiger.class),
    BLUE_WHALE(BlueWhale.class),
    ENDER_DRAGON(EnderDragon.class),
    BLACK_CAT(BlackCat.class),

    ;

    final Class<? extends Pet> clazz;

    public Pet newInstance() {
        try {
            return clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
