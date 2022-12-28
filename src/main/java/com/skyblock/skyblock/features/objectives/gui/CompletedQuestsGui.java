package com.skyblock.skyblock.features.objectives.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class CompletedQuestsGui extends Gui {
    public CompletedQuestsGui(Player player) {
        super("Quest Log (Completed)", 54, new HashMap<>());

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        List<String> compQuests = (List<String>) skyblockPlayer.getValue("quests.completedQuests");

        Util.fillBorder(this);

        addItem(4, new ItemBuilder(ChatColor.GREEN + "Quest Log (Completed)", Material.BOOK_AND_QUILL).addLore("&7View your active quests,", "&7progress, and rewards.").toItemStack());
        addItem(49, Util.buildCloseButton());
        addItem(45, Util.buildBackButton());

        addItem(50, new ItemBuilder(ChatColor.GREEN + "Ongoing Quests", Material.BOOK).addLore("&7&7View quests you are currently", "&7working towards", " ", ChatColor.YELLOW + "Click to view!").toItemStack());

        clickEvents.put(ChatColor.GREEN + "Ongoing Quests", () -> new QuestLogGui(player).show(player));

        clickEvents.put(ChatColor.GREEN + "Go Back", () -> player.performCommand("sb menu skyblock_menu"));

        int i = 10;
        for (QuestLine quest : Skyblock.getPlugin().getQuestLineHandler().getQuests()) {
            if (!compQuests.contains(quest.getName())) continue;

            while (getItems().containsKey(i)) {
                i++;

                if (i == 54) break;
            }

            ItemBuilder item = new ItemBuilder(ChatColor.GREEN + quest.getDisplay(), Material.PAPER);
            item.addEnchantmentGlint();
            item.addLore(" ");

            for (Objective objective : quest.getLine()) {
                item.addLore(ChatColor.GREEN + " ✔ " + ChatColor.WHITE + objective.getDisplay() + " " + objective.getSuffix(skyblockPlayer));
            }

            item.addLore(" ");

            addItem(i, item.toItemStack());
        }
    }
}
