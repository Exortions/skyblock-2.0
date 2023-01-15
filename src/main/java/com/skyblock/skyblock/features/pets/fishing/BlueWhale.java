package com.skyblock.skyblock.features.pets.fishing;

import com.google.common.util.concurrent.AtomicDouble;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.features.pets.PetAbility;
import com.skyblock.skyblock.features.skills.Fishing;
import com.skyblock.skyblock.features.skills.Skill;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BlueWhale extends Pet {
    public BlueWhale() {
        super("Blue Whale");
    }

    @Override
    public Skill getSkill() {
        return new Fishing();
    }

    @Override
    public List<PetAbility> getAbilities(int level) {
        AtomicDouble ingestMult = new AtomicDouble(getRarity().getLevel() * 0.5);
        AtomicInteger bulkHp = new AtomicInteger(30 - (5 * (getRarity().getLevel() - 3)));

        return Arrays.asList(
                new PetAbility() {
                     @Override
                     public String getName() {
                         return "Ingest";
                     }

                     @Override
                     public List<String> getDescription() {
                         return Collections.singletonList("All potions heal " + ChatColor.RED + "+" + (ingestMult.get() * level) + "❤");
                     }
                 },
                new PetAbility() {
                    @Override
                    public String getName() {
                        return "Bulk";
                    }

                    @Override
                    public List<String> getDescription() {
                        return Arrays.asList("Gain " + ChatColor.GREEN + (0.03 * level) + "❈ Defense " + ChatColor.GRAY + "per",
                                ChatColor.RED + "" + bulkHp.get() + " Max ❤ Health");
                    }

                    @Override
                    public Rarity getRequiredRarity() {
                        return Rarity.RARE;
                    }

                    @Override
                    public void onEquip(SkyblockPlayer player) {
                        double def = (float) (player.getStat(SkyblockStat.MAX_HEALTH) / bulkHp.get()) * (0.03 * level);
                        player.setStat(SkyblockStat.DEFENSE, player.getStat (SkyblockStat.DEFENSE) + (int) def, false);
                    }

                    @Override
                    public void onUnequip(SkyblockPlayer player) {
                        double def = (float) (player.getStat(SkyblockStat.MAX_HEALTH) / bulkHp.get()) * (0.03 * level);
                        player.subtractStat(SkyblockStat.DEFENSE, (int) def);
                    }
                },
                new PetAbility() {
                    @Override
                    public String getName() {
                        return "Archimedes";
                    }

                    @Override
                    public List<String> getDescription() {
                        return Collections.singletonList("Gain " + ChatColor.RED + "+" + (0.2 * level) + "% Max ❤ Health");
                    }

                    @Override
                    public Rarity getRequiredRarity() {
                        return Rarity.LEGENDARY;
                    }

                    @Override
                    public void onEquip(SkyblockPlayer player) {
                        player.addStatMultiplier(SkyblockStat.MAX_HEALTH, ((0.2 * level) / 100f));
                    }

                    @Override
                    public void onUnequip(SkyblockPlayer player) {
                        player.subtractStatMultiplier(SkyblockStat.MAX_HEALTH, ((0.2 * level) / 100f));
                    }
                });
    }

    @Override
    public String getSkull() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFiNzc5YmJjY2M4NDlmODgyNzNkODQ0ZThjYTJmM2E2N2ExNjk5Y2IyMTZjMGExMWI0NDMyNmNlMmNjMjAifX19";
    }

    @Override
    public double getPerHealth() {
        return 2;
    }
}
