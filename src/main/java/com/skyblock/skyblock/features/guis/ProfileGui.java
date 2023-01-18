package com.skyblock.skyblock.features.guis;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.commands.menu.SkyblockMenuCommand;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ProfileGui extends Gui {

    public ProfileGui(Player opener, Player targetPlayer) {
        super(targetPlayer.getName() + (targetPlayer.getName().endsWith("s") ? "'" : "'s") + " Profile", 54, new HashMap<String, Runnable>() {{
            put(ChatColor.RED + "Close", opener::closeInventory);
            put(ChatColor.GREEN + "Visit Island", () -> opener.performCommand("visit " + targetPlayer.getName()));
            put(ChatColor.GREEN + "Trade Request", () -> {
                // TODO: make trades
            });
            put(ChatColor.GREEN + "Invite to Island", () -> {
                // TODO: make coop
            });
            put(ChatColor.GREEN + "Co-op Request", () -> {
                // TODO: make coop
            });
            put(ChatColor.GREEN + "Personal Vault", () -> opener.openInventory(targetPlayer.getEnderChest()));
        }});

        Util.fillEmpty(this);

        SkyblockPlayer target = SkyblockPlayer.getPlayer(targetPlayer);

        ItemStack emptySlot = new ItemBuilder(ChatColor.RED + "Empty Slot!", Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getData()).addLore(Util.buildLore("&cThis slot is currently\n&cempty!")).toItemStack();
        ItemStack profile = SkyblockMenuCommand.createSkyblockProfileItem(target, false);

        addItem(22, profile);

        addItem(1, targetPlayer.getItemInHand().getType().equals(Material.AIR) ? emptySlot : targetPlayer.getItemInHand());
        addItem(10, targetPlayer.getInventory().getHelmet() == null ? emptySlot : targetPlayer.getInventory().getHelmet());
        addItem(19, targetPlayer.getInventory().getChestplate() == null ? emptySlot : targetPlayer.getInventory().getChestplate());
        addItem(28, targetPlayer.getInventory().getLeggings() == null ? emptySlot : targetPlayer.getInventory().getLeggings());
        addItem(37, targetPlayer.getInventory().getBoots() == null ? emptySlot : targetPlayer.getInventory().getBoots());
        addItem(46, target.getPet() == null ? emptySlot : target.getPet().toItemStack());

        addItem(15, new ItemBuilder(ChatColor.GREEN + "Visit Island", Material.FEATHER).addLore(Util.buildLore("&eClick to visit!")).toItemStack());
        addItem(16, new ItemBuilder(ChatColor.GREEN + "Trade Request", Material.EMERALD).addLore(Util.buildLore("&eSend a trade request!")).toItemStack());
        addItem(24, new ItemBuilder(ChatColor.GREEN + "Invite to Island", Material.RED_ROSE).addLore(Util.buildLore("&eClick to invite!")).toItemStack());
        addItem(25, new ItemBuilder(ChatColor.GREEN + "Co-op Request", Material.DIAMOND).addLore(Util.buildLore("&eSend a co-op request!")).toItemStack());
        addItem(33, new ItemBuilder(ChatColor.GREEN + "Personal Vault", Material.ENDER_CHEST).addLore(Util.buildLore("&eClick to view!")).toItemStack());

        addItem(49, Util.buildCloseButton());
    }

}
