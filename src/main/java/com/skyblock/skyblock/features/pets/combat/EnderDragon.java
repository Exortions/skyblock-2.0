package com.skyblock.skyblock.features.pets.combat;

import com.google.common.util.concurrent.AtomicDouble;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import com.skyblock.skyblock.features.entities.end.ObsidianDefender;
import com.skyblock.skyblock.features.entities.end.Watcher;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.features.pets.PetAbility;
import com.skyblock.skyblock.features.skills.Combat;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EnderDragon extends Pet {

    public EnderDragon() {
        super("Ender Dragon");
    }

    @Override
    public Skill getSkill() {
        return new Combat();
    }

    @Override
    public List<PetAbility> getAbilities(int level) {
        AtomicInteger endStrike = new AtomicInteger(2 * level);
        AtomicDouble oneWithTheDragonsDamage = new AtomicDouble(level * 0.5);
        AtomicDouble oneWithTheDragonsStrength = new AtomicDouble(level * 0.3);
        AtomicDouble superiorBonus = new AtomicDouble(0.1 * level);

        return Arrays.asList(
                new PetAbility() {
                    @Override
                    public String getName() {
                        return "End Strike";
                    }

                    @Override
                    public List<String> getDescription() {
                        return Util.buildLoreList("&7Deal &a" + endStrike.get() + "% &7more damage to\n&7end mobs.");
                    }

                    @Override
                    public void onDamage(SkyblockPlayerDamageEntityEvent e) {
                        EntityType type = e.getEntity().getVanilla().getType();
                        if (type.equals(EntityType.ENDERMAN) ||
                                type.equals(EntityType.ENDER_DRAGON) ||
                                type.equals(EntityType.ENDERMITE) ||
                                e.getEntity() instanceof ObsidianDefender ||
                                e.getEntity() instanceof Watcher) {
                            e.setDamage(e.getDamage() + (e.getDamage() * (endStrike.get() / 100f)));
                        }
                    }
                },
                new PetAbility() {
                    @Override
                    public String getName() {
                        return "One with the Dragons";
                    }

                    @Override
                    public List<String> getDescription() {
                        return Util.buildLoreList(
                                "&7Buffs the Aspect of the\n&7Dragons sword by &a" + oneWithTheDragonsDamage.get() + " &c" + SkyblockStat.STRENGTH.getIcon() + " Damage\n" +
                                        "&7and &a" + oneWithTheDragonsStrength.get() + " &c" + SkyblockStat.STRENGTH.getIcon() + " Strength."
                        );
                    }
                },
                new PetAbility() {
                    @Override
                    public String getName() {
                        return "Superior";
                    }

                    @Override
                    public List<String> getDescription() {
                        return Util.buildLoreList("&7Increases all stats by &a" + superiorBonus.get() + "%");
                    }

                    @Override
                    public void onEquip(SkyblockPlayer player) {
                        for (SkyblockStat stat : SkyblockStat.values()) {
                            player.addStatMultiplier(stat, superiorBonus.get() / 100);
                        }
                    }

                    @Override
                    public void onUnequip(SkyblockPlayer player) {
                        for (SkyblockStat stat : SkyblockStat.values()) {
                            player.subtractStatMultiplier(stat, superiorBonus.get() / 100);
                        }
                    }
                }
        );
    }

    @Override
    public String getSkull() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFkMDhjMDI4OWQ5ZWZlNTE5ZTg3ZjdiODE0Y2IyMzQ5ZjQ0NzViZDNjMzdkNDRmOWM0ZjBlNTA4ZTc3OTgxZSJ9fX0=";
    }

    @Override
    public double getPerStrength() {
        return 0.5;
    }

    @Override
    public double getPerCritDamage() {
        return 0.5;
    }

    @Override
    public double getPerCritChance() {
        return 0.1;
    }
}
