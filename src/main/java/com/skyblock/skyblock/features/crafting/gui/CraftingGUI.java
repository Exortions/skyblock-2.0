package com.skyblock.skyblock.features.crafting.gui;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerCraftEvent;
import com.skyblock.skyblock.features.crafting.RecipeHandler;
import com.skyblock.skyblock.features.crafting.SkyblockRecipe;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CraftingGUI extends CraftInventoryCustom implements Listener {

    private static final ItemStack recipeRequired = new ItemBuilder(ChatColor.RED + "Recipe Required", Material.BARRIER).toItemStack();
    public static final List<String> needsUnlocking = new ArrayList<>();
    private final List<Integer> slots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30);
    private final BukkitRunnable task;
    private Player opener;
    private SkyblockRecipe recipe;
    private List<Integer> excess;

    public CraftingGUI(Player opener) {
        super(null, 54, "Crafting Table");

        this.opener = opener;

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

        task.runTaskTimer(Skyblock.getPlugin(), 5L, 1);

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin());
    }

    private void tick() {
        RecipeHandler handler = Skyblock.getPlugin().getRecipeHandler();
        StringBuilder string = new StringBuilder("[");
        StringBuilder ignoreAmount = new StringBuilder("[");

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(opener);

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

                    string.append(nbtItem.getString("skyblockId").toUpperCase()).append(":").append(item.getAmount()).append(":0");
                    ignoreAmount.append(nbtItem.getString("skyblockId").toUpperCase()).append(":0");
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

        List<String> unlocked = (List<String>) skyblockPlayer.getValue("recipes.unlocked");

        handler.getRecipes().forEach(recipe -> {
            if (!unlocked.contains(Util.getSkyblockId(recipe.getResult()).toUpperCase()) && needsUnlocking.contains(Util.getSkyblockId(recipe.getResult()).toUpperCase())) return;

            if (recipe.toShapeless().equals(shapeless)) {
                setItem(23, recipe.getResult());
                updateStatus(true);

                this.recipe = recipe;
                return;
            }

            if (recipe.toString(true).equals(ignoreAmount.toString())) {
                excess = recipe.getExcess(string.toString());

                if (excess.isEmpty()) return;

                setItem(23, recipe.getResult());
                updateStatus(true);

                this.recipe = recipe;
            }
        });

        setItem(49, Util.buildBackButton());
    }

    private void updateStatus(boolean status) {
        for (int i = 45; i < 54; i++) {
            ItemStack item = new ItemBuilder(" ", Material.STAINED_GLASS_PANE, 1, (short) (status ? 5 : 14)).toItemStack();
            setItem(i, item);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(this)) return;
        if (!Util.notNull(event.getCurrentItem())) return;

        event.setCancelled(true);

        if (event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Go Back")) {
            ((Player) event.getWhoClicked()).performCommand("sb menu");

            return;
        }

        if (slots.contains(event.getSlot())) {
            event.setCancelled(false);
            return;
        }

        if (event.getSlot() != 23 || event.getCurrentItem().getType().equals(Material.BARRIER)) return;

        event.getWhoClicked().getInventory().addItem(Skyblock.getPlugin().getItemHandler().getItem(event.getCurrentItem()));
        setItem(23, recipeRequired);

        Bukkit.getPluginManager().callEvent(new SkyblockPlayerCraftEvent(recipe, (Player) event.getWhoClicked()));

        for (int i = 0; i < slots.size(); i++) {
            if (excess != null && excess.get(i) == 0 && getItem(slots.get(i)) == null) {
                setItem(slots.get(i), null);
            } else {
                int ex = (excess != null ? excess.get(i) : 0);

                ItemStack item = getItem(slots.get(i));
                item.setAmount(ex);
                setItem(slots.get(i), item);
            }
        }

        tick();

        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(this)) return;

        for (int slot : slots) {
            ItemStack item = getItem(slot);

            if (item != null) {
                event.getPlayer().getInventory().addItem(item);
            }
        }

        task.cancel();
        HandlerList.unregisterAll(this);
    }
}
