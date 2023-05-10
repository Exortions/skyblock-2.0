package com.skyblock.skyblock.features.merchants;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Data;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class Merchant implements Listener {

    private final String name;

    private final String skinSignature;
    private final String skinValue;

    private int interactionIteration = 0;

    private final List<String> initialDialogue;

    private NPC npc;
    private ArmorStand stand;
    private ArmorStand click;

    private final List<MerchantItem> items;
    private final Location location;

    private final boolean villager;
    private final Villager.Profession profession;

    protected Merchant(String name, String skinValue, String skinSignature, List<MerchantItem> items, Location location, List<String> initialDialogue, boolean villager, Villager.Profession profession) {
        this.name = name;

        this.skinSignature = skinSignature;
        this.skinValue = skinValue;

        this.items = items;

        this.location = location;

        this.initialDialogue = initialDialogue;

        this.villager = villager;
        this.profession = profession;
    }

    private ArrayList<ItemStack> getSold(SkyblockPlayer player) {
        if (!player.hasExtraData("merchantSold")) {
            player.setExtraData("merchantSold", new ArrayList<ItemStack>());
        }
        return (ArrayList<ItemStack>) player.getExtraData("merchantSold");
    }

    private ItemStack createBuyBack(SkyblockPlayer player) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        formatter.setGroupingUsed(true);
        if (getSold(player).size() > 0) {
            ItemStack lastSold = getSold(player).get(getSold(player).size() - 1).clone();
            NBTItem nbt = new NBTItem(lastSold);
            return new ItemBuilder(nbt.getItem()).addLore(
                            Arrays.asList(Util.buildLore("\n&7Cost\n&6"
                                    + formatter.format(nbt.getDouble("merchantCost"))
                                    + " &6coins\n\n&eClick to buyback!")))
                    .toItemStack();
        } else return new ItemBuilder(ChatColor.GREEN + "Sell Item", Material.HOPPER)
                .setLore(Util.buildLore("&7Click items in your inventory to\n&7sell them to this shop!"))
                .toItemStack();
    }

    public void createNpc() {
        List<Object> npcData = Util.spawnSkyblockNpc(this.location, this.name, this.skinValue, this.skinSignature, true, true, this.villager, this.profession);

        this.npc = (NPC) npcData.get(0);

        npc.getEntity().setMetadata("merchant", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), true));
        npc.getEntity().setMetadata("merchantName", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), name));
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.npc)) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getClicker());

        if (player == null) return;

        if ((boolean) player.getValue("merchant." + this.getName().toLowerCase().replace(" ", "_") + ".interacting"))
            return;

        if (!((boolean) player.getValue("merchant." + this.getName().toLowerCase().replace(" ", "_") + ".interacted")) && this.initialDialogue.size() > 0) {
            int delay = 0;

            player.setValue("merchant." + this.getName().toLowerCase().replace(" ", "_") + ".interacting", true);

            for (String s : this.initialDialogue) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getBukkitPlayer().sendMessage(ChatColor.YELLOW + "[NPC] " + getName() + ChatColor.WHITE + ": " + s);

                        if (interactionIteration >= initialDialogue.size() - 1) {
                            player.setValue("merchant." + getName().toLowerCase().replace(" ", "_") + ".interacted", true);
                            player.setValue("merchant." + getName().toLowerCase().replace(" ", "_") + ".interacting", false);
                        }

                        interactionIteration += 1;

                        player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.VILLAGER_YES, 1, 1);
                    }
                }.runTaskLater(Skyblock.getPlugin(Skyblock.class), delay);

                delay += 40;
            }

            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 54, this.name);

        Util.fillBorder(inventory);

        DecimalFormat formatter = new DecimalFormat("#,###");
        formatter.setGroupingUsed(true);

        for (MerchantItem item : this.items) {
            ItemStack stack = item.getItem().clone();

            if (item.isTrade()) continue; // TODO: Implement trades


            if (stack.getItemMeta().getLore().stream().noneMatch(l -> l.contains("Right-Click for more trading options!"))) {
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = meta.getLore();
                lore.addAll(Arrays.asList(Util.buildLore("\n&7Cost\n&6" + formatter.format(item.getCost()) + " &6coins\n\n&eClick to trade!\n&eRight-Click for more trading options!")));
                meta.setLore(lore);
                stack.setItemMeta(meta);
            }

            NBTItem nbt = new NBTItem(stack);

            nbt.setBoolean("merchantItem", true);
            nbt.setString("merchantName", this.name);
            nbt.setInteger("merchantCost", item.getCost());
            nbt.setString("merchantReward", item.getRewardCommand());

            inventory.addItem(nbt.getItem());
        }

        inventory.setItem(49, createBuyBack(player));
        player.getBukkitPlayer().openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(this.name)) return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        ItemStack item = event.getCurrentItem();

        event.setCancelled(true);

        NBTItem nbt = new NBTItem(item);

        SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getWhoClicked());

        if (player == null) return;

        DecimalFormat formatter = new DecimalFormat("#,###");
        formatter.setGroupingUsed(true);

        if (nbt.hasKey("merchantItem") || nbt.hasKey("merchantSold")) {
            if (event.isRightClick() && nbt.hasKey("merchantItem")) {
                Gui gui = new Gui("Shop Trading Options", 54, new HashMap<>());

                Util.fillEmpty(gui);
                gui.addItem(49, Util.buildCloseButton());
                gui.addItem(48, Util.buildBackButton("&7To " + this.name));

                gui.addItem(20, buildShopOption(nbt.getItem(), 1, player, gui));
                gui.addItem(21, buildShopOption(nbt.getItem(), 5, player, gui));
                gui.addItem(22, buildShopOption(nbt.getItem(), 10, player, gui));
                gui.addItem(23, buildShopOption(nbt.getItem(), 32, player, gui));
                gui.addItem(24, buildShopOption(nbt.getItem(), 64, player, gui));

                gui.getClickEvents().put(ChatColor.GREEN + "Go Back", () -> {
                    player.getBukkitPlayer().openInventory(event.getInventory());
                });

                gui.show(player.getBukkitPlayer());

                return;
            }

            if (player.getDouble("stats.purse") < nbt.getInteger("merchantCost")) {
                player.getBukkitPlayer().sendMessage(ChatColor.RED + "You do not have enough coins to purchase this item!");
                return;
            }

            player.setValue("stats.purse", player.getDouble("stats.purse") - nbt.getInteger("merchantCost"));

            if (nbt.hasKey("merchantItem")) {
                player.getBukkitPlayer().performCommand(nbt.getString("merchantReward"));

                player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You have purchased "
                        + item.getItemMeta().getDisplayName() + ChatColor.GREEN + " for "
                        + ChatColor.GOLD + formatter.format(nbt.getInteger("merchantCost")) + " coins" + ChatColor.GREEN + "!");
            } else {
                nbt = new NBTItem((ItemStack) getSold(player).get(getSold(player).size() - 1));
                nbt.setBoolean("merchantSold", null);
                player.getBukkitPlayer().getInventory().addItem(nbt.getItem());
                player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You have bought back " + item.getItemMeta().getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + formatter.format(nbt.getInteger("merchantCost")) + " coins" + ChatColor.GREEN + "!");
                getSold(player).remove(getSold(player).size() - 1);
                event.getInventory().setItem(49, createBuyBack(player));

            }

            player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 10, 2);
        } else if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
            double price = Skyblock.getPlugin().getMerchantHandler().getPriceHandler().getPrice(item);
            player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You have sold " + item.getItemMeta().getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + Util.formatInt((int) price) + " coins" + ChatColor.GREEN + "!");
            player.addCoins(price);
            nbt = new NBTItem(item);
            nbt.setDouble("merchantCost", price);
            nbt.setBoolean("merchantSold", true);
            getSold(player).add(nbt.getItem());
            event.getInventory().setItem(49, createBuyBack(player));
            event.setCurrentItem(null);
            player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 10, 2);
        }
    }

    private ItemStack buildShopOption(ItemStack item, int amount, SkyblockPlayer player, Gui gui) {
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        List<String> lore = meta.getLore();

        NBTItem nbt = new NBTItem(clone);

        AtomicReference<String> command = new AtomicReference<>(nbt.getString("merchantReward"));
        int cost = nbt.getInteger("merchantCost");
        int num = 0;

        try {
            num = Integer.parseInt(command.get().split(" ")[3]);
            command.set(command.get().replace(" " + num, ""));
        } catch (NumberFormatException e) {
            num = 1;
        }

        int costForOne = (int) Math.ceil((1.0 * cost / num));

        meta.setDisplayName(meta.getDisplayName() + " " + ChatColor.DARK_GRAY + "x" + amount);

        for (int i = 0; i < 7; i++) {
            lore.remove(lore.size() - 1);
        }

        lore.add(" ");
        lore.add(ChatColor.GRAY + "Cost");
        lore.add(ChatColor.GOLD + "" + (costForOne * amount) + " coins");
        lore.add(" ");
        lore.add(ChatColor.YELLOW + "Click to purchase!");

        meta.setLore(lore);
        clone.setItemMeta(meta);

        clone.setAmount(amount);

        gui.getClickEvents().put(meta.getDisplayName(), () -> {
            if (player.checkCoins(costForOne * amount)) {
                DecimalFormat formatter = new DecimalFormat("#,###");
                formatter.setGroupingUsed(true);

                player.subtractCoins(costForOne * amount);

                player.getBukkitPlayer().performCommand(command.get() + " " + amount);

                player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You have purchased " + meta.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + formatter.format((long) costForOne * amount) + " coins" + ChatColor.GREEN + "!");

                player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 10, 2);
            }
        });

        return clone;
    }

}
