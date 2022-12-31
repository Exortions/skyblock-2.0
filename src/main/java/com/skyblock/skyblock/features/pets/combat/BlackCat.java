package com.skyblock.skyblock.features.pets.combat;

import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.features.pets.PetAbility;
import com.skyblock.skyblock.features.skills.Combat;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Triple;
import com.skyblock.skyblock.utilities.Util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class BlackCat extends Pet {

    private final Function<Integer, Triple<Integer, Double, Double>> getAbilityValues = (level) -> {
        double omen = level * 0.15;
        double superNatural = level * 0.15;

        return Triple.of(level, omen, superNatural);
    };

    public BlackCat() {
        super("Black Cat");
    }

    @Override
    public Skill getSkill() {
        return new Combat();
    }

    @Override
    public List<PetAbility> getAbilities(int level) {
        Triple<Integer, Double, Double> abilityStats = this.getAbilityValues.apply(level);

        int hunter = abilityStats.getFirst();
        double omen = abilityStats.getSecond();
        double superNatural = abilityStats.getThird();

        return Arrays.asList(
                new PetAbility() {
                    @Override
                    public String getName() {
                        return "Hunter";
                    }

                    @Override
                    public List<String> getDescription() {
                        return Util.buildLoreList("&7Increases your speed and speed\n&7cap by +&a" + hunter + "&7.");
                    }
                },
                new PetAbility() {
                    @Override
                    public String getName() {
                        return "Omen";
                    }

                    @Override
                    public List<String> getDescription() {
                        return Util.buildLoreList("&7Grants &d" + omen + " " + SkyblockStat.PET_LUCK.getIcon() + " Pet Luck&7.");
                    }
                },
                new PetAbility() {
                    @Override
                    public String getName() {
                        return "Supernatural";
                    }

                    @Override
                    public List<String> getDescription() {
                        return Util.buildLoreList("&7Grants &b" + superNatural + " " + SkyblockStat.MAGIC_FIND.getIcon() + " Magic Find&7.");
                    }
                }
        );
    }

    @Override
    public String getSkull() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRiNDVjYmFhMTlmZTNkNjhjODU2Y2QzODQ2YzAzYjVmNTlkZTgxYTQ4MGVlYzkyMWFiNGZhM2NkODEzMTcifX19";
    }

    @Override
    public double getPerIntelligence() {
        return 1;
    }

    @Override
    public double getPerSpeed() {
        return 0.25;
    }

}
