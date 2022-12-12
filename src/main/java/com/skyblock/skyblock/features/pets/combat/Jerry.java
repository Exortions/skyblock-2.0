package com.skyblock.skyblock.features.pets.combat;

import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.features.pets.PetAbility;
import com.skyblock.skyblock.features.skills.Combat;
import com.skyblock.skyblock.features.skills.Skill;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class Jerry extends Pet {

    public Jerry() {
        super("Jerry");
    }

    @Override
    public Skill getSkill() {
        return new Combat();
    }

    @Override
    public List<PetAbility> getAbilities(int level) {
        return Arrays.asList(new PetAbility() {
            @Override
            public String getName() {
                return "Jerry";
            }

            @Override
            public List<String> getDescription() {
                return Arrays.asList("Gain " + ChatColor.GREEN + "50%" + ChatColor.GRAY + " chance to deal",
                        "your regular damage");
            }
        },
        new PetAbility() {
            @Override
            public String getName() {
                return "Jerry";
            }

            @Override
            public List<String> getDescription() {
                return Arrays.asList("Gain " + ChatColor.GREEN + "100%" + ChatColor.GRAY + " chance to",
                        "receive a normal amount of drops", "from mobs");
            }
        },
        new PetAbility() {
            @Override
            public String getName() {
                return "Jerry";
            }

            @Override
            public List<String> getDescription() {
                return Arrays.asList("Actually adds " + ChatColor.RED + level / 10 + " damage" + ChatColor.GRAY + " to",
                        "the Aspect of the Jerry");
            }

            @Override
            public Rarity getRequiredRarity() {
                return Rarity.LEGENDARY;
            }
        });
    }

    @Override
    public String getSkull() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI2ZWMxY2ExODViNDdhYWQzOWY5MzFkYjhiMGE4NTAwZGVkODZhMTI3YTIwNDg4NmVkNGIzNzgzYWQxNzc1YyJ9fX0=";
    }

    @Override
    public double getPerIntelligence() {
        return -1.0;
    }
}
