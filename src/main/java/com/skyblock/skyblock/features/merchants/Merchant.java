package com.skyblock.skyblock.features.merchants;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Data;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

@Data
public class Merchant implements Listener {

    private final String name;

    private final String skinSignature;
    private final String skinValue;

    private final List<String> initialDialogue;

    private NPC npc;
    private ArmorStand stand;
    private ArmorStand click;

    private final List<MerchantItem> items;
    private final Location location;

    protected Merchant(String name, String skinValue, String skinSignature, List<MerchantItem> items, Location location, List<String> initialDialogue) {
        this.name = name;

        this.skinSignature = skinSignature;
        this.skinValue = skinValue;

        this.items = items;

        this.location = location;

        this.initialDialogue = initialDialogue;
    }

    public void createNpc() {
        List<Object> npcData = Util.spawnSkyblockNpc(this.location, this.name, this.skinValue, this.skinSignature, true, true, false, null);

        this.npc = (NPC) npcData.get(0);
        this.stand = (ArmorStand) npcData.get(1);
        this.click = (ArmorStand) npcData.get(2);
    }

    private int interactionIteration = 0;

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.npc)) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getClicker());

        if (player == null) return;

        if ((boolean) player.getValue("merchant." + this.getName().toLowerCase().replace(" ", "_") + ".interacting")) return;

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

        for (MerchantItem item : this.items) {
            ItemStack stack = item.getItem();

            if (item.isTrade()) continue; // TODO: Implement trades

            DecimalFormat formatter = new DecimalFormat("#,###");
            formatter.setGroupingUsed(true);

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

        inventory.setItem(49, new ItemBuilder(ChatColor.GREEN + "Sell Item", Material.HOPPER).setLore(Util.buildLore("&7Click items in your inventory to\n&7sell them to this shop!")).toItemStack());

        player.getBukkitPlayer().openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(this.name)) return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        ItemStack item = event.getCurrentItem();

        event.setCancelled(true);

        NBTItem nbt = new NBTItem(item);

        if (nbt.hasKey("merchantItem")) {
            SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getWhoClicked());

            if (player == null) return;

            if ((int) player.getValue("stats.purse") < nbt.getInteger("merchantCost")) {
                player.getBukkitPlayer().sendMessage(ChatColor.RED + "You do not have enough coins to purchase this item!");
                return;
            }

            DecimalFormat formatter = new DecimalFormat("#,###");
            formatter.setGroupingUsed(true);

            player.setValue("stats.purse", (int) player.getValue("stats.purse") - nbt.getInteger("merchantCost"));

            player.getBukkitPlayer().performCommand(nbt.getString("merchantReward"));

            player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You have purchased " + item.getItemMeta().getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + formatter.format(nbt.getInteger("merchantCost")) + " coins" + ChatColor.GREEN + "!");
        }
    }

}
