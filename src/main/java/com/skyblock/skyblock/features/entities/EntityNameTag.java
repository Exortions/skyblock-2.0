package com.skyblock.skyblock.features.entities;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSlime;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityNameTag {

    private final Entity entity;
    private final NPC stand;
    private final NPC slime;

    public EntityNameTag(SkyblockEntity e) {
        entity = e.getVanilla();

        slime = CitizensAPI.getNPCRegistry().createNPC(EntityType.SLIME, "");
        slime.spawn(entity.getLocation());

        Slime bukkit = (Slime) slime.getEntity();

        bukkit.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, true));
        bukkit.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 10, true, true));
        bukkit.setSize(1);

        ((CraftSlime) bukkit).getHandle().ai = false;

        bukkit.setCustomName("name_slime_" + entity.getEntityId());
        bukkit.setCustomNameVisible(false);

        stand = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARMOR_STAND, "");
        stand.spawn(entity.getLocation());

        ArmorStandTrait trait = stand.getOrAddTrait(ArmorStandTrait.class);
        trait.setMarker(true);
        trait.setGravity(false);
        trait.setVisible(false);

        stand.getEntity().setCustomNameVisible(true);

        slime.getEntity().setPassenger(stand.getEntity());
        entity.setPassenger(slime.getEntity());
    }

    public void tick(String name) {
        stand.getEntity().setCustomName(name);

        Slime sbukkit = (Slime) slime.getEntity();

        if (sbukkit.getSize() == 1) {
            int slimeSize = 1;

            if (entity.getType().equals(EntityType.ZOMBIE) && ((Zombie) entity).isVillager()) {
                slimeSize = 2;
            }

            if (entity.getType().equals(EntityType.ENDERMAN)) slimeSize = 2;

            sbukkit.setSize(slimeSize);
        }
    }

    public void death() {
        stand.destroy();
        slime.destroy();
    }
}
