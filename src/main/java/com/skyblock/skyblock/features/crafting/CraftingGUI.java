package com.skyblock.skyblock.features.crafting;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class CraftingGUI extends CraftInventoryCustom implements Listener {

    private static final ItemStack recipeRequired = new ItemBuilder(ChatColor.RED + "Recipe Required", Material.BARRIER).toItemStack();

    private final List<Integer> slots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30);
    private final BukkitRunnable task;
    private List<Integer> excess;

    public CraftingGUI(Player opener) {
        super(null, 54, "Crafting Table");

        Util.fillEmpty(this);
        updateStatus(false);

        for (int i : slots) setItem(i, null);

        setItem(23, recipeRequired);

        setItem(49, Util.buildBackButton());

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (opener.isOnline()) tick(); else cancel();
            }
        };

        task.runTaskTimer(Skyblock.getPlugin(Skyblock.class), 5L, 1);

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin(Skyblock.class));
    }

    private void tick() {
        RecipeHandler handler = Skyblock.getPlugin(Skyblock.class).getRecipeHandler();
        StringBuilder string = new StringBuilder("[");
        StringBuilder ignoreAmount = new StringBuilder("[");

        setItem(23, recipeRequired);
        updateStatus(false);

        for (int i : slots) {
            ItemStack item = getItem(i);

            if (item == null) {
                string.append("AIR");
                ignoreAmount.append("AIR");
            } else {
                NBTItem nbtItem = new NBTItem(item);

                if (nbtItem.getBoolean("skyblockItem")) {
                    ItemStack one = new ItemStack(item);
                    one.setAmount(1);

                    string.append(Skyblock.getPlugin(Skyblock.class).getItemHandler().getReversed().get(one)).append(":").append(item.getAmount()).append(":0");
                    ignoreAmount.append(Skyblock.getPlugin(Skyblock.class).getItemHandler().getReversed().get(one)).append(":0");
                } else {
                    string.append(item.getType().name()).append(":").append(item.getAmount()).append(":").append(item.getDurability());
                    ignoreAmount.append(item.getType().name()).append(":").append(item.getDurability());
                }
            }

            if (i == 30) continue;

            string.append(", ");
            ignoreAmount.append(", ");
        }

        string.append("]");
        ignoreAmount.append("]");

        String shapeless = string.toString().replaceAll(", AIR", "").replaceAll("AIR", "");

        handler.getRecipes().forEach(recipe -> {
            if (recipe.toShapeless().equals(shapeless)) {
                setItem(23, recipe.getResult());
                updateStatus(true);
            }

            if (recipe.toString(true).equals(ignoreAmount.toString())) {
                excess = recipe.getExcess(string.toString());

                if (excess.isEmpty()) return;

                setItem(23, recipe.getResult());
                updateStatus(true);
            }
        });

        setItem(49, Util.buildBackButton());
    }

    private void updateStatus(boolean status) {
        for (int i = 45; i < 54; i++) {
            ItemStack item = new ItemBuilder(" ", Material.STAINED_GLASS_PANE, 1, (short) (status ? 7 : 14)).toItemStack();
            setItem(i, item);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getClickedInventory().equals(this)) return;

        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Go Back")) {
            ((Player) event.getWhoClicked()).performCommand("sb menu");

            return;
        }

        if (slots.contains(event.getSlot())) return;

        if (event.getSlot() != 23 || event.getCurrentItem().getType().equals(Material.BARRIER)) return;

        event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
        setItem(23, recipeRequired);

        for (int i = 0; i < slots.size(); i++) {
            if (excess != null && excess.get(i) == 0 && getItem(slots.get(i)) == null) setItem(slots.get(i), null);
            else {
                ItemStack item = getItem(slots.get(i));
                item.setAmount(excess.get(i));
                setItem(slots.get(i), item);
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(this)) return;

        task.cancel();
        HandlerList.unregisterAll(this);
    }
}
