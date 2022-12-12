package com.skyblock.skyblock.features.pets.combat;

import com.google.common.util.concurrent.AtomicDouble;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.features.pets.PetAbility;
import com.skyblock.skyblock.features.skills.Combat;
import com.skyblock.skyblock.features.skills.Skill;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tiger extends Pet {

    public Tiger() {
        super("Tiger");
    }

    @Override
    public Skill getSkill() {
        return new Combat();
    }

    @Override
    public List<PetAbility> getAbilities(int level) {
        AtomicDouble ferocityMult = new AtomicDouble(0.1);
        AtomicDouble hemorrhageMult = new AtomicDouble(0.3);

        if (getRarity().getLevel() >= 2 && getRarity().getLevel() < 4) ferocityMult.set(0.2);;

        if (getRarity().getLevel() >= 4) {
            ferocityMult.set(0.3);
            hemorrhageMult.set(0.5);
        }

        return Arrays.asList(
                new PetAbility() {
                     @Override
                     public String getName() {
                         return "Merciless Swipe";
                     }

                     @Override
                     public List<String> getDescription() {
                         return Collections.singletonList("Gain " + ChatColor.RED + Math.round(level * ferocityMult.get()) + "% Ferocity");
                     }

                    @Override
                    public void onEquip(SkyblockPlayer player) {
                        player.addStat(SkyblockStat.FEROCITY, (int) Math.round(level * ferocityMult.get()));
                    }

                    @Override
                    public void onUnequip(SkyblockPlayer player) {
                        player.subtractStat(SkyblockStat.FEROCITY, (int) Math.round(level * ferocityMult.get()));
                    }
                },
                new PetAbility() {
                    @Override
                    public String getName() {
                        return "Hemorrhage";
                    }

                    @Override
                    public List<String> getDescription() {
                        return Arrays.asList("Melee attacks reduce healing", "by " + ChatColor.GOLD + (level * hemorrhageMult.get())
                                + "%" + ChatColor.GRAY + " for " + ChatColor.GREEN + 10 + "s");
                    }

                    @Override
                    public Rarity getRequiredRarity() {
                        return Rarity.RARE;
                    }
                },
                new PetAbility() {
                    @Override
                    public String getName() {
                        return "Apex Predator";
                    }

                    @Override
                    public List<String> getDescription() {
                        return Arrays.asList("Deal " + ChatColor.RED + "+" + level + "% " + ChatColor.GRAY + "damage against", "targets with no other mobs", "within 15 blocks");
                    }

                    @Override
                    public Rarity getRequiredRarity() {
                        return Rarity.LEGENDARY;
                    }
                });
    }

    @Override
    public String getSkull() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmM0MjYzODc0NDkyMmI1ZmNmNjJjZDliZjI3ZWVhYjkxYjJlNzJkNmM3MGU4NmNjNWFhMzg4Mzk5M2U5ZDg0In19fQ==";
    }

    @Override
    public double getPerCritDamage() {
        return 0.005;
    }

    @Override
    public double getPerCritChance() {
        return 0.0005;
    }

    @Override
    public double getPerStrength() {
        return 0.1;
    }

    @Override
    public double getPerFerocity() {
        return 0.05;
    }

    @Override
    public double getBaseStrength() {
        return 5;
    }
}
