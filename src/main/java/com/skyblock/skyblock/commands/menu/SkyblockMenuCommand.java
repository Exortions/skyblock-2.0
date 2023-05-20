package com.skyblock.skyblock.commands.menu;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.bags.Bag;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.crafting.gui.CraftingGUI;
import com.skyblock.skyblock.listeners.SkyblockMenuListener;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.TrueAlias;
import com.skyblock.skyblock.utilities.command.annotations.Alias;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiresPlayer
@Alias(aliases = "menu")
@Usage(usage = "/sb skyblockmenu")
@Description(description = "Opens the Skyblock Menu")
public class SkyblockMenuCommand implements Command, TrueAlias<SkyblockMenuCommand> {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        Inventory inventory = Bukkit.createInventory(null, 54, SkyblockMenuListener.MENU_NAME);

        ItemStack yourSkyblockProfile = createSkyblockProfileItem(skyblockPlayer, true);
        ItemStack yourSkills = this.createSkillsItem(skyblockPlayer);

        int unlockedCollections = skyblockPlayer.getIntValue("collection.unlocked");
        int totalCollections = Collection.getCollections().size();

        ItemStack collection = this.getCollectionItem(unlockedCollections, totalCollections);
        ItemStack recipeBook = SkyblockMenuCommand.createRecipeBookItem(skyblockPlayer);
        ItemStack trades = this.createTradesItem();
        ItemStack questLog = this.createQuestLogItem();
        ItemStack calendarAndEvents = this.createCalendarItem();
        ItemStack enderChest = this.createEnderChestItem();
        ItemStack pets = this.createPetsItem(skyblockPlayer);
        ItemStack craftingTable = this.createCraftingTableItem();
        ItemStack activeEffects = this.createActiveEffectsItem(skyblockPlayer);
        ItemStack settings = this.createSettings(skyblockPlayer);
        ItemStack bank;

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
        if ((int) skyblockPlayer.getValue("bank.personal.cooldown") != -1) {
            bank = this.createPersonalBank(skyblockPlayer);
            inventory.setItem(33, bank);
        }

        inventory.setItem(47, this.createWarpItem(skyblockPlayer));
        inventory.setItem(49, Util.buildCloseButton());
        inventory.setItem(50, settings);

        for (Bag bag : plugin.getBagManager().getBags().values()) {
            if ((boolean) skyblockPlayer.getValue("bag." + bag.getId() + ".unlocked")) {
                inventory.setItem(bag.getSkyblockMenuSlot(), bag.toItemStack());
            }
        }

        player.openInventory(inventory);
    }

    public static ItemStack createSkyblockProfileItem(SkyblockPlayer skyblockPlayer, boolean selfViewing) {
        ItemBuilder builder = new ItemBuilder(
                ChatColor.GREEN + (selfViewing ? "Your" : skyblockPlayer.getBukkitPlayer().getName() + (skyblockPlayer.getBukkitPlayer().getName().endsWith("s") ? "'" : "'s")) + " SkyBlock Profile",
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
                        ChatColor.GOLD + " ⸕ Mining Speed " + ChatColor.WHITE + skyblockPlayer.getStat(SkyblockStat.MINING_SPEED)
                );

        if (selfViewing) builder.addLore("", ChatColor.YELLOW + "Click to view!");

        return builder.toItemStack();
    }

    public ItemStack createSkillsItem(SkyblockPlayer player) {
        return new ItemBuilder(
                ChatColor.GREEN + "Your Skills",
                Material.DIAMOND_SWORD)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "View your Skill progression and",
                        ChatColor.GRAY + "rewards.", "",
                        ChatColor.GOLD + "" + player.getSkillAverage() + " Skill Avg." + ChatColor.DARK_GRAY + " (non-cosmetic)",
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

    public static ItemStack createRecipeBookItem(SkyblockPlayer skyblockPlayer) {
        int unlocked = ((List<String>) skyblockPlayer.getValue("recipes.unlocked")).size();
        int total = CraftingGUI.needsUnlocking.size();

        double percent = Math.round((double) unlocked / (double) total * 1000) / 10.0;

        int barLength = 20;
        int barPercentPerBlock = 100 / barLength;
        int barFilled = (int) Math.round(percent / barPercentPerBlock);
        int barEmpty = barLength - barFilled;

        String bar = ChatColor.DARK_GREEN + StringUtils.repeat("-", barFilled) + ChatColor.WHITE + StringUtils.repeat("-", barEmpty);

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
                        ChatColor.GRAY + "Recipe Book Unlocked: " + ChatColor.YELLOW + percent + ChatColor.GOLD + "%",
                        bar + ChatColor.YELLOW + " " + unlocked + ChatColor.GOLD + "/" + ChatColor.YELLOW + total,
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
                        ChatColor.GRAY + "Next Event: " + Skyblock.getPlugin().getTimeManager().getNextEvent().getDisplayName(),
                        ChatColor.GRAY + "Starting in: " + ChatColor.YELLOW + Skyblock.getPlugin().getTimeManager().getNextEvent().getIn(),
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

    public ItemStack createSettings(SkyblockPlayer player) {
        return new ItemBuilder(
                ChatColor.GREEN + "Settings",
                Material.REDSTONE_TORCH_ON)
                .addLore(
                        ChatColor.GRAY + "View and edit your Skyblock",
                        ChatColor.GRAY + "settings.",
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack();
    }

    public ItemStack createActiveEffectsItem(SkyblockPlayer player) {
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
                        ChatColor.GRAY + "Currently Active: " + ChatColor.YELLOW + "" + player.getActiveEffects().size(),
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .toItemStack();
    }

    public ItemStack createPersonalBank(SkyblockPlayer player) {
        String status;
        int cooldown = (int) player.getValue("bank.personal.cooldown");
        if (!((boolean) player.hasExtraData("personalBankLastUsed"))) {
            player.setExtraData("personalBankLastUsed", 0L);
        }

        if ((long) player.getExtraData("personalBankLastUsed") < System.currentTimeMillis() - cooldown * 60000) {
            status = ChatColor.GREEN + "Available";
        } else {
            status = ChatColor.RED + "Unavailable";
        }

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());

        return new ItemBuilder(Util.idToSkull(skull.clone(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM2ZTk0ZjZjMzRhMzU0NjVmY2U0YTkwZjJlMjU5NzYzODllYjk3MDlhMTIyNzM1NzRmZjcwZmQ0ZGFhNjg1MiJ9fX0="))
                .setDisplayName(ChatColor.GREEN + "Personal Bank")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addLore(
                        ChatColor.GRAY + "Contact your banker from",
                        ChatColor.GRAY + "anywhere.",
                        ChatColor.GRAY + "Cooldown: " + ChatColor.YELLOW + cooldown + " minutes",
                        "",
                        ChatColor.GRAY + "Banker Status:",
                        status,
                        "",
                        ChatColor.YELLOW + "Click to open!"
                )
                .toItemStack();
    }

    public ItemStack createWarpItem(SkyblockPlayer player) {
        String name;
        String command;
        String description;

        String head;
        String signature;

        if (player.isNotOnPrivateIsland()) {
            name = "Private Island";
            command = "warp home";
            description = "Your very own chunk of SkyBlock.\nNice housing for your minions.";

            head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzljODg4MWU0MjkxNWE5ZDI5YmI2MWExNmZiMjZkMDU5OTEzMjA0ZDI2NWRmNWI0MzliM2Q3OTJhY2Q1NiJ9fX0=";
            signature = null;
        } else {
            name = "SkyBlock Hub";
            command = "warp hub";
            description = "Where everything happens and\nanything is possible.";

            head = "eyJ0aW1lc3RhbXAiOjE1ODcyNDE1OTEyNzYsInByb2ZpbGVJZCI6IjRkNzA0ODZmNTA5MjRkMzM4NmJiZmM5YzEyYmFiNGFlIiwicHJvZmlsZU5hbWUiOiJzaXJGYWJpb3pzY2hlIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82ODY3MThkODVlMjViMDA2ZjJjOGYxNjBmNjE5YjIzYzhmZDZhZTc1ZGRmMWMwNjMwOGVjMGY1MzlkOTMxNzAzIn19fQ==";
            signature = "qSJBND+tmy4O/+m/N21yHl6kWbuUIsjgugLgELLhpoqc55cd6DAGl4seGV9qE7tFCrNU1MgiSIeGE/mgqdzcGLyVsXsywJxiJzaSLra63I2EdsITvA9gMZVvcMlkPOmYOCF37d0hfOUW5eIxL9sq52B4yPws4k5Mfcd4PPD3NbAoA8exH9bRqH+hx7+pbhjCdkxIxwFEHfsp7t/DGzhbUJFW3ulEUHJHddXGE1JuqYxGsk9UhmQu7sA4bLOQuHisZj0CYXlsDXIopVVSEN7nnajEvCE4e2yoW1kHUfOsADQGkD0kBBw5A+VHz15dKFaLjmyGz0GrKTPNlXcrGPiHbCVU+WxXGAljfIXLtUiKPJksAmQTlIt6bGnrZ/oDWbp7WXizo4qogD5TTHz2ZBQ+wPf1h7BTjv1tVWjVhjEpbj2AXveUHL6CVYEv0Eb4GCXRpJO83z5sGGhyVDRnRZYLIUBapJvpDCpGYkQAiW+go04s4R5/RKpHJ9kxnhILXpxY/3NBz8rPy7NNvVBAQJOiXVX9IJbZkoxQwSjDO+VdD0cEb/Ov5vHtTkzhQuVTOUwP0DgdluPB6jq6Ui3nMSi1PbBESGCwiU6xMXk0E96saxF5NAET+n06yU7Si2Jju5mQX8cphG8jIm4pLxYaYulOqyHLOYAEjZdSd3f26FAWtVc=";
        }

        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        if (signature != null) profile.getProperties().put("textures", new Property("textures", head, signature));
        else profile.getProperties().put("textures", new Property("textures", head));

        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        meta.setDisplayName(ChatColor.AQUA + name);
        meta.setLore(
                Arrays.asList(Util.buildLore(
                        "&8/" + command + "\n\n" + description + "\n\n&eClick to warp!",
                        '7'))
        );

        stack.setItemMeta(meta);

        NBTItem nbt = new NBTItem(stack);
        nbt.setString("skyblock.warp.destination.command", command);

        return nbt.getItem();
    }

}
