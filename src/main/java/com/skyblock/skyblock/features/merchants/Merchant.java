package com.skyblock.skyblock.features.merchants;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Data;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

@Data
public class Merchant implements Listener {

    private final String name;
    private final String skin;

    private NPC npc;
    private ArmorStand stand;
    private NBTEntity nbtas;
    private ArmorStand click;
    private NBTEntity nbtEntity;
    private LookClose lookClose;
    private SkinTrait skinTrait;

    private final List<MerchantItem> items;
    private final Location location;

    protected Merchant(String name, String skin, List<MerchantItem> items, Location location) {
        this.name = name;
        this.skin = skin;

        this.items = items;

        this.location = location;
    }

    public void createNpc() {
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.YELLOW + "" + ChatColor.BOLD + "CLICK");
        this.npc.spawn(location);

        this.npc.getEntity().setCustomNameVisible(false);

        this.npc.getEntity().setMetadata("merchant", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), true));
        this.npc.getEntity().setMetadata("merchantName", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), this.name));

        this.stand = npc.getEntity().getWorld().spawn(npc.getEntity().getLocation().add(0, 1.95, 0), ArmorStand.class);
        this.stand.setGravity(false);
        this.stand.setVisible(false);
        this.stand.setCustomNameVisible(true);
        this.stand.setCustomName(this.name);

        this.nbtas = new NBTEntity(this.stand);
        this.nbtas.setBoolean("Invisible", true);
        this.nbtas.setBoolean("Gravity", false);
        this.nbtas.setBoolean("CustomNameVisible", true);
        this.nbtas.setBoolean("Marker", true);
        this.nbtas.setBoolean("Invulnerable", true);

        this.stand.teleport(this.npc.getEntity().getLocation().add(0, 1.95, 0));
        this.stand.setMetadata("merchant", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), true));
        this.stand.setMetadata("merchantName", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), this.name));
        this.stand.setMetadata("NPC", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), true));

        this.click = this.npc.getEntity().getWorld().spawn(this.npc.getEntity().getLocation().add(0, 1.7, 0), ArmorStand.class);
        this.click.setCustomName(ChatColor.YELLOW + "" + ChatColor.BOLD + "CLICK");
        this.click.setGravity(false);
        this.click.setVisible(false);
        this.click.setCustomNameVisible(true);
        this.nbtEntity = new NBTEntity(this.click);
        this.nbtEntity.setBoolean("Invisible", true);
        this.nbtEntity.setBoolean("Gravity", false);
        this.nbtEntity.setBoolean("CustomNameVisible", true);
        this.nbtEntity.setBoolean("Marker", true);
        this.nbtEntity.setBoolean("Invulnerable", true);

        this.click.teleport(this.npc.getEntity().getLocation().add(0, 1.7, 0));

        this.lookClose = this.npc.getTrait(LookClose.class);
        this.lookClose.lookClose(true);

        this.skinTrait = this.npc.getTrait(SkinTrait.class);
        this.skinTrait.setSkinName(this.skin);

        this.npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);

        this.npc.addTrait(lookClose);
        this.npc.addTrait(skinTrait);
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.npc)) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getClicker());

        if (player == null) return;

        Inventory inventory = Bukkit.createInventory(null, 54, this.name);

        Util.fillBorder(inventory);

        for (MerchantItem item : this.items) {
            ItemStack stack = item.getItem();

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

            player.setValue("stats.purse", (int) player.getValue("stats.purse") - nbt.getInteger("merchantCost"));

            player.getBukkitPlayer().performCommand(nbt.getString("merchantReward"));

            player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You have purchased " + item.getItemMeta().getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + nbt.getInteger("merchantCost") + " coins" + ChatColor.GREEN + "!");
        }
    }

}
