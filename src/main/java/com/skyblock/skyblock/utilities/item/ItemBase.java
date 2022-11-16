package com.skyblock.skyblock.utilities.item;

import com.skyblock.skyblock.enums.ReforgeType;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.plugin.NBTAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ItemBase {

    private List<String> description;
    private Material material;
    private String name;
    private String rarity;
    private int amount;

    private ReforgeType reforgeType;
    private boolean reforgeable;

    private boolean enchantGlint;

    private List<String> abilityDescription;
    private String abilityCooldown;
    private String abilityName;
    private String abilityType;
    private boolean hasAbility;
    private int abilityCost;

    private int intelligence;
    private int attackSpeed;
    private int critChance;
    private int critDamage;
    private int strength;
    private int defense;
    private int damage;
    private int speed;

    private ItemStack stack;

    public ItemBase(Material material, String name, ReforgeType reforgeType, int amount, List<String> description, boolean enchantGlint, boolean hasAbility, String abilityName, List<String> abilityDescription, String abilityType, int abilityCost, String abilityCooldown, String rarity, int damage, int strength, int critChance, int critDamage, int attackSpeed, int intelligence, int speed, int defense, boolean reforgeable) {
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

        this.createStack();
    }

    public ItemStack createStack() {
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

        NBTItem nbt = new NBTItem(this.stack);

        nbt.setBoolean("skyblockItem", true);
        nbt.setString("name", name);
        nbt.setString("rarity", rarity);
        nbt.setString("reforgeType", reforgeType.toString());
        nbt.setBoolean("reforgeable", reforgeable);
        nbt.setBoolean("hasAbility", hasAbility);
        nbt.setString("abilityName", abilityName);
        nbt.setString("abilityType", abilityType);
        nbt.setString("abilityCooldown", abilityCooldown);
        nbt.setInteger("abilityCost", abilityCost);
        nbt.setString("abilityDescription", abilityDescription.toString());
        nbt.setInteger("damage", damage);
        nbt.setInteger("strength", strength);
        nbt.setInteger("critChance", critChance);
        nbt.setInteger("critDamage", critDamage);
        nbt.setInteger("attackSpeed", attackSpeed);
        nbt.setInteger("intelligence", intelligence);
        nbt.setInteger("speed", speed);
        nbt.setInteger("defense", defense);

        this.stack = nbt.getItem();

        return this.stack;
    }

    public void give(Player player) {
        player.getInventory().addItem(createStack());
    }

}
