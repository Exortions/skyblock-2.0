package com.skyblock.skyblock.features.auction.display;

import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.utilities.Util;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

@Getter
public class Display {

    private final Location location;
    private final int rank;

    public Display(int x, int y, int z, int rank) {
        this.location = new Location(Skyblock.getSkyblockWorld(), x, y, z);
        this.rank = rank;

        ArmorStand cage = (ArmorStand) this.location.getWorld().spawnEntity(this.location.clone().add(0.5, -0.5, 0.5), EntityType.ARMOR_STAND);
        cage.setGravity(false);
        cage.setVisible(false);
        cage.setMarker(true);
        cage.setCustomNameVisible(false);
        cage.setHelmet(new ItemStack(Material.GLASS));
    }

    private void updateSign(Location loc, Auction auction) {
        if (loc.getBlock().getState() instanceof Sign) {
            Sign sign = (Sign) loc.getBlock().getState();

            sign.setLine(0, ChatColor.GOLD + "" + ChatColor.BOLD + Util.abbreviate(auction.getPrice()) + " coins");
            sign.setLine(1, ChatColor.GREEN + "" + auction.getBidHistory().size() + " bids");
            sign.setLine(2, ChatColor.DARK_AQUA + "" + Util.formatTimeLeft(auction.getTimeLeft()));
            sign.setLine(3, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "[CLICK]");

            sign.update();
        }
    }

    private ItemStack previousItem = null;
//    private Item item = null;
    private NPC item = null;

    public void update(Auction auction) {
        updateSign(location.clone().add(0, 0, 1), auction);
        updateSign(location.clone().add(0, 0, -1), auction);
        updateSign(location.clone().add(1, 0, 0), auction);
        updateSign(location.clone().add(-1, 0, 0), auction);

//        if (previousItem != auction.getItem()) {
//            if (this.item != null) item.remove();
//
//            item = location.getWorld().dropItem(location.clone().add(0, 1, 0), auction.getItem());
//            item.setPickupDelay(Integer.MAX_VALUE);
//            item.setCustomNameVisible(true);
//            item.setCustomName(auction.getItem().getItemMeta().getDisplayName());
//
////            Skyblock.getPlugin().addRemoveable(item);
//
//            previousItem = auction.getItem();
//        }
//
//        if (item != null) item.teleport(location.clone().add(0.5, 1, 0.5));

        if (previousItem != auction.getItem()) {
            if (this.item != null) item.despawn();

            item = CitizensAPI.getNPCRegistry().createNPC(EntityType.DROPPED_ITEM, auction.getItem().getItemMeta().getDisplayName(), location.clone().add(0.5, 1, 0.5));
            item.spawn(location.clone().add(0.5, 1, 0.5));
            Item entity = (Item) item.getEntity();
            entity.setItemStack(auction.getItem());
            entity.setPickupDelay(Integer.MAX_VALUE);
            entity.setCustomNameVisible(true);
            entity.setCustomName(auction.getItem().getItemMeta().getDisplayName());

            previousItem = auction.getItem();
        }

        if (item != null) item.teleport(location.clone().add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

}
