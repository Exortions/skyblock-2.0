package com.skyblock.skyblock.commands.menu;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.collections.CollectionCategory;
import com.skyblock.skyblock.features.collections.gui.CollectionRewardGUI;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiresPlayer
@Usage(usage = "/sb collection")
@Description(description = "Open the collection menu")
public class CollectionCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        Inventory inventory;

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (args.length == 0) {
            inventory = Bukkit.createInventory(null, 54, "Collection");

            Util.fillEmpty(inventory);

            AtomicInteger collectionUnlocked = new AtomicInteger();
            AtomicInteger collectionTotal = new AtomicInteger();

            Collection.getCollections().forEach(col -> {
                if (skyblockPlayer.getValue("collection." + col.getName().toLowerCase() + ".unlocked").equals(true)) collectionUnlocked.getAndIncrement();

                collectionTotal.getAndIncrement();
            });

            double collectionPercentage = Math.round((double) collectionUnlocked.get() / (double) collectionTotal.get() * 1000) / 10.0;

            int collectionPercentageBar = (int) Math.round(collectionPercentage / 5);
            int collectionPercentageBarEmpty = 20 - collectionPercentageBar;

            String bar = "&2" + StringUtils.repeat("-", collectionPercentageBar) + "&7" + StringUtils.repeat("-", collectionPercentageBarEmpty);

            inventory.setItem(4, new ItemBuilder(ChatColor.GREEN + "Collection", Material.ITEM_FRAME).addLore(Util.buildLore("&7View all of the items available\n&7in SkyBlock. Collect more of an\n&7item to unlock rewards on your\n&7way to becoming the master of\n&7SkyBlock!\n\n&7Collection Unlocked: &e" + collectionPercentage + "&6%\n" + bar + " &e" + collectionUnlocked + "&6/&e" + collectionTotal + "\n\n&eClick to show rankings!")).toItemStack());

            inventory.setItem(20, generateCollectionCategory(skyblockPlayer, Collection.getCollectionCategories().get(0), true));
            inventory.setItem(21, generateCollectionCategory(skyblockPlayer, Collection.getCollectionCategories().get(1), true));
            inventory.setItem(22, generateCollectionCategory(skyblockPlayer, Collection.getCollectionCategories().get(2), true));
            inventory.setItem(23, generateCollectionCategory(skyblockPlayer, Collection.getCollectionCategories().get(3), true));
            inventory.setItem(24, generateCollectionCategory(skyblockPlayer, Collection.getCollectionCategories().get(4), true));

            inventory.setItem(48, Util.buildBackButton());
            inventory.setItem(49, Util.buildCloseButton());

            player.openInventory(inventory);

            return;
        }

        String inv = args[0];

        if (Collection.getCollectionCategories().stream().anyMatch(cat -> cat.getName().equalsIgnoreCase(inv))) {
            CollectionCategory category = Collection.getCollectionCategories().stream().filter(cat -> cat.getName().equalsIgnoreCase(inv)).findFirst().get();

            List<Collection> collections = Collection.getCollections().stream().filter(col -> col.getCategory().equals(category.getName())).collect(Collectors.toList());

            inventory = Bukkit.createInventory(null, 54, category.getName() + " Collection");

            Util.fillBorder(inventory);

            inventory.setItem(4, generateCollectionCategory(skyblockPlayer, category, false));

            for (Collection collection : collections) {
                inventory.addItem(generateCollectionItem(skyblockPlayer, collection, false));
            }

            inventory.setItem(48, Util.buildBackButton());
            inventory.setItem(49, Util.buildCloseButton());

            player.openInventory(inventory);
        } else if (Collection.getCollections().stream().anyMatch(col -> col.getName().replace(" ", "_").equalsIgnoreCase(inv))) {
            Collection collection = Collection.getCollections().stream().filter(col -> col.getName().replace(" ", "_").equalsIgnoreCase(inv)).findFirst().get();

            Gui gui = new Gui(collection.getName() + " Collection", 54, new HashMap<>());

            Util.fillEmpty(gui);

            ItemStack collectionItem = generateCollectionItem(skyblockPlayer, collection, true);

            gui.addItem(4, collectionItem);

            List<ItemStack> levelPanes = new ArrayList<>();

            for (int i = 0; i < collection.getMaxLevel(); i++) {
                levelPanes.add(generateCollectionLevel(skyblockPlayer, collection, i));
            }

            for (int i = 0; i < levelPanes.size(); i++) {
                ItemStack pane = levelPanes.get(i);

                gui.addItem(18 + i, pane);

                gui.getClickEvents().put(pane.getItemMeta().getDisplayName(), () -> {
                    if (isRecipe(pane)) {
                        new CollectionRewardGUI(pane, collectionItem, gui, player).show(player);
                    }
                });
            }

            gui.addItem(48, Util.buildBackButton());
            gui.addItem(49, Util.buildCloseButton());

            gui.show(player);
        } else {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid collection category or collection");
        }

    }

    private boolean isRecipe(ItemStack item) {
        List<String> lore = item.getItemMeta().getLore();

        for (String s : lore) {
            if (s.endsWith("Recipe")) return true;
        }

        return false;
    }

    private ItemStack generateCollectionLevel(SkyblockPlayer skyblockPlayer, Collection collection, int i) {
        int currentLevel = 0;
        int currentExp = 0;
        int requiredExp = 0;

        if (skyblockPlayer.getValue("collection." + collection.getName().toLowerCase() + ".level") != null) {
            currentLevel = (int) skyblockPlayer.getValue("collection." + collection.getName().toLowerCase() + ".level") + 1;
            currentExp = (int) skyblockPlayer.getValue("collection." + collection.getName().toLowerCase() + ".exp");
            requiredExp = collection.getLevelToExp().get(i);
        }

        ChatColor color;
        int stainedGlassPaneColor;

        if (currentLevel == i + 1) {
            color = ChatColor.YELLOW;
            stainedGlassPaneColor = 4;
        } else if (currentLevel > i + 1) {
            color = ChatColor.GREEN;
            stainedGlassPaneColor = 5;
        } else {
            color = ChatColor.RED;
            stainedGlassPaneColor = 14;
        }

        ItemBuilder builder = new ItemBuilder(color + collection.getName() + " " + Util.toRoman(i + 1), Material.STAINED_GLASS_PANE, (short) stainedGlassPaneColor);

        builder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        double percentage = Math.round((double) currentExp / (double) requiredExp * 1000) / 10.0;

        double barMax = 20;
        double barCharPerPercent = 5;

        int barFilled = (int) Math.round(percentage / barCharPerPercent);

        int barEmpty = (int) (barMax - barFilled);

        if (barFilled > barMax) barFilled = (int) barMax;

        if (percentage > 100) percentage = 100;

        String bar = "&2" + StringUtils.repeat("-", barFilled) + "&7" + StringUtils.repeat("-", barEmpty);

        List<String> rewards = collection.getRewards().stringify(i + 1);

        if (rewards.size() == 0) rewards.add("  &cComing soon");

        String rewardsString = String.join("\n", rewards);

        builder.addLore(
                Util.buildLore("\n&7Progress: &e" + percentage + "&6%\n" + bar + " &e" + Util.formatInt(currentExp) + "&6/&e" + Util.abbreviate(requiredExp) + "\n\n&7Rewards:\n" + rewardsString + "\n\n&eClick to view rewards!")
        );

        builder.setAmount(i + 1);

        return builder.toItemStack();
    }

    private ItemStack generateCollectionItem(SkyblockPlayer skyblockPlayer, Collection collection, boolean inItemMenu) {
        if (skyblockPlayer.getValue("collection." + collection.getName().toLowerCase() + ".unlocked").equals(false)) return new ItemBuilder(ChatColor.RED + collection.getName(), Material.INK_SACK, (short) 8).addLore(Util.buildLore("&7Find this item to add it to your\n&7collection and unlock collection\n&7rewards!")).toItemStack();

        ItemBuilder builder = new ItemBuilder(ChatColor.YELLOW + collection.getName(), collection.getMaterial(), collection.getData());

        builder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        int currentLevel = 0;
        int currentExp = 0;
        int nextLevel = currentLevel + 1;
        int requiredExp = 0;
        boolean maxed = true;

        if (skyblockPlayer.getValue("collection." + collection.getName().toLowerCase() + ".level") != null) {
            currentLevel = (int) skyblockPlayer.getValue("collection." + collection.getName().toLowerCase() + ".level");
            currentExp = (int) skyblockPlayer.getValue("collection." + collection.getName().toLowerCase() + ".exp");
            nextLevel = currentLevel + 1;
            requiredExp = Integer.MAX_VALUE;

            if (collection.getLevelToExp().containsKey(currentLevel)){
                requiredExp = collection.getLevelToExp().get(currentLevel);
                maxed = false;
            }
        }

        String nameSuffix = currentLevel == 0 ? "" : " " + Util.toRoman(currentLevel);

        builder.setDisplayName(ChatColor.YELLOW + collection.getName() + nameSuffix);

        double percentage = Math.round((double) currentExp / (double) requiredExp * 1000) / 10.0;

        double barMax = 20;
        double barCharPerPercent = 5;

        int barFilled = (int) Math.round(percentage / barCharPerPercent);

        int barEmpty = (int) (barMax - barFilled);

        String bar = "&2" + StringUtils.repeat("-", barFilled) + "&7" + StringUtils.repeat("-", barEmpty);

        builder.addLore(
                Util.buildLore("&7View all of your " + collection.getName() + " Collection\n&7progress and rewards!")
        );

        if (!inItemMenu && !maxed) {
            builder.addLore(
                    Util.buildLore("\n&7Progress to " + collection.getName() + " " + Util.toRoman(nextLevel) + ": &e" + percentage + "&6%\n" + bar + " &e" + Util.formatInt(currentExp) + "&6/&e" + Util.abbreviate(requiredExp) +
                            "\n\n&7Co-op Contributions:\n&b" + skyblockPlayer.getBukkitPlayer().getDisplayName() + "&7: &e" + Util.abbreviate(currentExp) +
                            "\n\n&7" + collection.getName() + " " + Util.toRoman(nextLevel) + " Rewards:\n" + String.join("\n", collection.getRewards().stringify(nextLevel)) + "\n\n&eClick to view!")
            );
        } else {
            builder.setDisplayName(ChatColor.YELLOW + collection.getName() + " Collection");

            builder.addLore(
                    Util.buildLore("\n&7Total Collected: &e" + currentExp)
            );
        }

        return builder.toItemStack();
    }

    public ItemStack generateCollectionCategory(SkyblockPlayer player, CollectionCategory category, boolean clickToView) {
        ItemBuilder builder = new ItemBuilder(ChatColor.GREEN + category.getName() + " Collection", category.getIcon()).setDamage(category.getData());

        builder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        List<Collection> collections = Collection.getCollections().stream().filter(col -> col.getCategory().equals(category.getName())).collect(Collectors.toList());

        int unlocked = 0;
        int total = collections.size();

        for (Collection collection : collections) {
            if (player.getValue("collection." + collection.getName().toLowerCase() + ".unlocked").equals(true)) unlocked++;
        }

        double percentUnlocked = Math.round((double) unlocked / (double) total * 1000) / 10.0;

        double barMax = 20;
        double barCharPerPercent = 5;

        int barFilled = (int) Math.round(percentUnlocked / barCharPerPercent);

        int barEmpty = (int) (barMax - barFilled);

        String bar = "&2" + StringUtils.repeat("-", barFilled) + "&7" + StringUtils.repeat("-", barEmpty);

        builder.addLore(
                Util.buildLore("&7View your " + category.getName() + " collection!\n\n&7Collection Unlocked: &e" + percentUnlocked + "&6%\n" + bar + " &e" + unlocked + "&6/&e" + total)
        );

        if (clickToView) builder.addLore(Util.buildLore("\n&eClick to view!"));

        return builder.toItemStack();
    }

}
