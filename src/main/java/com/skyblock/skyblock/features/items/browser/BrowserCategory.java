package com.skyblock.skyblock.features.items.browser;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.ItemBlock;
import net.minecraft.server.v1_8_R3.ItemSpade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

import static org.bukkit.Material.*;

@Getter
@AllArgsConstructor
public enum BrowserCategory {

    ALL(category(), (base) -> true),
    SWORD(category(DIAMOND_SWORD, "Sword"), validation("Sword")),
    BOW(category(Material.BOW, "Bow"), validation("Bow")),
    HELMET(category(Skyblock.getPlugin().getItemHandler().getItem("superior_dragon_helmet"), "Helmet"), validation("Helmet")),
    CHESTPLATE(category(Skyblock.getPlugin().getItemHandler().getItem("superior_dragon_chestplate"), "Chestplate"), validation("Chestplate")),
    LEGGING(category(Skyblock.getPlugin().getItemHandler().getItem("superior_dragon_leggings"), "Legging"), validation("Leggings")),
    BOOT(category(Skyblock.getPlugin().getItemHandler().getItem("superior_dragon_boots"), "Boot"), validation("Boots")),
    PICKAXE(category(Skyblock.getPlugin().getItemHandler().getItem("stonk_pickaxe"), "Pickaxe"), validation("Pickaxe")),
    AXE(category(WOOD_AXE, "Axe"), validation(" Axe")),
    SHOVEL(category(WOOD_SPADE, "Shovel"), validation("Shovel").and((base) -> CraftItemStack.asNMSCopy(base.createStack()).getItem() instanceof ItemSpade)),
    HOE(category(DIAMOND_HOE, "Hoe"), validation("Hoe")),
    ACCESSORIE(category(Skyblock.getPlugin().getItemHandler().getItem("ender_artifact"), "Talisman"), validation("Accessory")),
    FISHING(category(FISHING_ROD, "Fishing Rod"), validation("Fishing Rod")),
    WAND(category(Skyblock.getPlugin().getItemHandler().getItem("wand_of_mending"), "Wand"), validation("Wand")),
    TRAVEL_SCROLL(category(Skyblock.getPlugin().getItemHandler().getItem("travel_scroll_to_the_end"), "Travel Scroll"), validation("Travel Scroll")),
    MINION(category(Skyblock.getPlugin().getItemHandler().getItem("cobblestone_generator_1"), "Minion"), (base) -> base.getSkyblockId().contains("generator")),
    BLOCKS(category(COBBLESTONE, "Block"), (base) -> CraftItemStack.asNMSCopy(base.createStack()).getItem() instanceof ItemBlock),
    MISC(category(STICK, "Miscellaneous"), (base) -> {
        for (BrowserCategory c : values()) {
            if (c.name().equals("MISC") || c.name().equals("ALL")) continue;
            if (c.getValidate().test(base)) return false;
        }

        return true;
    });

    private final ItemStack item;
    private final Predicate<ItemBase> validate;

    private static ItemStack category() {
        return category(new ItemStack(NETHER_STAR), "All", "item");
    }

    private static ItemStack category(Material material, String name) {
        return category(new ItemStack(material), name, name);
    }

    private static ItemStack category(ItemStack base, String name) {
        return category(base, name, name);
    }

    private static ItemStack category(ItemStack base, String name, String lore) {
        return new ItemBuilder(base).setDisplayName(ChatColor.GREEN + name + "s").setLore(ChatColor.GRAY + "View all " + lore.toLowerCase() + " items.").addItemFlags(ItemFlag.values()).toItemStack();
    }

    private static Predicate<ItemBase> validation(String rarity) {
        return (base) -> base.getRarity().contains(rarity.toUpperCase());
    }

}
