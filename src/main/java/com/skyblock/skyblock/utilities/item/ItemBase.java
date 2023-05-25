package com.skyblock.skyblock.utilities.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.Item;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.enums.Reforge;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.enchantment.ItemEnchantment;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantment;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantmentHandler;
import com.skyblock.skyblock.features.enchantment.enchantments.sword.CriticalEnchantment;
import com.skyblock.skyblock.features.items.SkyblockItemHandler;
import com.skyblock.skyblock.features.reforge.ReforgeData;
import com.skyblock.skyblock.features.reforge.ReforgeStat;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Function;

@Getter
@Setter
public class ItemBase {

    private List<String> description;
    private Rarity rarityEnum;
    private Material material;
    private String skyblockId;
    private String rarity;
    private String name;
    private int amount;
    private Item item;

    private boolean reforgeable;
    private Reforge reforge;

    private boolean appliedCritical;
    private boolean isThick;

    private List<ItemEnchantment> enchantments;
    private boolean enchantGlint;

    private List<String> abilityDescription;
    private NBTTagCompound compound;
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
    private int health;
    private int damage;
    private int speed;

    private ItemStack stack;
    private ItemStack orig;

    private Function<ItemBase, ItemBase> dynamicGeneration = null;

    public ItemBase(ItemStack item) {
        NBTItem nbt = new NBTItem(item);

        if (!nbt.getBoolean("skyblockItem")) throw new IllegalArgumentException("Item is not a skyblock item.");

        this.material = item.getType();
        this.amount = item.getAmount();
        this.name = nbt.getString("name");
        this.rarity = nbt.getString("rarity");
        try {
            if (!ChatColor.stripColor(this.rarity).startsWith("VERY")) {
                this.rarityEnum = Rarity.valueOf(ChatColor.stripColor(rarity.split(" ")[0]).toUpperCase().replace(" ", "_"));
            } else this.rarityEnum = Rarity.VERY_SPECIAL;
        } catch (Exception ex) {
            this.rarityEnum = Rarity.COMMON;
        }
        try {
            this.reforge = Reforge.getReforge(nbt.getString("reforgeType"));
        } catch (Exception ex) {
            this.reforge = Reforge.NONE;
        }

        this.reforgeable = nbt.getBoolean("reforgeable");
        this.appliedCritical = nbt.getBoolean("appliedCritical");
        this.isThick = nbt.getBoolean("isThick");
        this.enchantments = new ArrayList<>();
        String enchantmentsStr = nbt.getString("enchantments");

        if (enchantmentsStr.length() > 0) {
            String[] enchantmentsList = enchantmentsStr.substring(1, enchantmentsStr.length() - 1).split(", ");
            try {
                for (String enchantment : enchantmentsList)
                    this.enchantments.add(new ItemEnchantment(Skyblock.getPlugin(Skyblock.class).getEnchantmentHandler().getEnchantment(enchantment.split(";")[1]), Integer.parseInt(enchantment.split(";")[0])));
            } catch (Exception ex) {
                this.enchantments = new ArrayList<>();
            }
        }
        this.enchantGlint = nbt.getBoolean("enchantGlint");
        String abilityDescriptionStr = nbt.getString("abilityDescription");
        this.hasAbility = nbt.getBoolean("hasAbility");
        if (hasAbility) {
            this.abilityDescription = Arrays.asList(abilityDescriptionStr.substring(1, abilityDescriptionStr.length() - 1).split("; "));
            this.abilityCooldown = nbt.getString("abilityCooldown");
            this.abilityName = nbt.getString("abilityName");
            this.abilityType = nbt.getString("abilityType");
            this.abilityCost = nbt.getInteger("abilityCost");
        }

        ReforgeStat stats = reforge.getReforgeData(rarityEnum);

        if (stats == null) stats = new ReforgeStat(rarityEnum, new HashMap<>(), new ReforgeData(null, null, null));

        this.intelligence = nbt.getInteger("intelligence") - stats.get(SkyblockStat.MANA);
        this.attackSpeed = nbt.getInteger("attackSpeed") - stats.get(SkyblockStat.ATTACK_SPEED);
        this.critChance = nbt.getInteger("critChance") - stats.get(SkyblockStat.CRIT_CHANCE);
        this.critDamage = nbt.getInteger("critDamage") - stats.get(SkyblockStat.CRIT_DAMAGE);
        this.strength = nbt.getInteger("strength") - stats.get(SkyblockStat.STRENGTH);
        this.defense = nbt.getInteger("defense") - stats.get(SkyblockStat.DEFENSE);
        this.damage = nbt.getInteger("damage") - stats.get(SkyblockStat.DAMAGE);
        this.health = nbt.getInteger("health") - stats.get(SkyblockStat.HEALTH);
        this.speed = nbt.getInteger("speed") - stats.get(SkyblockStat.SPEED);
        this.skyblockId = nbt.getString("skyblockId");
        this.item = this.getItem(rarity);

        String descriptionStr = nbt.getString("description");
        if (descriptionStr.length() > 0) {
            String[] descriptionArr = descriptionStr.substring(1, descriptionStr.length() - 1).split("; ");
            String[] descriptionArrClone = Arrays.copyOf(descriptionArr, descriptionArr.length + 1);
            descriptionArrClone[descriptionArrClone.length - 1] = "";
            this.description = Arrays.asList(descriptionArrClone);
        } else {
            this.description = new ArrayList<>();
        }

        this.stack = item;
        this.orig = item;

        this.dynamicGeneration = ItemHandler.SKYBLOCK_ID_TO_ITEM.get(this.skyblockId);
    }

    public ItemBase(Material material, String name, Reforge reforgeType, int amount, List<String> description, List<ItemEnchantment> enchantments, boolean enchantGlint, boolean hasAbility, String abilityName, List<String> abilityDescription, String abilityType, int abilityCost, String abilityCooldown, String rarity, String skyblockId, int damage, int strength, int health, int critChance, int critDamage, int attackSpeed, int intelligence, int speed, int defense, boolean reforgeable) {
        this(material, name, reforgeType, false, amount, description, enchantments, enchantGlint, hasAbility, abilityName, abilityDescription, abilityType, abilityCost, abilityCooldown, rarity, skyblockId, damage, strength, health, critChance, critDamage, attackSpeed, intelligence, speed, defense, reforgeable);
    }

    public ItemBase(Material material, String name, Reforge reforgeType, boolean isThick, int amount, List<String> description, List<ItemEnchantment> enchantments, boolean enchantGlint, boolean hasAbility, String abilityName, List<String> abilityDescription, String abilityType, int abilityCost, String abilityCooldown, String rarity, String skyblockId, int damage, int strength, int health, int critChance, int critDamage, int attackSpeed, int intelligence, int speed, int defense, boolean reforgeable, HashMap<String, Object> nbt, Function<ItemBase, ItemBase> after) {
        this(material, name, reforgeType, isThick, amount, description, enchantments, enchantGlint, hasAbility, abilityName, abilityDescription, abilityType, abilityCost, abilityCooldown, rarity, skyblockId, damage, strength, health, critChance, critDamage, attackSpeed, intelligence, speed, defense, reforgeable);

        NBTItem nbtItem = new NBTItem(this.stack);

        for (String key : nbt.keySet()) {
            Object value = nbt.get(key);
            if (value instanceof String) nbtItem.setString(key, (String) value);
            else if (value instanceof Integer) nbtItem.setInteger(key, (Integer) value);
            else if (value instanceof Boolean) nbtItem.setBoolean(key, (Boolean) value);
        }

        this.stack = nbtItem.getItem();

        this.dynamicGeneration = after;

        this.regenerate();
    }

    public ItemBase(Material material, String name, Reforge reforgeType, boolean isThick, int amount, List<String> description, List<ItemEnchantment> enchantments, boolean enchantGlint, boolean hasAbility, String abilityName, List<String> abilityDescription, String abilityType, int abilityCost, String abilityCooldown, String rarity, String skyblockId, int damage, int strength, int health, int critChance, int critDamage, int attackSpeed, int intelligence, int speed, int defense, boolean reforgeable) {
        this.description = description;
        this.material = material;
        this.name = name;
        this.rarity = rarity;
        this.amount = amount;

        this.reforge = reforgeType;
        this.reforgeable = reforgeable;

        this.appliedCritical = false;
        this.isThick = isThick;

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
        this.health = health;
        this.damage = damage;
        this.speed = speed;

        this.skyblockId = skyblockId;

        this.createStack();
    }

    public ItemStack createStack() {
        if (orig == null) this.orig = new ItemStack(material, amount);
        this.stack = orig.clone();

        ItemMeta meta = this.stack.getItemMeta();

        List<String> lore = new ArrayList<>();

        if (this.reforge == null) this.reforge = Reforge.NONE;

        this.rarityEnum = this.getRarity(this.rarity);
        this.item = this.getItem(this.rarity);

        ReforgeStat reforgeData = this.reforge.getReforgeData(this.rarityEnum);

        int rStrength = reforgeData.get(SkyblockStat.STRENGTH);
        int rCritChance = reforgeData.get(SkyblockStat.CRIT_CHANCE);
        int rCritDamage = reforgeData.get(SkyblockStat.CRIT_DAMAGE);
        int rAttackSpeed = reforgeData.get(SkyblockStat.ATTACK_SPEED);
        int rSpeed = reforgeData.get(SkyblockStat.SPEED);
        int rMana = reforgeData.get(SkyblockStat.MANA);
        int rDefense = reforgeData.get(SkyblockStat.DEFENSE);
        int rHealth = reforgeData.get(SkyblockStat.HEALTH);

        if (this.getEnchantment("critical") != null && this.hasEnchantment(this.getEnchantment("critical").getBaseEnchantment())
                && !this.appliedCritical
        ) {
            damage += CriticalEnchantment.getCritDamageIncrease.apply(this.getEnchantment("critical").getLevel());

            this.appliedCritical = true;
        }

        /*
          Stats
         */
        if (damage != 0) lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + damage);
        if (strength != 0 || rStrength > 0)
            lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+" + (strength + rStrength) + ((reforge != Reforge.NONE && rStrength > 0) || isThick ? " " + ChatColor.BLUE + "(+" + rStrength + (isThick ? 100 : 0) + ")" : ""));
        if (critChance != 0 || rCritChance > 0)
            lore.add(ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "+" + (critChance + rCritChance) + "%" + (reforge != Reforge.NONE && rCritChance > 0 ? " " + ChatColor.BLUE + "(+" + rCritChance + "%)" : ""));
        if (critDamage != 0 || rCritDamage > 0)
            lore.add(ChatColor.GRAY + "Crit Damage: " + ChatColor.RED + "+" + (critDamage + rCritDamage) + "%" + (rCritDamage > 0 ? " " + ChatColor.BLUE + "(+" + rCritDamage + "%)" : ""));
        if (attackSpeed != 0 || rAttackSpeed > 0)
            lore.add(ChatColor.GRAY + "Attack Speed: " + ChatColor.RED + "+" + (attackSpeed + rAttackSpeed + "%" + (reforge != Reforge.NONE && rAttackSpeed > 0 ? " " + ChatColor.BLUE + "(+" + rAttackSpeed + "%)" : "")));
        if ((speed != 0 || rSpeed != 0) && (intelligence != 0 || rMana != 0) && (defense != 0 || rDefense != 0) && (health != 0 || rHealth > 0)) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + (health + rMana) + (reforge != Reforge.NONE && rHealth > 0 ? " HP " + ChatColor.BLUE + "(+" + rHealth + ")" : ""));
            lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+" + (defense + rHealth) + (reforge != Reforge.NONE && rHealth > 0 ? " " + ChatColor.BLUE + "(+" + rHealth + ")" : ""));
            lore.add(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + (speed + rSpeed) + (reforge != Reforge.NONE && rSpeed > 0 ? " " + ChatColor.BLUE + "(+" + rSpeed + ")" : ""));
            lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+" + (intelligence + rMana) + (reforge != Reforge.NONE && rMana > 0 ? " " + ChatColor.BLUE + "(+" + rMana + ")" : ""));
        } else if (health != 0 || rHealth > 0) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + (health + rHealth) + (reforge != Reforge.NONE && rHealth > 0 ? " HP " + ChatColor.BLUE + "(+" + rHealth + ")" : ""));
        } else if (defense != 0 || rDefense != 0) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+" + (defense + rHealth) + (reforge != Reforge.NONE && rDefense > 0 ? " " + ChatColor.BLUE + "(+" + rHealth + ")" : ""));
        } else if (speed != 0 || rSpeed != 0) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + (speed + rSpeed) + (reforge != Reforge.NONE && rSpeed > 0 ? " " + ChatColor.BLUE + "(+" + rSpeed + ")" : ""));
        } else if (intelligence != 0 || rMana != 0) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+" + (intelligence + rMana) + (reforge != Reforge.NONE && rMana > 0 ? " " + ChatColor.BLUE + "(+" + rMana + ")" : ""));
        }

        if (description.size() != 0 && !lore.isEmpty()) lore.add("");

        /*
        Enchantments
         */

        if (this.enchantments.size() >= 1) {
            if (this.enchantments.size() <= 3) {
                for (ItemEnchantment enchantment : this.enchantments) {
                    lore.add(ChatColor.BLUE + enchantment.getBaseEnchantment().getDisplayName() + " " + Util.toRoman(enchantment.getLevel()));
                    for (String s : Util.buildLore(enchantment.getBaseEnchantment().getDescription(enchantment.getLevel()))) {
                        lore.add(ChatColor.GRAY + s);
                    }
                }
            } else if (this.enchantments.size() <= 7) {
                for (ItemEnchantment enchantment : this.enchantments) {
                    lore.add(ChatColor.BLUE + enchantment.getBaseEnchantment().getDisplayName() + " " + Util.toRoman(enchantment.getLevel()));
                }
            } else {
                List<String> enchantmentLore = new ArrayList<>();

                int maxCharsPerLine = 45;

                StringBuilder currentLine = new StringBuilder();

                for (ItemEnchantment enchantment : this.enchantments) {
                    if (currentLine.length() + enchantment.getBaseEnchantment().getDisplayName().length() + 1 > maxCharsPerLine) {
                        enchantmentLore.add(currentLine.toString());
                        currentLine = new StringBuilder();
                    }

                    currentLine.append(ChatColor.BLUE).append(enchantment.getBaseEnchantment().getDisplayName()).append(" ").append(Util.toRoman(enchantment.getLevel())).append(ChatColor.BLUE).append(", ");
                }

                if (currentLine.length() > 0) {
                    currentLine.delete(currentLine.length() - 2, currentLine.length());
                    enchantmentLore.add(currentLine.toString());
                }

                lore.addAll(enchantmentLore);
            }

            lore.add("");
        }

        /*
          Description
         */
        if (description != null && description.size() != 0 && !description.get(0).equals("laceholder descriptio"))
            lore.addAll(description);

        for (String s : lore) lore.set(lore.indexOf(s), s.replaceAll(";", ""));

        /*
          Ability
         */
        if (hasAbility) {
            lore.add(ChatColor.GOLD + "Ability: " + abilityName + "" + ChatColor.YELLOW + ChatColor.BOLD + abilityType);
            lore.addAll(abilityDescription);

            if (abilityCost != 0) lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + abilityCost);
            if (!Objects.equals(abilityCooldown, ""))
                lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + abilityCooldown);

            lore.add("");
        }

        /*
          Reforge
         */

        if (reforgeable) lore.add(ChatColor.DARK_GRAY + "This item can be reforged!");

        /*
          Rarity
         */

        if (lore.size() == 1) lore.clear();

        ChatColor nameColor = ChatColor.WHITE;

        if (this.rarityEnum.equals(Rarity.MYTHIC)) {
            lore.add("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + rarity.toUpperCase());
            nameColor = ChatColor.LIGHT_PURPLE;
        } else if (this.rarityEnum.equals(Rarity.LEGENDARY)) {
            lore.add("" + ChatColor.GOLD + ChatColor.BOLD + rarity.toUpperCase());
            nameColor = ChatColor.GOLD;
        } else if (this.rarityEnum.equals(Rarity.EPIC)) {
            lore.add("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + rarity.toUpperCase());
            nameColor = ChatColor.DARK_PURPLE;
        } else if (this.rarityEnum.equals(Rarity.RARE)) {
            lore.add("" + ChatColor.BLUE + ChatColor.BOLD + rarity.toUpperCase());
            nameColor = ChatColor.BLUE;
        } else if (this.rarityEnum.equals(Rarity.UNCOMMON)) {
            lore.add("" + ChatColor.GREEN + ChatColor.BOLD + rarity.toUpperCase());
            nameColor = ChatColor.GREEN;
        } else if (this.rarityEnum.equals(Rarity.COMMON))
            lore.add("" + ChatColor.WHITE + ChatColor.BOLD + rarity.toUpperCase());

        if (reforge != Reforge.NONE)
            meta.setDisplayName(nameColor + StringUtils.capitalize(reforge.toString().toLowerCase()) + (isThick ? " Thick " : " ") + name);
        else meta.setDisplayName(name);

        meta.setLore(lore);

        if (enchantGlint || this.enchantments.size() > 0) meta.addEnchant(Enchantment.LUCK, 1, true);

        meta.spigot().setUnbreakable(true);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        this.stack.setItemMeta(meta);

        NBTItem nbt = new NBTItem(this.stack);

        nbt.setBoolean("skyblockItem", true);
        nbt.setString("name", name);
        nbt.setString("rarity", rarity);
        nbt.setString("reforgeType", reforge.toString());
        nbt.setString("item", item.toString());

        if (description != null && description.size() > 1) {
            StringBuilder description = new StringBuilder();

            for (String s : getDescription()) {
                description.append("; ").append(s);
            }

            nbt.setString("description", description.substring(1, description.length() - 1));
        } else {
            nbt.setString("description", "");
        }

        nbt.setBoolean("reforgeable", reforgeable);
        nbt.setBoolean("appliedCritical", appliedCritical);
        nbt.setBoolean("isThick", isThick);
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

        if (getAbilityDescription() != null && getAbilityDescription().size() > 1) {
            StringBuilder abilityDescription = new StringBuilder();

            for (String s : getAbilityDescription()) {
                abilityDescription.append("; ").append(s);
            }

            nbt.setString("abilityDescription", abilityDescription.substring(1, abilityDescription.length() - 1));
        }

        nbt.setInteger("damage", damage);
        nbt.setInteger("strength", strength + rStrength);
        nbt.setInteger("critChance", critChance + rCritChance);
        nbt.setInteger("critDamage", critDamage + rCritDamage);
        nbt.setInteger("attackSpeed", attackSpeed + rAttackSpeed);
        nbt.setInteger("intelligence", intelligence + rMana);
        nbt.setInteger("speed", speed + rSpeed);
        nbt.setInteger("defense", defense + rDefense);
        nbt.setInteger("health", health + rHealth);
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

    public void setEnchantment(String name, int level) {
        SkyblockEnchantment enchantment = Skyblock.getPlugin(Skyblock.class).getEnchantmentHandler().getEnchantment(name);

        if (enchantment == null) return;

        ItemEnchantment itemEnchantment = new ItemEnchantment(enchantment, level);

        for (int i = 0; i < this.enchantments.size(); i++) {
            SkyblockEnchantment found = this.enchantments.get(i).getBaseEnchantment();

            if (found.getName().replace(" ", "_").equalsIgnoreCase(name)) {
                this.enchantments.set(i, itemEnchantment);
                found.onUnEnchant(this);
                itemEnchantment.getBaseEnchantment().onEnchant(this);

                return;
            }
        }

        this.enchantments.add(itemEnchantment);
        itemEnchantment.getBaseEnchantment().onEnchant(this);
    }

    public void addEnchantment(String name, int level) {
        SkyblockEnchantment enchantment = Skyblock.getPlugin(Skyblock.class).getEnchantmentHandler().getEnchantment(name);

        if (enchantment == null) return;

        this.enchantments.add(new ItemEnchantment(enchantment, level));
    }

    public void give(Player player) {
        player.getInventory().addItem(createStack());
    }

    public void set(Player player, int slot) {
        player.getInventory().setItem(slot, createStack());
    }

    public int getReforgeCost() {
        switch (this.rarityEnum) {
            case VERY_SPECIAL:
                return 50000;
            case SPECIAL:
                return 25000;
            case DIVINE:
                return 15000;
            case MYTHIC:
                return 10000;
            case LEGENDARY:
                return 5000;
            case EPIC:
                return 2500;
            case RARE:
                return 1000;
            case UNCOMMON:
                return 500;
            case COMMON:
                return 250;
            default:
                return 0;
        }
    }

    public Item getItem(String rarity) {
        if (Item.SWORD.containsQualifiedName(rarity)) return Item.SWORD;
        else if (Item.RANGED.containsQualifiedName(rarity)) return Item.RANGED;
        else if (Item.ARMOR.containsQualifiedName(rarity)) return Item.ARMOR;
        else return Item.NONE;
    }

    public Rarity getRarity(String rarity) {
        if (rarity.toUpperCase().contains("MYTHIC")) return Rarity.MYTHIC;
        else if (rarity.toUpperCase().contains("LEGENDARY")) return Rarity.LEGENDARY;
        else if (rarity.toUpperCase().contains("EPIC")) return Rarity.EPIC;
        else if (rarity.toUpperCase().contains("RARE")) return Rarity.RARE;
        else if (rarity.toUpperCase().contains("UNCOMMON")) return Rarity.UNCOMMON;
        else return Rarity.COMMON;
    }

    public void setStat(SkyblockStat stat, int value) {
        if (stat.equals(SkyblockStat.DAMAGE)) this.damage = value;
        else if (stat.equals(SkyblockStat.STRENGTH)) this.strength = value;
        else if (stat.equals(SkyblockStat.CRIT_CHANCE)) this.critChance = value;
        else if (stat.equals(SkyblockStat.CRIT_DAMAGE)) this.critDamage = value;
        else if (stat.equals(SkyblockStat.ATTACK_SPEED)) this.attackSpeed = value;
        else if (stat.equals(SkyblockStat.MANA)) this.intelligence = value;
        else if (stat.equals(SkyblockStat.SPEED)) this.speed = value;
        else if (stat.equals(SkyblockStat.DEFENSE)) this.defense = value;
        else if (stat.equals(SkyblockStat.HEALTH)) this.health = value;
    }

    public int getStat(SkyblockStat stat) {
        if (stat.equals(SkyblockStat.DAMAGE)) return this.damage;
        else if (stat.equals(SkyblockStat.STRENGTH)) return this.strength;
        else if (stat.equals(SkyblockStat.CRIT_CHANCE)) return this.critChance;
        else if (stat.equals(SkyblockStat.CRIT_DAMAGE)) return this.critDamage;
        else if (stat.equals(SkyblockStat.ATTACK_SPEED)) return this.attackSpeed;
        else if (stat.equals(SkyblockStat.MANA)) return this.intelligence;
        else if (stat.equals(SkyblockStat.SPEED)) return this.speed;
        else if (stat.equals(SkyblockStat.DEFENSE)) return this.defense;
        else if (stat.equals(SkyblockStat.HEALTH)) return this.health;
        else return 0;
    }

    public static ItemStack reforge(ItemStack stack, Reforge reforge) {
        ItemBase base = new ItemBase(stack);

        base.createStack();

        base.setReforge(reforge);

        return base.createStack();
    }

    public void addNbt(String path, Object value) {
        NBTItem item = new NBTItem(this.stack);

        if (value instanceof Integer) item.setInteger(path, (Integer) value);
        else if (value instanceof String) item.setString(path, (String) value);
        else if (value instanceof Double) item.setDouble(path, (Double) value);
        else if (value instanceof Float) item.setFloat(path, (Float) value);
        else if (value instanceof Long) item.setLong(path, (Long) value);
        else if (value instanceof Boolean) item.setBoolean(path, (Boolean) value);
        else if (value instanceof Byte) item.setByte(path, (Byte) value);
        else if (value instanceof Short) item.setShort(path, (Short) value);

        this.stack = item.getItem();
    }

    public boolean regenerate() {
        SkyblockItemHandler handler = Skyblock.getPlugin().getSkyblockItemHandler();
        if (handler != null && handler.isRegistered(orig)) {
            handler.getRegistered(orig).onRegenerate(this);

            return true;
        }

        return false;
    }

    public void maxItem() {
        boolean isSword = this.rarity.toUpperCase().contains("SWORD");
        boolean isArmor = this.rarity.toUpperCase().contains("HELMET") || this.rarity.toUpperCase().contains("CHESTPLATE") || this.rarity.toUpperCase().contains("LEGGINGS") || this.rarity.toUpperCase().contains("BOOTS");
        boolean isRanged = this.rarity.toUpperCase().contains("BOW");
        boolean isHelmet = this.rarity.toUpperCase().contains("HELMET");
        boolean isBoots = this.rarity.toUpperCase().contains("BOOTS");

        SkyblockEnchantmentHandler enchantmentHandler = Skyblock.getPlugin().getEnchantmentHandler();

        List<SkyblockEnchantment> enchantments = new ArrayList<>();

        if (isSword) {
            enchantments.addAll(enchantmentHandler.getEnchantments("sword"));

            this.setReforge(Reforge.SPICY);
        } else if (isHelmet) {
            enchantments.addAll(enchantmentHandler.getEnchantments("helmet"));

            this.setReforge(Reforge.FIERCE);
        } else if (isBoots) {
            enchantments.addAll(enchantmentHandler.getEnchantments("boots"));

            this.setReforge(Reforge.FIERCE);
        } else if (isArmor) {
            enchantments.addAll(enchantmentHandler.getEnchantments("armor"));

            this.setReforge(Reforge.FIERCE);
        } else if (isRanged) {
            enchantments.addAll(enchantmentHandler.getEnchantments("bow"));

            this.setReforge(Reforge.RAPID);
        }

        for (SkyblockEnchantment enchantment : enchantments) {
            this.addEnchantment(enchantment.getName(), enchantment.getMaxLevel());
        }

        this.createStack();
    }

    public int getIntelligence() {
        return intelligence + getReforgeStat(SkyblockStat.MANA);
    }

    public int getAttackSpeed() {
        return attackSpeed + getReforgeStat(SkyblockStat.ATTACK_SPEED);
    }

    public int getCritChance() {
        return critChance + getReforgeStat(SkyblockStat.CRIT_CHANCE);
    }

    public int getCritDamage() {
        return critDamage + getReforgeStat(SkyblockStat.CRIT_DAMAGE);
    }

    public int getStrength() {
        return strength + getReforgeStat(SkyblockStat.STRENGTH);
    }

    public int getDefense() {
        return defense + getReforgeStat(SkyblockStat.DEFENSE);
    }

    public int getHealth() {
        return health + getReforgeStat(SkyblockStat.HEALTH);
    }

    public int getDamage() {
        return damage + getReforgeStat(SkyblockStat.DAMAGE);
    }

    public int getSpeed() {
        return speed + getReforgeStat(SkyblockStat.SPEED);
    }

    private int getReforgeStat(SkyblockStat stat) {
        if (reforge == null) return 0;
        if (reforge.getReforgeData(rarityEnum) == null) return 0;

        return reforge.getReforgeData(rarityEnum).get(stat);
    }

}
