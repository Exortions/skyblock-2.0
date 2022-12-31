package com.skyblock.skyblock.features.auction.display;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.utilities.Util;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

@Getter
public class Display {

    private Auction currentAuction = null;
    private ItemStack previousItem = null;
    private NPC item = null;

    private final Location location;
    private final int rank;

    public Display(int x, int y, int z, int rank) {
        this.location = new Location(Skyblock.getSkyblockWorld(), x, y, z);
        this.rank = rank;

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARMOR_STAND, "", this.location.clone().add(0.5, -0.5, 0.5));
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        ArmorStandTrait cage = npc.getOrAddTrait(ArmorStandTrait.class);
        Equipment equipment = npc.getOrAddTrait(Equipment.class);

        cage.setGravity(false);
        cage.setVisible(false);
        cage.setMarker(true);
        equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.GLASS));
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

    public void update(Auction auction) {
        this.currentAuction = auction;

        updateSign(location.clone().add(0, 0, 1), auction);
        updateSign(location.clone().add(0, 0, -1), auction);
        updateSign(location.clone().add(1, 0, 0), auction);
        updateSign(location.clone().add(-1, 0, 0), auction);

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
    }

    public void updateItemPosition() {
        if (item != null) item.teleport(location.clone().add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean isPossibleSignLocation(Location loc) {
        return loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ() + 1 ||
                loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ() - 1 ||
                loc.getBlockX() == location.getBlockX() + 1 && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ() ||
                loc.getBlockX() == location.getBlockX() - 1 && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ();
    }

}
