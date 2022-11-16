package com.skyblock.skyblock.utilities.item;

import com.skyblock.skyblock.enums.ReforgeType;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Item {

    private final List<String> description;
    private final Material material;
    private final String name;
    private final String rarity;
    private final int amount;

    private final ReforgeType reforgeType;
    private final boolean reforgeable;

    private final boolean enchantGlint;

    private final List<String> abilityDescription;
    private final String abilityCooldown;
    private final String abilityName;
    private final String abilityType;
    private final boolean hasAbility;
    private final int abilityCost;

    private final int intelligence;
    private final int attackSpeed;
    private final int critChance;
    private final int critDamage;
    private final int strength;
    private final int defense;
    private final int damage;
    private final int speed;

    private final ItemStack stack;

    public Item(List<String> description, Material material, String name, String rarity, int amount, ReforgeType reforgeType, boolean reforgeable, boolean enchantGlint, List<String> abilityDescription, String abilityCooldown, String abilityName, String abilityType, boolean hasAbility, int abilityCost, int intelligence, int attackSpeed, int critChance, int critDamage, int strength, int defense, int damage, int speed) {
        this.description = description;
        this.material = material;
        this.name = name;
        this.rarity = rarity;
        this.amount = amount;

        this.reforgeType = reforgeType;
        this.reforgeable = reforgeable;

        this.enchantGlint = enchantGlint;

        this.abilityDescription = abilityDescription;
        this.abilityCooldown = abilityCooldown;
        this.abilityName = abilityName;
        this.abilityType = abilityType;
        this.abilityCost = abilityCost;
        this.hasAbility = hasAbility;

        this.intelligence = intelligence;
        this.attackSpeed = attackSpeed;
        this.critChance = critChance;
        this.critDamage = critDamage;
        this.strength = strength;
        this.defense = defense;
        this.damage = damage;
        this.speed = speed;

        this.stack = new ItemStack(this.material, this.amount);

        ItemMeta meta = this.stack.getItemMeta();

        List<String> lore = new ArrayList<>();

        /*
          Stats
         */
        if (damage != 0) lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + damage);
        if (strength != 0) lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+" + strength);
        if (critChance != 0) lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+" + critChance + "%");
        if (critDamage != 0) lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+" + critDamage + "%");
        if (attackSpeed != 0) lore.add(ChatColor.GRAY + "Attack Speed: " + ChatColor.RED + "+" + attackSpeed + "%");
        if (speed != 0 && intelligence != 0 && defense != 0){
            lore.add("");
            lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+" + intelligence);
            lore.add(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + speed);
            lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+" + defense);
        } else if (intelligence != 0){
            lore.add("");
            lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+" + intelligence);
        } else if (speed != 0){
            lore.add("");
            lore.add(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + speed);
        } else if (defense != 0){
            lore.add("");
            lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+" + defense);
        }

        lore.add("");

        /*
          Description
         */
        if (description != null) lore.addAll(description);

        /*
          Ability
         */
        if (hasAbility) {
            lore.add(ChatColor.GOLD + "Item Ability: " + abilityName + " " + ChatColor.YELLOW + ChatColor.BOLD  + abilityType);
            lore.addAll(abilityDescription);

            if(abilityCost != 0)  lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.AQUA + abilityCost);
            if(!Objects.equals(abilityCooldown, "")) lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + abilityCooldown);

            lore.add("");
        }

        /*
          Reforge
         */

        if (reforgeable) lore.add(ChatColor.DARK_GRAY + "This item can be reforged!");

        /*
          Rarity
         */

        if (rarity.contains("LEGENDARY") || rarity.contains("legendary")){
            lore.add("" + ChatColor.GOLD + ChatColor.BOLD + rarity.toUpperCase());
        } else if (rarity.contains("EPIC") || rarity.contains("epic")){
            lore.add("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + rarity.toUpperCase());
        } else if (rarity.contains("RARE") || rarity.contains("rare")){
            lore.add("" + ChatColor.BLUE + ChatColor.BOLD + rarity.toUpperCase());
        } else if (rarity.contains("UNCOMMON") || rarity.contains("uncommon")) {
            lore.add("" + ChatColor.GREEN + ChatColor.BOLD + rarity.toUpperCase());
        } else if (rarity.contains("COMMON") || rarity.contains("common")) {
            lore.add("" + ChatColor.WHITE + ChatColor.BOLD + rarity.toUpperCase());
        }

        if (!(reforgeType == ReforgeType.NO_REFORGE)) meta.setDisplayName(reforgeType + " " + name);
        else meta.setDisplayName(name);

        meta.setLore(lore);

        if (enchantGlint) meta.addEnchant(Enchantment.DURABILITY, 1, true);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        this.stack.setItemMeta(meta);
    }

}
