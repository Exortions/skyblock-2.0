package com.skyblock.skyblock.features.objectives.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import net.citizensnpcs.api.util.SpigotUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class QuestLogGui extends Gui {

    public QuestLogGui(Player player) {
        super("Quest Log", 54, new HashMap<>());

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        int fairySouls = ((List<Location>) skyblockPlayer.getValue("fairySouls.found")).size();
        List<String> compQuests = (List<String>) skyblockPlayer.getValue("quests.completedQuests");
        List<String> compObj = (List<String>) skyblockPlayer.getValue("quests.completedObjectives");

        Util.fillBorder(this);

        addItem(4, new ItemBuilder(ChatColor.GREEN + "Quest Log", Material.BOOK_AND_QUILL).addLore("&7View your active quests,", "&7progress, and rewards.").toItemStack());
        addItem(10, Util.idToSkull(new ItemBuilder(ChatColor.YELLOW + "Find all Fairy Souls", Material.SKULL_ITEM, (byte) SkullType.PLAYER.ordinal()).addLore(" ", (fairySouls >= 195 ? ChatColor.GREEN + "✔" : ChatColor.RED + "✖") + ChatColor.YELLOW + " Found: " + ChatColor.LIGHT_PURPLE + fairySouls + "&7/" + ChatColor.LIGHT_PURPLE + 195, " ", "&7Forever ongoing quest...", " ", ChatColor.YELLOW + "Click to view details!").toItemStack(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2OTIzYWQyNDczMTAwMDdmNmFlNWQzMjZkODQ3YWQ1Mzg2NGNmMTZjMzU2NWExODFkYzhlNmIyMGJlMjM4NyJ9fX0="));
        addItem(49, Util.buildCloseButton());
        addItem(45, Util.buildBackButton());

        addItem(50, new ItemBuilder(ChatColor.GREEN + "Completed Quests", Material.BOOK).addLore("&7Take a peek at the past", "&7and browse quests you've", "&7already completed.", " ", "&7Completed: " + ChatColor.GREEN + compQuests.size(), " ", ChatColor.YELLOW + "Click to view!").toItemStack());

        clickEvents.put(ChatColor.GREEN + "Completed Quests", () -> new CompletedQuestsGui(player).show(player));

        clickEvents.put(ChatColor.GREEN + "Go Back", () -> player.performCommand("sb menu skyblock_menu"));

        int i = 11;
        for (QuestLine quest : Skyblock.getPlugin().getQuestLineHandler().getQuests()) {
            if (compQuests.contains(quest.getName())) continue;

            while (getItems().containsKey(i)) {
                i++;

                if (i == 54) break;
            }

            ItemBuilder item = new ItemBuilder(ChatColor.YELLOW + quest.getDisplay(), Material.PAPER);
            item.addEnchantmentGlint();
            item.addLore(" ");

            for (Objective objective : quest.getLine()) {
                if (compObj.contains(objective.getId())) {
                    item.addLore(ChatColor.GREEN + " ✔ " + ChatColor.WHITE + objective.getDisplay() + " " + objective.getSuffix(skyblockPlayer));
                } else {
                    item.addLore(ChatColor.RED + " ✖ " + ChatColor.YELLOW + objective.getDisplay() + " " + objective.getSuffix(skyblockPlayer));
                }
            }

            item.addLore(" ");

            addItem(i, item.toItemStack());
        }
    }
}
