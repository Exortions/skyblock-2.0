package com.skyblock.skyblock.features.auction;

import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum AuctionCategory {

    WEAPON(new ItemBuilder(ChatColor.GOLD + "Weapons", Material.GOLD_SWORD).addLore(ChatColor.DARK_GRAY + "Category", " ", "&7Examples:", "&7 - Swords", "&7 - Bows", "&7 - Axes", "&7 - Magic Weapons", " ", ChatColor.YELLOW + "Click to view items!").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).toItemStack(),
            (itemStack) -> {
                Item stack = CraftItemStack.asNMSCopy(itemStack).getItem();

                try {
                    ItemBase base = new ItemBase(itemStack);

                    if (ChatColor.stripColor(base.getRarity().toLowerCase()).contains("sword")) return true;
                } catch (IllegalArgumentException ignored) { }

                return (stack instanceof ItemAxe) || (stack instanceof ItemSword) || (stack instanceof ItemBow);
            }, (short) 1),
    ACCESSORY(Util.idToSkull(new ItemBuilder(ChatColor.DARK_GREEN + "Accessories", Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()).addLore(ChatColor.DARK_GRAY + "Category", " ", "&7Examples:", "&7 - Talismans", "&7 - Rings", "&7 - Orbs", "&7 - Artifacts", " ", ChatColor.YELLOW + "Click to view items!").toItemStack(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTI5YWYyYTQzOTA5MmU0NTMzMGM1NzdhYzg3NDIxYmM3NmVmYzBiNTg5MzZkMTEwZDJjOThmYzhjOTgwNGNjMiJ9fX0="),
            (itemStack) -> {
                try {
                    ItemBase base = new ItemBase(itemStack);

                    return ChatColor.stripColor(base.getRarity().toLowerCase()).contains("accessory");
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }, (short) 13),
    ARMOR(new ItemBuilder(ChatColor.AQUA + "Armor", Material.DIAMOND_CHESTPLATE).addLore(ChatColor.DARK_GRAY + "Category", " ", "&7Examples:", "&7 - Hats", "&7 - Chestplates", "&7 - Leggings", "&7 - Boots", " ", ChatColor.YELLOW + "Click to view items!").toItemStack(),
            (itemStack) -> {
                Item stack = CraftItemStack.asNMSCopy(itemStack).getItem();

                return ((stack instanceof ItemArmor) || (stack instanceof ItemSkull)) && !ACCESSORY.canPut.test(itemStack);
            }, (short) 11),
    CONSUMABLES(new ItemBuilder(ChatColor.RED + "Consumables", Material.APPLE).addLore(ChatColor.DARK_GRAY + "Category", " ", "&7Examples:", "&7 - Potions", "&7 - Food", "&7 - Books", " ", ChatColor.YELLOW + "Click to view items!").toItemStack(),
            (itemStack) -> {
                Item stack = CraftItemStack.asNMSCopy(itemStack).getItem();

                return (stack instanceof ItemFood) || (stack instanceof ItemBook) || (stack instanceof ItemPotion);
            }, (short) 14),
    BLOCKS(new ItemBuilder(ChatColor.YELLOW + "Blocks", Material.COBBLESTONE).addLore(ChatColor.DARK_GRAY + "Category", " ", "&7Examples:", "&7 - Dirt", "&7 - Stone", "&7 - Any blocks really", " ", ChatColor.YELLOW + "Click to view items!").toItemStack(),
            (itemStack) -> {
                Item stack = CraftItemStack.asNMSCopy(itemStack).getItem();

                return (stack instanceof ItemBlock) && !ARMOR.canPut.test(itemStack);
            }, (short) 12),
    MISC(new ItemBuilder(ChatColor.LIGHT_PURPLE + "Tools & Misc", Material.STICK).addLore(ChatColor.DARK_GRAY + "Category", " ", "&7Examples:", "&7 - Tools", "&7 - Specials", "&7 - Magic", "&7 - Staff items", " ", ChatColor.YELLOW + "Click to view items!").toItemStack(),
            (itemStack) -> (!BLOCKS.getCanPut().test(itemStack)) &&
                    (!CONSUMABLES.getCanPut().test(itemStack)) &&
                    (!ACCESSORY.getCanPut().test(itemStack)) &&
                    (!ARMOR.getCanPut().test(itemStack)) &&
                    (!WEAPON.getCanPut().test(itemStack)), (short) 10),
    ALL(new ItemStack(Material.DIRT), (itemStack) -> true, (short) 15);

    private final ItemStack display;
    private final Predicate<ItemStack> canPut;
    private final short color;

}
