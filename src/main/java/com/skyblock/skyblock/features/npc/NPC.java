package com.skyblock.skyblock.features.npc;

import com.skyblock.skyblock.Skyblock;
import de.tr7zw.nbtapi.NBTEntity;
import lombok.Data;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.function.Consumer;

@Data
public class NPC implements Listener {

    private final String name;

    private final boolean doesLookClose;
    private final boolean hasSkin;

    private final Location location;

    private final Consumer<Player> action;

    private final String skinValue;
    private final String skinSignature;

    private net.citizensnpcs.api.npc.NPC npc;
    private ArmorStand stand;
    private NBTEntity nbtas;
    private ArmorStand click;
    private NBTEntity nbtEntity;
    private LookClose lookClose;
    private SkinTrait skinTrait;

    public void spawn() {
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.YELLOW + "" + ChatColor.BOLD + "CLICK");
        this.npc.spawn(location);

        this.npc.getEntity().setCustomNameVisible(false);

        this.npc.getEntity().setMetadata("isSkyblockNpc", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), true));
        this.npc.getEntity().setMetadata("npcName", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), name));

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
        this.lookClose.lookClose(this.doesLookClose);

        this.skinTrait = this.npc.getTrait(SkinTrait.class);
        this.skinTrait.setTexture(this.skinValue, this.skinSignature);

        this.npc.data().set(net.citizensnpcs.api.npc.NPC.NAMEPLATE_VISIBLE_METADATA, false);

        this.npc.addTrait(lookClose);

        if (this.hasSkin) this.npc.addTrait(skinTrait);
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.npc)) return;

        Player player = event.getClicker();

        this.action.accept(player);
    }

}
