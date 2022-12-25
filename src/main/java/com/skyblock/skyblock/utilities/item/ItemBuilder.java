package com.skyblock.skyblock.utilities.item;

import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class ItemBuilder {

    private ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder() {
        item = new ItemStack(Material.DIRT);
        meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack stack) {
        item = stack;
        meta = item.getItemMeta();
    }

    public ItemBuilder(String name) {
        item = new ItemStack(Material.DIRT);
        meta = item.getItemMeta();
        setDisplayName(name);
    }

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        meta = item.getItemMeta();
    }

    public ItemBuilder(String name, int amount) {
        item = new ItemStack(Material.DIRT, amount);
        meta = item.getItemMeta();
        setDisplayName(name);
    }

    public ItemBuilder(String name, Material material) {
        item = new ItemStack(material);
        meta = item.getItemMeta();
        setDisplayName(name);
    }

    public ItemBuilder(String name, Material material, short dmg) {
        item = new ItemStack(material, 1, dmg);
        meta = item.getItemMeta();
        setDisplayName(name);
    }

    public ItemBuilder(String name, Material material, int amount) {
        item = new ItemStack(material, amount);
        meta = item.getItemMeta();
        setDisplayName(name);
    }

    public ItemBuilder(String name, Material material, int amount, short damage) {
        item = new ItemStack(material, amount, damage);
        meta = item.getItemMeta();
        setDisplayName(name);
    }

    public ItemBuilder setDamage(int damage) {
        item.setDurability((short) damage);
        return this;
    }

    public ItemBuilder setDamage(short damage) {
        item.setDurability(damage);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        item.setType(material);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        List<String> ls = new ArrayList<>();
        for(String s : lore) {
            ls.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(ls);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        List<String> ls;
        if (meta.getLore() != null) ls = meta.getLore(); else ls = new ArrayList<>();
        ls.addAll(lore);
        meta.setLore(ls);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(String lore) {
        List<String> ls;
        if (meta.getLore() != null) ls = meta.getLore(); else ls = new ArrayList<>();
        ls.add(ChatColor.translateAlternateColorCodes('&', lore));
        meta.setLore(ls);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        List<String> ls;
        if (meta.getLore() != null) ls = meta.getLore(); else ls = new ArrayList<>();
        for(String s : lore) {
            ls.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(ls);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment) {
        meta.addEnchant(enchantment, 1, false);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, false);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreMaxLevel) {
        meta.addEnchant(enchantment, level, ignoreMaxLevel);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        meta.removeEnchant(enchantment);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchantmentGlint() {
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeEnchantmentGlint() {
        meta.removeEnchant(Enchantment.LUCK);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        meta.addItemFlags(itemFlag);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeItemFlag(ItemFlag itemFlag) {
        meta.removeItemFlags(itemFlag);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeItemFlags(ItemFlag... itemFlags) {
        meta.removeItemFlags(itemFlags);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder dyeColor(Color color) {
        LeatherArmorMeta leather = (LeatherArmorMeta) meta;
        leather.setColor(color);
        item.setItemMeta(leather);
        return this;
    }

    public ItemBuilder addNBT(String key, String value) {
        NBTItem nbt = new NBTItem(item);

        nbt.setString(key, value);

        this.item = nbt.getItem();

        return this;
    }

    public ItemBuilder addNBT(String key, int value) {
        NBTItem nbt = new NBTItem(item);

        nbt.setInteger(key, value);

        this.item = nbt.getItem();

        return this;
    }

    public ItemBuilder addNBT(String key, double value) {
        NBTItem nbt = new NBTItem(item);

        nbt.setDouble(key, value);

        this.item = nbt.getItem();

        return this;
    }

    public ItemBuilder addNBT(String key, boolean value) {
        NBTItem nbt = new NBTItem(item);

        nbt.setBoolean(key, value);

        this.item = nbt.getItem();

        return this;
    }

    public ItemBuilder setSkullID(String id) {
        this.item = Util.idToSkull(item, id);

        return this;
    }

    public ItemStack toItemStack(){
        return item;
    }

}