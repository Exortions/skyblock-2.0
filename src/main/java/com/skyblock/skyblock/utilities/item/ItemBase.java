package com.skyblock.skyblock.utilities.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.ReforgeType;
import com.skyblock.skyblock.features.enchantment.ItemEnchantment;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantment;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ItemBase {

    private List<String> description;
    private Material material;
    private String name;
    private String rarity;
    private String skyblockId;
    private int amount;

    private ReforgeType reforgeType;
    private boolean reforgeable;

    private List<ItemEnchantment> enchantments;
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

    public ItemBase(ItemStack item) {
        NBTItem nbt = new NBTItem(item);

        if (!nbt.getBoolean("skyblockItem")) throw new IllegalArgumentException("Item is not a skyblock item.");

        this.material = item.getType();
        this.amount = item.getAmount();
        this.name = nbt.getString("name");
        this.rarity = nbt.getString("rarity");
        this.reforgeType = ReforgeType.getReforge(nbt.getString("reforgeType"));
        this.reforgeable = nbt.getBoolean("reforgeable");
        this.enchantments = new ArrayList<>();
        String enchantmentsStr = nbt.getString("enchantments");
        String[] enchantmentsList = enchantmentsStr.substring(1, enchantmentsStr.length() - 1).split(", ");
        try {
            for (String enchantment : enchantmentsList)
                this.enchantments.add(new ItemEnchantment(Skyblock.getPlugin(Skyblock.class).getEnchantmentHandler().getEnchantment(enchantment.split(";")[1]), Integer.parseInt(enchantment.split(";")[0])));
        } catch (Exception ex) {
            this.enchantments = new ArrayList<>();
        }
        this.enchantGlint = nbt.getBoolean("enchantGlint");
        String abilityDescriptionStr = nbt.getString("abilityDescription");
        this.abilityDescription = Arrays.asList(abilityDescriptionStr.substring(1, abilityDescriptionStr.length() - 1).split(", "));
        this.abilityCooldown = nbt.getString("abilityCooldown");
        this.abilityName = nbt.getString("abilityName");
        this.abilityType = nbt.getString("abilityType");
        this.hasAbility = nbt.getBoolean("hasAbility");
        this.abilityCost = nbt.getInteger("abilityCost");
        this.intelligence = nbt.getInteger("intelligence");
        this.attackSpeed = nbt.getInteger("attackSpeed");
        this.critChance = nbt.getInteger("critChance");
        this.critDamage = nbt.getInteger("critDamage");
        this.strength = nbt.getInteger("strength");
        this.defense = nbt.getInteger("defense");
        this.damage = nbt.getInteger("damage");
        this.speed = nbt.getInteger("speed");
        this.skyblockId = nbt.getString("skyblockId");

        String descriptionStr = nbt.getString("description");
        String[] descriptionArr = descriptionStr.substring(1, descriptionStr.length() - 1).split(", ");
        String[] descriptionArrClone = Arrays.copyOf(descriptionArr, descriptionArr.length + 1);
        descriptionArrClone[descriptionArrClone.length - 1] = "";
        this.description = Arrays.asList(descriptionArrClone);

        this.stack = item;
    }

    public ItemBase(Material material, String name, ReforgeType reforgeType, int amount, List<String> description, List<ItemEnchantment> enchantments, boolean enchantGlint, boolean hasAbility, String abilityName, List<String> abilityDescription, String abilityType, int abilityCost, String abilityCooldown, String rarity, int damage, int strength, int critChance, int critDamage, int attackSpeed, int intelligence, int speed, int defense, boolean reforgeable) {
        this.description = description;
        this.material = material;
        this.name = name;
        this.rarity = rarity;
        this.amount = amount;

        this.reforgeType = reforgeType;
        this.reforgeable = reforgeable;

        this.enchantments = enchantments;
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

        if (this.reforgeType == null) this.reforgeType = ReforgeType.NONE;

        /*
          Stats
         */
        if (damage != 0) lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + damage);
        if (strength != 0 || reforgeType.getStrength() > 0) lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+" + (strength + reforgeType.getStrength()) + (reforgeType != ReforgeType.NONE && reforgeType.getStrength() > 0 ? " " + ChatColor.BLUE + "(+" + reforgeType.getStrength() + ")" : ""));
        if (critChance != 0 || reforgeType.getCritChance() > 0) lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+" + (critChance + reforgeType.getCritChance()) + "%" + (reforgeType != ReforgeType.NONE && reforgeType.getCritChance() > 0 ? " " + ChatColor.BLUE + "(+" + reforgeType.getCritChance() + "%)" : ""));
        if (critDamage != 0 || reforgeType.getCritDamage() > 0) lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+" + (critDamage + reforgeType.getCritDamage()) + "%" + (reforgeType != ReforgeType.NONE && reforgeType.getCritDamage() > 0 ? " " + ChatColor.BLUE + "(+" + reforgeType.getCritDamage() + "%)" : ""));
        if (attackSpeed != 0 || reforgeType.getAttackSpeed() > 0) lore.add(ChatColor.GRAY + "Attack Speed: " + ChatColor.RED + "+" + (attackSpeed + reforgeType.getAttackSpeed()) + "%" + (reforgeType != ReforgeType.NONE && reforgeType.getAttackSpeed() > 0 ? " " + ChatColor.BLUE + "(+" + reforgeType.getAttackSpeed() + "%)" : ""));
        if ((speed != 0 || reforgeType.getSpeed() > 0) && (intelligence != 0 || reforgeType.getMana() > 0) && (defense != 0 || reforgeType.getDefense() > 0)) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+" + (intelligence + reforgeType.getMana()) + (reforgeType != ReforgeType.NONE && reforgeType.getMana() > 0 ? " " + ChatColor.BLUE + "(+" + reforgeType.getMana() + ")" : ""));
            lore.add(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + (speed + reforgeType.getSpeed()) + (reforgeType != ReforgeType.NONE && reforgeType.getSpeed() > 0 ? " " + ChatColor.BLUE + "(+" + reforgeType.getSpeed() + ")" : ""));
            lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+" + (defense + reforgeType.getDefense()) + (reforgeType != ReforgeType.NONE && reforgeType.getDefense() > 0 ? " " + ChatColor.BLUE + "(+" + reforgeType.getDefense() + ")" : ""));
        } else if (intelligence != 0 || reforgeType.getMana() > 0){
            lore.add("");
            lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+" + (intelligence + reforgeType.getMana()) + (reforgeType != ReforgeType.NONE && reforgeType.getMana() > 0 ? " " + ChatColor.BLUE + "(+" + reforgeType.getMana() + ")" : ""));
        } else if (speed != 0 || reforgeType.getSpeed() > 0){
            lore.add("");
            lore.add(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + (speed + reforgeType.getSpeed()) + (reforgeType != ReforgeType.NONE && reforgeType.getSpeed() > 0 ? " " + ChatColor.BLUE + "(+" + reforgeType.getSpeed() + ")" : ""));
        } else if (defense != 0 || reforgeType.getDefense() > 0){
            lore.add("");
            lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+" + (defense + reforgeType.getDefense()) + (reforgeType != ReforgeType.NONE && reforgeType.getDefense() > 0 ? " " + ChatColor.BLUE + "(+" + reforgeType.getDefense() + ")" : ""));
        }

        lore.add("");

        if (!(this.enchantments.size() < 1)) {
            if (this.enchantments.size() <= 3) {
                for (ItemEnchantment enchantment : this.enchantments) {
                    lore.add(ChatColor.BLUE + enchantment.getBaseEnchantment().getName() + " " + Util.toRoman(enchantment.getLevel()));
                    for (String s : Util.buildLore(enchantment.getBaseEnchantment().getDescription(enchantment.getLevel()))) {
                        lore.add(ChatColor.GRAY + s);
                    }
                }
            } else {
                List<String> enchantmentLore = new ArrayList<>();

                int maxCharsPerLine = 45;

                StringBuilder currentLine = new StringBuilder();

                for (ItemEnchantment enchantment : this.enchantments) {
                    if (currentLine.length() + enchantment.getBaseEnchantment().getName().length() + 1 > maxCharsPerLine) {
                        enchantmentLore.add(currentLine.toString());
                        currentLine = new StringBuilder();
                    }

                    currentLine.append(ChatColor.BLUE).append(enchantment.getBaseEnchantment().getName()).append(" ").append(Util.toRoman(enchantment.getLevel())).append(ChatColor.BLUE).append(", ");
                }

                if (currentLine.length() > 0) {
                    currentLine.delete(currentLine.length() - 2, currentLine.length());
                    enchantmentLore.add(currentLine.toString());
                }

                lore.addAll(enchantmentLore);
            }
        }

        lore.add("");

        /*
          Description
         */
        if (description != null && description.size() != 0 && !description.get(0).equals("laceholder descriptio")) lore.addAll(description);

        /*
          Ability
         */
        if (hasAbility) {
            lore.add(ChatColor.GOLD + "Item Ability: " + abilityName + "" + ChatColor.YELLOW + ChatColor.BOLD  + abilityType);
            lore.addAll(abilityDescription);

            if(abilityCost != 0)  lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + abilityCost);
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

        ChatColor nameColor = ChatColor.WHITE;

        if (rarity.contains("LEGENDARY") || rarity.contains("legendary")){
            lore.add("" + ChatColor.GOLD + ChatColor.BOLD + rarity.toUpperCase());
            nameColor = ChatColor.GOLD;
        } else if (rarity.contains("EPIC") || rarity.contains("epic")){
            lore.add("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + rarity.toUpperCase());
            nameColor = ChatColor.DARK_PURPLE;
        } else if (rarity.contains("RARE") || rarity.contains("rare")){
            lore.add("" + ChatColor.BLUE + ChatColor.BOLD + rarity.toUpperCase());
            nameColor = ChatColor.BLUE;
        } else if (rarity.contains("UNCOMMON") || rarity.contains("uncommon")) {
            lore.add("" + ChatColor.GREEN + ChatColor.BOLD + rarity.toUpperCase());
            nameColor = ChatColor.GREEN;
        } else if (rarity.contains("COMMON") || rarity.contains("common")) {
            lore.add("" + ChatColor.WHITE + ChatColor.BOLD + rarity.toUpperCase());
        }

        if (!(reforgeType == ReforgeType.NONE)) meta.setDisplayName(nameColor + StringUtils.capitalize(reforgeType.toString().toLowerCase()) + " " + name);
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

        if (description != null) {
            nbt.setString("description", description.toString());
        }

        nbt.setBoolean("reforgeable", reforgeable);
        List<String> enchantmentNbt = new ArrayList<>();
        for (ItemEnchantment enchantment : this.enchantments) {
            enchantmentNbt.add(enchantment.getLevel() + ";" + enchantment.getBaseEnchantment().getName());
        }
        nbt.setString("enchantments", enchantmentNbt.toString());
        nbt.setBoolean("enchantGlint", enchantGlint);
        nbt.setBoolean("hasAbility", hasAbility);
        nbt.setString("abilityName", abilityName);
        nbt.setString("abilityType", abilityType);
        nbt.setString("abilityCooldown", abilityCooldown);
        nbt.setInteger("abilityCost", abilityCost);
        nbt.setString("abilityDescription", abilityDescription.toString());
        nbt.setInteger("damage", damage);
        nbt.setInteger("strength", strength + reforgeType.getStrength());
        nbt.setInteger("critChance", critChance + reforgeType.getCritChance());
        nbt.setInteger("critDamage", critDamage + reforgeType.getCritDamage());
        nbt.setInteger("attackSpeed", attackSpeed + reforgeType.getAttackSpeed());
        nbt.setInteger("intelligence", intelligence + reforgeType.getMana());
        nbt.setInteger("speed", speed + reforgeType.getSpeed());
        nbt.setInteger("defense", defense + reforgeType.getDefense());
        nbt.setString("skyblockId", skyblockId);

        this.stack = nbt.getItem();

        return this.stack;
    }

    public boolean hasEnchantment(SkyblockEnchantment enchantment) {
        for (ItemEnchantment itemEnchantment : this.enchantments) {
            if (itemEnchantment.getBaseEnchantment().equals(enchantment)) return true;
        }
        return false;
    }

    public ItemEnchantment getEnchantment(String name) {
        for (ItemEnchantment enchantment : this.enchantments) {
            if (enchantment.getBaseEnchantment().getName().equalsIgnoreCase(name)) return enchantment;
        }

        return null;
    }

    public void addEnchantment(String name, int level) {
        SkyblockEnchantment enchantment = Skyblock.getPlugin(Skyblock.class).getEnchantmentHandler().getEnchantment(name);

        if (enchantment == null) return;

        this.enchantments.add(new ItemEnchantment(enchantment, level));
    }

    public void give(Player player) {
        player.getInventory().addItem(createStack());
    }

    public void set(Player player, int slot) { player.getInventory().setItem(slot, createStack()); }

}
