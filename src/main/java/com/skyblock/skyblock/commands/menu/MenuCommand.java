package com.skyblock.skyblock.commands.menu;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.bags.Bag;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.listeners.SkyblockMenuListener;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@RequiresPlayer
@Usage(usage = "/sb menu")
@Description(description = "Opens the Skyblock Menu")
public class MenuCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        Inventory inventory = Bukkit.createInventory(null, 54, SkyblockMenuListener.MENU_NAME);

        ItemStack yourSkyblockProfile = this.createSkyblockProfileItem(skyblockPlayer);
        ItemStack yourSkills = this.createSkillsItem();

        int unlockedCollections = 0;
        int totalCollections = 0;

        for (Collection collection : Collection.getCollections()) {
            totalCollections++;

            if (skyblockPlayer.getValue("collection." + collection.getName().toLowerCase() + ".unlocked").equals(true)) unlockedCollections++;
        }

        ItemStack collection = this.getCollectionItem(unlockedCollections, totalCollections);
        ItemStack recipeBook = this.createRecipeBookItem();
        ItemStack trades = this.createTradesItem();
        ItemStack questLog = this.createQuestLogItem();
        ItemStack calendarAndEvents = this.createCalendarItem();
        ItemStack enderChest = this.createEnderChestItem();
        ItemStack pets = this.createPetsItem(skyblockPlayer);
        ItemStack craftingTable = this.createCraftingTableItem();
        ItemStack activeEffects = this.createActiveEffectsItem();

        Util.fillEmpty(inventory);

        inventory.setItem(13, yourSkyblockProfile);

        inventory.setItem(19, yourSkills);
        inventory.setItem(20, collection);
        inventory.setItem(21, recipeBook);
        inventory.setItem(22, trades);
        inventory.setItem(23, questLog);
        inventory.setItem(24, calendarAndEvents);
        inventory.setItem(25, enderChest);

        inventory.setItem(30, pets);
        inventory.setItem(31, craftingTable);
        inventory.setItem(32, activeEffects);

        for (Bag bag : plugin.getBagManager().getBags().values()) {
            if ((boolean) skyblockPlayer.getValue("bag." + bag.getId() + ".unlocked")) {
                inventory.setItem(bag.getSkyblockMenuSlot(), bag.toItemStack());
            }
        }

        player.openInventory(inventory);
    }

    public ItemStack createSkyblockProfileItem(SkyblockPlayer skyblockPlayer) {
        return new ItemBuilder(
                ChatColor.GREEN + "Your SkyBlock Profile",
                Material.SKULL_ITEM,
                1, (short) 3)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addLore(
                        ChatColor.GRAY + "View your equipment, stats,",
                        ChatColor.GRAY + "and more!",
                        "",
                        ChatColor.RED + " ❤ Health " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.HEALTH),
                        ChatColor.GREEN + " ❈ Defence " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.DEFENSE),
                        ChatColor.RED + " ❁ Strength " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.STRENGTH),
                        ChatColor.WHITE + " ✦ Speed " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.SPEED),
                        ChatColor.BLUE + " ☣ Crit Chance " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.CRIT_CHANCE) + "%",
                        ChatColor.BLUE + " ☠ Crit Damage " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.CRIT_DAMAGE) + "%",
                        ChatColor.AQUA + " ✎ Intelligence " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.MANA),
                        ChatColor.YELLOW + " ⚔ Attack Speed " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.ATTACK_SPEED),
                        ChatColor.DARK_AQUA + " ∞ Sea Creature Chance " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.SEA_CREATURE_CHANCE) + "%",
                        ChatColor.AQUA + " ✯ Magic Find " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.MAGIC_FIND),
                        ChatColor.LIGHT_PURPLE + " ♣ Pet Luck " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.PET_LUCK),
                        ChatColor.WHITE + " ❂ True Defense " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.TRUE_DEFENSE),
                        ChatColor.RED + " ⫽ Ferocity " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.FEROCITY),
                        ChatColor.RED + " ✹ Ability Damage " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.ABILITY_DAMAGE),
                        ChatColor.GOLD + " ⸕ Mining Speed " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.MINING_SPEED),
                        "",
                        ChatColor.YELLOW + "Click to view!"
                ).toItemStack();
    }

    public ItemStack createSkillsItem() {
        return new ItemBuilder(
                ChatColor.GREEN + "Your Skills",
                Material.DIAMOND_SWORD)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View your Skill progression and",
                        ChatColor.GRAY + "rewards.", "",
                        ChatColor.GOLD + "14.8 Skill Avg. " + ChatColor.DARK_GRAY + "(non-cosmetic)",
                        "",
                        ChatColor.YELLOW + "Click to view!")
                .toItemStack();
    }

    public ItemStack getCollectionItem(int unlockedCollections, int totalCollections) {
        double percent = Math.round((double) unlockedCollections / (double) totalCollections * 1000) / 10.0;

        int barLength = 20;
        int barPercentPerBlock = 100 / barLength;
        int barFilled = (int) Math.round(percent / barPercentPerBlock);
        int barEmpty = barLength - barFilled;

        String bar = ChatColor.DARK_GREEN + StringUtils.repeat("-", barFilled) + ChatColor.WHITE + StringUtils.repeat("-", barEmpty);

        return new ItemBuilder(
                ChatColor.GREEN + "Collection",
                Material.ITEM_FRAME)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View all of the items available",
                        ChatColor.GRAY + "in SkyBlock. Collect more of an",
                        ChatColor.GRAY + "item to unlock rewards on your",
                        ChatColor.GRAY + "way to becoming a master of",
                        ChatColor.GRAY + "SkyBlock!",
                        "",
                        ChatColor.GRAY + "Collection Unlocked: " + ChatColor.YELLOW + percent + ChatColor.GOLD + "%",
                        bar + ChatColor.YELLOW + " " + unlockedCollections + ChatColor.GOLD + "/" + ChatColor.YELLOW + totalCollections,
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack();
    }

    public ItemStack createRecipeBookItem() {
        return new ItemBuilder(
                ChatColor.GREEN + "Recipe Book",
                Material.BOOK)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "Through your adventure, you will",
                        ChatColor.GRAY + "unlock recipes for all kinds of",
                        ChatColor.GRAY + "special items! You can view how",
                        ChatColor.GRAY + "to craft these items here.",
                        "",
                        ChatColor.GRAY + "Recipe Book Unlocked: " + ChatColor.YELLOW + "38.4" + ChatColor.GOLD + "%",
                        ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "⎯⎯⎯⎯⎯⎯⎯⎯" + ChatColor.WHITE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯" + ChatColor.YELLOW + " 324" + ChatColor.GOLD + "/" + ChatColor.YELLOW + "844",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack();
    }

    public ItemStack createTradesItem() {
        return new ItemBuilder(
                ChatColor.GREEN + "Trades",
                Material.EMERALD)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View your available trades.",
                        ChatColor.GRAY + "These trades are always",
                        ChatColor.GRAY + "Availiable and accessible through",
                        ChatColor.GRAY + "the SkyBlock Menu.",
                        "",
                        ChatColor.GRAY + "Trades Unlocked: " + ChatColor.YELLOW + "83.3" + ChatColor.GOLD + "%",
                        ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯" + ChatColor.WHITE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "⎯⎯⎯⎯" + ChatColor.YELLOW + " 20" + ChatColor.GOLD + "/" + ChatColor.YELLOW + "24",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack();
    }

    public ItemStack createQuestLogItem() {
        return new ItemBuilder(
                ChatColor.GREEN + "Quest Log",
                Material.BOOK_AND_QUILL)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View your active quests,",
                        ChatColor.GRAY + "progress, and rewards.",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack();
    }

    public ItemStack createCalendarItem() {
        return new ItemBuilder(
                ChatColor.GREEN + "Calendar and Events",
                Material.WATCH)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View the SkyBlock Calendar,",
                        ChatColor.GRAY + "upcoming events, and event",
                        ChatColor.GRAY + "rewards!",
                        "",
                        ChatColor.GRAY + "Next Event: " + ChatColor.RED + "211th Season of Jerry",
                        ChatColor.GRAY + "Starting in: " + ChatColor.YELLOW + "0d 22h 24m 35s",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack();
    }

    public ItemStack createEnderChestItem() {
        return new ItemBuilder(
                ChatColor.GREEN + "Ender Chest",
                Material.ENDER_CHEST)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "Store global items that you want",
                        ChatColor.GRAY + "to access at any time from",
                        ChatColor.GRAY + "anywhere here.",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack();
    }

    public ItemStack createPetsItem(SkyblockPlayer skyblockPlayer) {
        return new ItemBuilder(
                ChatColor.GREEN + "Pets",
                Material.BONE)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View and manage all of your",
                        ChatColor.GRAY + "Pets.",
                        "",
                        ChatColor.GRAY + "Level up your pets faster by",
                        ChatColor.GRAY + "gaining xp in their favorite",
                        ChatColor.GRAY + "skill!",
                        "",
                        ChatColor.GRAY + "Selected pet: " + (skyblockPlayer.getPet() == null ? ChatColor.RED + "None" : skyblockPlayer.getPet().getColoredName()),
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack();
    }

    public ItemStack createCraftingTableItem() {
        return new ItemBuilder(
                ChatColor.GREEN + "Crafting Table",
                Material.WORKBENCH)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "Opens the crafting grid.",
                        "",
                        ChatColor.YELLOW + "Click to open!"
                )
                .toItemStack();
    }

    public ItemStack createActiveEffectsItem() {
        return new ItemBuilder(
                ChatColor.GREEN + "Active Effects",
                Material.POTION)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View and manage all of your",
                        ChatColor.GRAY + "active potion effects.",
                        "",
                        ChatColor.GRAY + "Drink Potions or splash them",
                        ChatColor.GRAY + "on the ground to buff yourself!",
                        "",
                        ChatColor.GRAY + "Currently Active: " + ChatColor.YELLOW + "0",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack();
    }

}
