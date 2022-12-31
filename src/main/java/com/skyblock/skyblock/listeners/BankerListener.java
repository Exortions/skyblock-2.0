package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.sign.SignGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankerListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))
            return;

        List<String> titles = new ArrayList<>(Arrays.asList("Personal Bank Account", "Bank Deposit", "Bank Withdrawal"));

        if (!titles.contains(event.getClickedInventory().getTitle())) return;

        event.setCancelled(true);

        ItemStack stack = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (stack.getType().equals(Material.BARRIER)) {
            player.closeInventory();
            return;
        } else if (stack.getType().equals(Material.ARROW)) {
            player.performCommand("sb banker");
            return;
        }

        if (event.getClickedInventory().getTitle().equals("Personal Bank Account")) {
            if (stack.getType().equals(Material.CHEST)) player.performCommand("sb banker deposit");
            else if (stack.getType().equals(Material.DROPPER)) player.performCommand("sb banker withdraw");
        } else if (event.getClickedInventory().getTitle().equals("Bank Deposit")) {
            if (stack.getType().equals(Material.CHEST)) {
                if (stack.getAmount() == 64) player.performCommand("sb deposit all");
                else if (stack.getAmount() == 32) player.performCommand("sb deposit half");

                player.performCommand("sb banker deposit");
                player.performCommand("sb banker");
            } else if (stack.getType().equals(Material.SIGN)) {
                SignGui sign = new SignGui(Skyblock.getPlugin().getSignManager(), e -> new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            int amount = Integer.parseInt(e.getLines()[0]);
                            player.performCommand("sb deposit " + amount);
                            player.performCommand("sb banker");
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }.runTask(Skyblock.getPlugin()));

                sign.open(player);
            }
        } else if (event.getClickedInventory().getTitle().equals("Bank Withdrawal")) {
            if (stack.getType().equals(Material.DROPPER)) {
                if (stack.getAmount() == 64) player.performCommand("sb withdraw all");
                else if (stack.getAmount() == 32) player.performCommand("sb withdraw half");
                else if (stack.getAmount() == 1) player.performCommand("sb withdraw 20%");

                player.performCommand("sb banker withdraw");
                player.performCommand("sb banker");
            } else if (stack.getType().equals(Material.SIGN)) {
                SignGui sign = new SignGui(Skyblock.getPlugin().getSignManager(), e -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                int amount = Integer.parseInt(e.getLines()[0]);
                                player.performCommand("sb withdraw " + amount);
                                player.performCommand("sb banker");
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }.runTask(Skyblock.getPlugin());
                });

                sign.open(player);
            }
        }
    }
}
