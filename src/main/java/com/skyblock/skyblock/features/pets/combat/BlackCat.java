package com.skyblock.skyblock.features.pets.combat;

import com.skyblock.skyblock.SkyblockPlayer;
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

                    @Override
                    public void onEquip(SkyblockPlayer player) {
                        player.addStat(SkyblockStat.SPEED, hunter);

                        player.setExtraData("black_cat.active", true);
                        player.setExtraData("black_cat.hunter", hunter);
                    }

                    @Override
                    public void onUnequip(SkyblockPlayer player) {
                        player.subtractStat(SkyblockStat.SPEED, hunter);

                        player.setExtraData("black_cat.active", false);
                        player.setExtraData("black_cat.hunter", 0);
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

                    @Override
                    public void onEquip(SkyblockPlayer player) {
                        double current = player.getStat(SkyblockStat.PET_LUCK);

                        player.setStat(SkyblockStat.PET_LUCK, current + (current * omen / 100));

                        player.setExtraData("black_cat.omen_bonus", omen);
                    }

                    @Override
                    public void onUnequip(SkyblockPlayer player) {
                        double current = player.getStat(SkyblockStat.PET_LUCK);

                        player.setStat(SkyblockStat.PET_LUCK, current - (double) player.getExtraData("black_cat.omen_bonus"));

                        player.setExtraData("black_cat.omen_bonus", 0);
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

                    @Override
                    public void onEquip(SkyblockPlayer player) {
                        double current = player.getStat(SkyblockStat.MAGIC_FIND);

                        player.setStat(SkyblockStat.MAGIC_FIND, current + (current * superNatural / 100));

                        player.setExtraData("black_cat.super_natural_bonus", superNatural);
                    }

                    @Override
                    public void onUnequip(SkyblockPlayer player) {
                        double current = player.getStat(SkyblockStat.MAGIC_FIND);

                        player.setStat(SkyblockStat.MAGIC_FIND, current - (double) player.getExtraData("black_cat.super_natural_bonus"));

                        player.setExtraData("black_cat.super_natural_bonus", 0);
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
    public double getBaseIntelligence() {
        return 1;
    }

    @Override
    public double getPerSpeed() {
        return 0.25;
    }

    @Override
    public double getBaseSpeed() {
        return 0.25;
    }
}
